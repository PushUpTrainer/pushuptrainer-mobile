import AVFoundation
import MediaPipeTasksVision
import Shared
import UIKit

final class PoseCameraService: NSObject, PoseSource, AVCaptureVideoDataOutputSampleBufferDelegate, PoseLandmarkerLiveStreamDelegate {
    let session = AVCaptureSession()

    private let sessionQueue = DispatchQueue(label: "com.example.pushuptrainer_mobile.ios.camera.session")
    private let frameQueue = DispatchQueue(label: "com.example.pushuptrainer_mobile.ios.camera.frames")
    private let output = AVCaptureVideoDataOutput()
    private let frameLock = NSLock()
    private let pointIndices = [11, 12, 13, 14, 15, 16, 23, 24, 25, 26, 27, 28]
    private var frameDimensions: [Int: (Int32, Int32, Int)] = [:]
    private var frameTimestamps: [Int] = []
    private var listener: PoseListener?
    private var readyListener: (() -> Void)?
    private var errorListener: ((String) -> Void)?
    private var landmarker: PoseLandmarker?
    private var configured = false
    private var lastTimestamp = 0
    private var generation = 0
    private var activeGeneration: Int?
    private var frameGate: PoseFrameGate?

    override init() {
        super.init()
        NotificationCenter.default.addObserver(self, selector: #selector(didEnterBackground), name: UIApplication.didEnterBackgroundNotification, object: nil)
    }

    @objc private func didEnterBackground() {
        stop()
    }

    func start(listener: PoseListener) {
        self.listener = listener
        resume()
    }

    func setErrorListener(listener: @escaping (String) -> Void) {
        errorListener = listener
    }

    func setReadyListener(listener: @escaping () -> Void) {
        readyListener = listener
    }

    func resume() {
        frameLock.lock()
        guard activeGeneration == nil else {
            frameLock.unlock()
            return
        }
        generation += 1
        let current = generation
        activeGeneration = current
        frameGate = nil
        frameLock.unlock()
        switch AVCaptureDevice.authorizationStatus(for: .video) {
        case .authorized:
            startSession(generation: current)
        case .notDetermined:
            AVCaptureDevice.requestAccess(for: .video) { [weak self] allowed in
                DispatchQueue.main.async {
                    guard let self, self.isActive(current) else { return }
                    if allowed {
                        self.startSession(generation: current)
                    } else {
                        self.reportError("Разрешите доступ к камере в настройках", generation: current)
                    }
                }
            }
        default:
            reportError("Разрешите доступ к камере в настройках", generation: current)
        }
    }

    func stop() {
        frameLock.lock()
        activeGeneration = nil
        frameGate = nil
        frameDimensions.removeAll()
        frameTimestamps.removeAll()
        frameLock.unlock()
        listener = nil
        errorListener = nil
        readyListener = nil
        sessionQueue.async { [self] in
            if session.isRunning { session.stopRunning() }
        }
    }

    private func startSession(generation current: Int) {
        sessionQueue.async { [weak self] in
            guard let self, self.isActive(current) else { return }
            do {
                if !self.configured {
                    try self.configure()
                }
                guard self.isActive(current) else { return }
                if !self.session.isRunning {
                    self.session.startRunning()
                }
                guard self.session.isRunning else { throw CameraError.cameraUnavailable }
                DispatchQueue.main.async { [weak self] in
                    guard let self else { return }
                    self.frameLock.lock()
                    guard self.activeGeneration == current, self.frameGate == nil else {
                        self.frameLock.unlock()
                        return
                    }
                    self.frameGate = PoseFrameGate(readyAtNanos: self.hostTimeNanos())
                    self.frameLock.unlock()
                    self.readyListener?()
                }
            } catch {
                self.reportError(error.localizedDescription, generation: current)
            }
        }
    }

    private func configure() throws {
        guard let modelPath = Bundle.main.path(forResource: "pose_landmarker_full", ofType: "task") else {
            throw CameraError.modelMissing
        }

        let options = PoseLandmarkerOptions()
        options.baseOptions.modelAssetPath = modelPath
        options.runningMode = .liveStream
        options.poseLandmarkerLiveStreamDelegate = self
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
                connection.isVideoMirrored = false
            }
        }

        configured = true
    }

    func captureOutput(_ output: AVCaptureOutput, didOutput sampleBuffer: CMSampleBuffer, from connection: AVCaptureConnection) {
        let presentationTime = CMSampleBufferGetPresentationTimeStamp(sampleBuffer)
        guard presentationTime.isNumeric, let clock = session.synchronizationClock else { return }
        let captureTime = CMSyncConvertTime(presentationTime, from: clock, to: CMClockGetHostTimeClock())
        guard captureTime.isNumeric else { return }
        let capturedAtNanos = CMTimeConvertScale(captureTime, timescale: 1_000_000_000, method: .default).value
        frameLock.lock()
        guard let current = activeGeneration,
              frameGate?.allows(capturedAtNanos: capturedAtNanos, nowNanos: hostTimeNanos()) == true else {
            frameLock.unlock()
            return
        }
        frameLock.unlock()
        guard let landmarker, let pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer) else { return }

        let width = Int32(CVPixelBufferGetWidth(pixelBuffer))
        let height = Int32(CVPixelBufferGetHeight(pixelBuffer))
        let timestamp = max(Int(CMTimeGetSeconds(presentationTime) * 1000), lastTimestamp + 1)
        lastTimestamp = timestamp

        do {
            let image = try MPImage(sampleBuffer: sampleBuffer, orientation: .up)
            frameLock.lock()
            guard activeGeneration == current else {
                frameLock.unlock()
                return
            }
            frameDimensions[timestamp] = (width, height, current)
            frameTimestamps.append(timestamp)
            if frameTimestamps.count > 120 {
                frameDimensions.removeValue(forKey: frameTimestamps.removeFirst())
            }
            defer { frameLock.unlock() }
            try landmarker.detectAsync(image: image, timestampInMilliseconds: timestamp)
        } catch {
            frameLock.lock()
            frameDimensions.removeValue(forKey: timestamp)
            frameLock.unlock()
            reportError(error.localizedDescription, generation: current)
        }
    }

    func poseLandmarker(_ poseLandmarker: PoseLandmarker, didFinishDetection result: PoseLandmarkerResult?, timestampInMilliseconds timestamp: Int, error: (any Error)?) {
        frameLock.lock()
        let dimensions = frameDimensions.removeValue(forKey: timestamp)
        frameLock.unlock()
        guard let dimensions else { return }
        let current = dimensions.2
        guard isActive(current) else { return }
        if let error {
            reportError(error.localizedDescription, generation: current)
            return
        }

        let points: [Shared.Landmark]?
        if let pose = result?.landmarks.first, pose.count > 28 {
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
            imageWidth: dimensions.0,
            imageHeight: dimensions.1,
            landmarks: points
        )
        DispatchQueue.main.async { [weak self] in
            guard let self, self.isActive(current) else { return }
            self.listener?.onFrame(frame_: frame)
        }
    }

    private func hostTimeNanos() -> Int64 {
        CMTimeConvertScale(CMClockGetTime(CMClockGetHostTimeClock()), timescale: 1_000_000_000, method: .default).value
    }

    private func isActive(_ current: Int) -> Bool {
        frameLock.lock()
        defer { frameLock.unlock() }
        return activeGeneration == current
    }

    private func reportError(_ value: String, generation current: Int) {
        DispatchQueue.main.async { [weak self] in
            guard let self, self.isActive(current) else { return }
            self.errorListener?(value)
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
