import AVFoundation
import UIKit
import Shared

final class WorkoutCameraView: UIView, IosWorkoutCamera {
    private let camera = PoseCameraService()
    private let preview = AVCaptureVideoPreviewLayer()

    var view: UIView { self }

    init() {
        super.init(frame: .zero)
        preview.session = camera.session
        preview.videoGravity = .resizeAspectFill
        layer.addSublayer(preview)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        preview.frame = bounds
    }

    func start(listener_ listener: WorkoutCameraListener) {
        camera.setErrorListener { _ in listener.onError() }
        camera.setReadyListener { [weak self] in
            if let connection = self?.preview.connection {
                if connection.isVideoOrientationSupported { connection.videoOrientation = .portrait }
                if connection.isVideoMirroringSupported {
                    connection.automaticallyAdjustsVideoMirroring = false
                    connection.isVideoMirrored = true
                }
            }
            listener.onReady()
        }
        camera.start(listener: listener)
    }

    func stop() {
        camera.stop()
    }
}
