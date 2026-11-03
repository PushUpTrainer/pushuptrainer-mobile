import Shared
import SwiftUI

struct ContentView: View {
    @Environment(\.scenePhase) private var scenePhase
    @StateObject private var camera = PoseCameraService()

    var body: some View {
        ComposeWorkoutView(camera: camera)
            .ignoresSafeArea()
            .onChange(of: scenePhase) { _, phase in
                if phase == .active {
                    camera.resume()
                } else {
                    camera.stop()
                }
            }
    }
}

private struct ComposeWorkoutView: UIViewControllerRepresentable {
    let camera: PoseCameraService

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(source: camera)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
