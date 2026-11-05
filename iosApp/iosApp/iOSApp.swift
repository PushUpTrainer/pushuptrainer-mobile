import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        IosKoinKt.doInitKoinIos()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
