import AVFoundation
import SwiftUI

struct ContentView: View {
    @Environment(\.scenePhase) private var scenePhase
    @StateObject private var camera = PoseCameraService()

    var body: some View {
        ZStack(alignment: .top) {
            CameraPreviewView(session: camera.session)
                .ignoresSafeArea()

            VStack(spacing: 8) {
                Text("Отжимания: \(camera.reps)")
                    .font(.title.bold())
                Text(camera.status)
                    .font(.body)
            }
            .foregroundStyle(.white)
            .padding(16)
            .background(.black.opacity(0.65), in: RoundedRectangle(cornerRadius: 16))
            .padding(.top, 20)
        }
        .onAppear { camera.start() }
        .onDisappear { camera.stop() }
        .onChange(of: scenePhase) { _, phase in
            if phase == .active {
                camera.start()
            } else {
                camera.stop()
            }
        }
    }
}

private struct CameraPreviewView: UIViewRepresentable {
    let session: AVCaptureSession

    func makeUIView(context: Context) -> CameraPreview {
        let view = CameraPreview()
        view.previewLayer.session = session
        view.previewLayer.videoGravity = .resizeAspectFill
        return view
    }

    func updateUIView(_ uiView: CameraPreview, context: Context) {}
}

private final class CameraPreview: UIView {
    override class var layerClass: AnyClass { AVCaptureVideoPreviewLayer.self }

    var previewLayer: AVCaptureVideoPreviewLayer {
        layer as! AVCaptureVideoPreviewLayer
    }
}
