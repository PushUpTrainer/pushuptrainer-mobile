import AVFoundation
import Combine
import MediaPipeTasksVision
import Shared
import UIKit

final class PoseCameraService: NSObject, ObservableObject, IosCameraSource, AVCaptureVideoDataOutputSampleBufferDelegate {
    let session = AVCaptureSession()

    private let sessionQueue = DispatchQueue(label: "app.pushups.ios.camera.session")
    private let frameQueue = DispatchQueue(label: "app.pushups.ios.camera.frames")
    private let output = AVCaptureVideoDataOutput()
    private let pointIndices = [11, 12, 13, 14, 15, 16, 23, 24, 25, 26, 27, 28]
    private var listener: PoseListener?
    private var errorListener: ((String) -> Void)?
    private var landmarker: PoseLandmarker?
    private var configured = false
    private var lastTimestamp = 0
    private var shouldRun = false

    func start(listener: PoseListener) {
        self.listener = listener
        resume()
    }

    func setErrorListener(listener: @escaping (String) -> Void) {
        errorListener = listener
    }

    func resume() {
        shouldRun = true
        switch AVCaptureDevice.authorizationStatus(for: .video) {
        case .authorized:
            startSession()
        case .notDetermined:
            AVCaptureDevice.requestAccess(for: .video) { [weak self] allowed in
                DispatchQueue.main.async {
                    guard let self, self.shouldRun else { return }
                    if allowed {
                        self.startSession()
                    } else {
                        self.reportError("Разрешите доступ к камере в настройках")
                    }
                }
            }
        default:
            reportError("Разрешите доступ к камере в настройках")
        }
    }

    func stop() {
        shouldRun = false
        sessionQueue.async { [weak self] in
            guard let self, self.session.isRunning else { return }
            self.session.stopRunning()
        }
    }

    private func startSession() {
        sessionQueue.async { [weak self] in
            guard let self else { return }
            do {
                if !self.configured {
                    try self.configure()
                }
                if !self.session.isRunning {
                    self.session.startRunning()
                }
                guard self.session.isRunning else { throw CameraError.cameraUnavailable }
            } catch {
                self.reportError(error.localizedDescription)
            }
        }
    }

    private func configure() throws {
        guard let modelPath = Bundle.main.path(forResource: "pose_landmarker_full", ofType: "task") else {
            throw CameraError.modelMissing
        }

        let options = PoseLandmarkerOptions()
        options.baseOptions.modelAssetPath = modelPath
        options.runningMode = .video
        options.numPoses = 1
        options.minPoseDetectionConfidence = 0.5
        options.minPosePresenceConfidence = 0.5
        options.minTrackingConfidence = 0.5
        landmarker = try PoseLandmarker(options: options)

        guard let device = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: .front) else {
            throw CameraError.cameraMissing
        }

        let input = try AVCaptureDeviceInput(device: device)
        session.beginConfiguration()
        defer { session.commitConfiguration() }
        session.sessionPreset = .high

        guard session.canAddInput(input) else { throw CameraError.cameraUnavailable }
        session.addInput(input)

        output.videoSettings = [kCVPixelBufferPixelFormatTypeKey as String: kCVPixelFormatType_32BGRA]
        output.alwaysDiscardsLateVideoFrames = true
        output.setSampleBufferDelegate(self, queue: frameQueue)
        guard session.canAddOutput(output) else { throw CameraError.cameraUnavailable }
        session.addOutput(output)

        if let connection = output.connection(with: .video) {
            if connection.isVideoOrientationSupported {
                connection.videoOrientation = .portrait
            }
            if connection.isVideoMirroringSupported {
                connection.automaticallyAdjustsVideoMirroring = false
                connection.isVideoMirrored = true
            }
        }

        configured = true
    }

    func captureOutput(_ output: AVCaptureOutput, didOutput sampleBuffer: CMSampleBuffer, from connection: AVCaptureConnection) {
        guard let landmarker, let pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer) else { return }

        let orientation: UIImage.Orientation
        switch UIDevice.current.orientation {
        case .landscapeLeft:
            orientation = .left
        case .landscapeRight:
            orientation = .right
        default:
            orientation = .up
        }

        let rawWidth = CVPixelBufferGetWidth(pixelBuffer)
        let rawHeight = CVPixelBufferGetHeight(pixelBuffer)
        let rotated = orientation == .left || orientation == .right
        let width = rotated ? rawHeight : rawWidth
        let height = rotated ? rawWidth : rawHeight
        let timestamp = max(Int(ProcessInfo.processInfo.systemUptime * 1000), lastTimestamp + 1)
        lastTimestamp = timestamp

        do {
            let image = try MPImage(sampleBuffer: sampleBuffer, orientation: orientation)
            let result = try landmarker.detect(videoFrame: image, timestampInMilliseconds: timestamp)
            let points: [Shared.Landmark]?
            if let pose = result.landmarks.first, pose.count > 28 {
                points = pointIndices.map { index in
                    let point = pose[index]
                    return Shared.Landmark(
                        x: point.x,
                        y: point.y,
                        z: point.z,
                        visibility: point.visibility?.floatValue ?? 0,
                        presence: point.presence?.floatValue ?? 0
                    )
                }
            } else {
                points = nil
            }

            let frame = PoseFrame(
                timestampMs: Int64(timestamp),
                imageWidth: Int32(width),
                imageHeight: Int32(height),
                landmarks: points
            )
            DispatchQueue.main.async { [weak self] in
                self?.listener?.onFrame(frame: frame)
            }
        } catch {
            reportError(error.localizedDescription)
        }
    }

    private func reportError(_ value: String) {
        DispatchQueue.main.async { [weak self] in
            self?.errorListener?(value)
        }
    }
}

private enum CameraError: LocalizedError {
    case modelMissing
    case cameraMissing
    case cameraUnavailable

    var errorDescription: String? {
        switch self {
        case .modelMissing: return "Модель позы не найдена"
        case .cameraMissing: return "Фронтальная камера не найдена"
        case .cameraUnavailable: return "Камера недоступна"
        }
    }
}
