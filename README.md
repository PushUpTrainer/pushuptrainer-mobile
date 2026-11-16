This is a Kotlin Multiplatform project targeting Android, iOS.

* [/iosApp](./iosApp/iosApp) contains an iOS application. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
    folder is the appropriate location.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- iOS app: open the [/iosApp](./iosApp) directory in Xcode and run it from there.

Android uses CameraX and MediaPipe Pose Landmarker. iOS uses AVFoundation and MediaPipe Pose Landmarker. Both platform adapters feed the same 12 pose points into the shared workout counter. Preparation opens a goal-based or free workout; reaching the goal or finishing manually saves a nonzero result in Room. Workouts with zero repetitions return to preparation without saving. Backgrounding pauses the camera and counting until Continue is selected. Back asks to confirm finishing. The camera preview displays visible pose points and their connections. Both platforms use MediaPipe 1.1.1.

Progress displays a monthly calendar, the selected day's workout and repetition counts, total repetitions, and the best workout. Home and Progress exclude legacy zero-repetition sessions from their summaries.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Android tests: `./gradlew :shared:testAndroidHostTest`
- iOS tests: `./gradlew :shared:iosSimulatorArm64Test`

The Android host test replays 2,728 recorded frames against the Python reference output. Live counting and skeleton alignment still need validation with a person on a physical device. The current reference counter does not validate straight legs or an initial exercise position; setup movements and knee push-ups remain an ML limitation.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
