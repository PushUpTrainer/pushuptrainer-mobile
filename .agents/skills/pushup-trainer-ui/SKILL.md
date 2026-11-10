---
name: pushup-trainer-ui
description: Local Kotlin Multiplatform UI/presentation rules for PushUp Trainer: shared Compose Multiplatform UI, lightweight MVI, UiState/UiEvent, mandatory UI models, UI kit, decomposition, shared resources, and Compose Previews.
---

# PushUp Trainer UI Architecture

This skill defines local UI/presentation rules **for PushUp Trainer**, a Kotlin Multiplatform project with a shared Compose Multiplatform UI. It applies to the shared UI/presentation layer and to platform-specific UI integration where explicitly required. It is a project-local rule set, not a universal Kotlin Multiplatform standard, and must not be automatically applied to other projects.

## 1. Lightweight MVI

Each screen follows a unidirectional flow:

```text
UiState -> UI -> UiEvent -> ViewModel -> new UiState
```

Rules:

- the screen receives its state through `UiState`;
- user actions are represented as `UiEvent`;
- the only public entry point for actions in the ViewModel is `onEvent(event)`;
- the UI must not call separate ViewModel methods such as `submit()`, `delete()`, `select()`, `load()`, `togglePassword()`, etc.;
- the ViewModel may update `UiState` directly;
- a separate reducer/store is not required unless the task explicitly requires one.

Preferred screen boundary:

```kotlin
Screen(
    state = state,
    onEvent = viewModel::onEvent,
)
```

Nested components receive narrow callbacks (`onClick`, `onValueChange`, etc.), while the screen maps them to the corresponding `UiEvent`.

## 2. Strict presentation/domain boundary

Compose UI and UI models must not directly use domain/data models.

Bad:

```kotlin
data class WorkoutCardUiModel(
    val workout: Workout,
)
```

```kotlin
@Composable
fun WorkoutCard(workout: Workout)
```

Correct:

```kotlin
data class WorkoutCardUiModel(
    val id: String,
    val title: String,
    val statusText: String,
)
```

If data comes from domain:

```text
Domain model -> presentation mapper -> UI model -> Compose
```

Rules:

- UI models belong to the presentation layer;
- UI models must not contain repositories, use cases, ViewModels, or domain/data models;
- domain -> UI mapping happens outside Composable functions;
- a UI model contains display data only;
- callbacks/actions must not be stored inside UI models unless an existing project convention explicitly requires it.

## 3. A standalone component gets its own UI model

If a component represents a meaningful UI block and uses a set of related data, that data set must be represented by a dedicated UI model.

Bad:

```kotlin
AppTextField(
    label = label,
    placeholder = placeholder,
    value = value,
    isPassword = isPassword,
    isPasswordVisible = isPasswordVisible,
    enabled = enabled,
    hasError = hasError,
    errorText = errorText,
    onValueChange = ...,
    onPasswordVisibilityClick = ...,
)
```

Correct in principle:

```kotlin
AppTextField(
    model = model,
    onValueChange = ...,
    onTrailingActionClick = ...,
    modifier = Modifier,
)
```

The related display data belongs in `AppTextFieldUiModel`.

Parameters that normally remain separate:

- `Modifier`;
- action callbacks;
- truly independent technical parameters, when needed.

Do not create a dedicated UI model for every `Text`, `Icon`, `Row`, `Spacer`, or other trivial detail.

## 4. Do not model component variants with unrelated Boolean flags

If a component has several structurally different variants, do not model them with a set of flags such as:

```text
isPassword
isPasswordVisible
hasIcon
isSearch
isReadOnly
```

when their combinations can create meaningless states.

For example, this is a bad state to allow:

```text
isPassword = false
isPasswordVisible = true
```

Use a type that makes invalid combinations impossible.

Example:

```kotlin
sealed interface TextFieldTypeUiModel {
    data object Text : TextFieldTypeUiModel

    data class Password(
        val isVisible: Boolean,
    ) : TextFieldTypeUiModel
}
```

Then:

```kotlin
data class AppTextFieldUiModel(
    val label: String,
    val placeholder: String,
    val value: String,
    val type: TextFieldTypeUiModel,
)
```

An `enum` is acceptable when the variants do not need their own data. Choose the simplest solution that prevents invalid states.

## 5. Keep feature code and the UI kit strictly separated

The screen, its `UiState`, `UiEvent`, ViewModel, and elements that are genuinely specific to that screen belong to the corresponding feature package/area.

All standalone visual components live in `core/uikit/components`, each in its own package. Keep each component's UI models in the same package beside the component. Do not create a feature-level `components` package. Screen state, events, mode, and ViewModel remain in the feature presentation package.

Before changing shared UI, fetch and integrate the current `main`. Inspect the accepted UI kit and reuse its components, resources, and theme. Resolve overlapping UI kit changes in favor of the accepted `main` version unless the task explicitly requires changing that component. Check the final diff against current `main` to ensure teammate screens and shared components have not been overwritten.

Workout mode and hint visibility must be controlled by screen `UiState`. Use typed modes for mode-specific data, and a nullable hint when no hint should be displayed. Do not derive these decisions from hidden local state.

Shared UI belongs in `commonMain` by default. Place only genuinely platform-specific implementations in platform source sets. Do not introduce new modules or restructure unrelated packages.

## 6. Decompose by meaningful UI blocks

Do not implement a large screen or complex component as one bloated Composable when it naturally consists of meaningful parts.

Example:

```text
WorkoutHistoryScreen
├── WorkoutHistoryHeader
├── WorkoutHistorySection
│   └── WorkoutCard
└── EmptyWorkoutHistoryState
```

The same principle applies inside complex components.

But avoid over-decomposition:

- extract meaningful blocks;
- extract reusable or independently understandable elements;
- extract a part when it clearly improves parent readability;
- do not create separate Composables for a single `Text`, simple `Row`, `Spacer`, `Divider`, etc. without a real reason.

The goal is readable structure, not the maximum possible number of functions.

## 7. Composables only render prepared state

A Composable should mainly:

1. receive prepared presentation state;
2. render it;
3. emit an action outward.

Do not place business logic in Compose.

Forbidden:

- filtering domain entities by business rules;
- deciding whether a workout repetition satisfies domain rules;
- deriving business statuses from domain/data models;
- calling repositories/use cases directly;
- coordinating server and local storage.

Purely visual logic is allowed:

```kotlin
if (model.isHighlighted) { ... }
```

## 8. State ownership

Prefer stateless components whose state is provided from above.

Local Compose state is acceptable for purely visual and temporary details:

- whether a popup is open;
- focus;
- animation state;
- other short-lived state with no business meaning.

State that affects the screen and the user flow belongs in `UiState`/ViewModel.

For example, password visibility is screen state rather than a hidden `remember` inside the field when the screen owns that state.

## 9. Static icons and images are resources, not Canvas code

Static icons/images must be stored as resources and referenced from the UI.

For shared Compose Multiplatform UI, use the project's common resources, for example:

```text
shared/src/commonMain/composeResources/drawable/ic_visibility.xml
shared/src/commonMain/composeResources/drawable/ic_visibility_off.xml
```

Reference them using the Compose Multiplatform resource API (for example, `painterResource(Res.drawable.ic_visibility)`) and `Icon` / `Image`. Use platform-specific resources only when a concrete platform requirement makes shared resources unsuitable; do not duplicate common assets across platforms.

Do not draw a regular static icon through a dedicated `@Composable` using `Canvas`, `Path`, `drawLine`, `drawCircle`, etc.

`Canvas` is acceptable when the graphics are genuinely:

- dynamic;
- calculated at runtime;
- dependent on state/geometry;
- not reasonably representable as a normal resource.

Static geometry inside a Composable is an exception, not the default.

## 10. Preview is a mandatory part of implementation

A meaningful UI component is not considered complete until it can be inspected independently in Compose Preview.

For shared Compose Multiplatform UI, `@Preview` functions may live directly in `commonMain` alongside the shared screen/component, using the project's supported Compose Preview setup. Android Studio renders those previews using its Android tooling; this does not make the shared UI Android-only and does not require moving the UI or its previews into `androidMain`. Previews do not replace checking the real UI on both platforms.

### Component Previews

For every reusable or logically standalone component:

- create at least one `@Preview` together with the component;
- use the application theme;
- use test UI models only;
- do not use a real ViewModel, repository, use case, network, database, or domain model;
- if the component has significantly different visual states, create Previews for the main variants;
- inspect the component in isolation before integrating it into the screen;
- when a component relies on platform services or device hardware, provide a previewable representation with test UI models instead of requiring those services or hardware inside Preview.

For a typed component, show its meaningful variants.

For example, for a text field:

```text
regular text field
password field — password hidden
password field — password visible
```

Do not create Previews for meaningless tiny fragments such as a standalone divider.

### Screen Previews

Every implemented screen must also have a Preview with a representative `UiState`.

A screen Preview:

- works without a ViewModel;
- receives test `UiState` / UI models directly;
- does not use production data sources;
- allows visual inspection of the composed screen before running the app.

If Preview data is reused, a small preview fixture/helper is acceptable. Do not create a heavy framework just for test data.

## 11. Preview must not depend on ViewModel

The production screen may receive state from a ViewModel, but a Preview must not create or resolve a ViewModel.

Production flow:

```text
ViewModel
  -> state + onEvent
  -> Screen/Content
```

Preview:

```text
test UiState
  -> Screen/Content
```

If a task requires a ViewModel skeleton, the ViewModel exists in production code, but the Preview still works directly through state and callbacks.

## 12. Respect the exact scope of the current task

Do not implement adjacent layers "for the future".

If the task asks only for UI + UI models:

- implement the required screens/components;
- implement the required UI models;
- add Previews;
- create only the `UiState`/`UiEvent`/ViewModel skeleton explicitly required by the task;
- do not independently connect repositories/use cases, data loading, mappers, storage, network, navigation, etc.

If the task explicitly says to keep the ViewModel almost empty and only expose `onEvent`, do not add future business logic to it.

## 13. Do not invent a new architecture

Follow the existing project structure and naming conventions.

Without an explicit need, do not add:

- strict reducer-based MVI;
- an additional Store/Reducer/Intent processor;
- new architecture libraries;
- a new DI hierarchy;
- a new feature-module approach;
- universal `BaseViewModel` / `BaseUiModel` abstractions.

Use the smallest solution that satisfies this skill and the concrete task.

## 14. Shared UI by default; platform changes only when required

PushUp Trainer uses shared Compose Multiplatform UI.

Rules:

- implement screens, reusable UI components, UI models, `UiState`, and `UiEvent` in `commonMain` by default;
- keep Android-specific UI integration in `androidMain` and iOS-specific UI integration in `iosMain` when genuinely necessary;
- keep `androidApp` and `iosApp` as platform application entry points; do not duplicate shared screen implementations there;
- do not introduce separate Android and iOS screens when the screen can be implemented in shared Compose;
- do not modify `androidMain`, `iosMain`, `androidApp`, or `iosApp` merely for symmetry or hypothetical future needs;
- if a task is limited to a shared UI component, do not modify platform-specific code unless the task explicitly requires integration;
- if a task is platform-specific, do not change the other platform without a demonstrated requirement within the task scope.

## 15. Exact visual fidelity belongs to the task prompt

This skill defines UI architecture and code organization.

Requirements such as:

- matching a Figma/mockup as closely as possible;
- exact sizes;
- spacing;
- colors;
- typography;
- behavior of a concrete screen

must come from the current task prompt and are not treated as universal skill rules.

## 16. Shared Compose UI is the default

UI implementation is shared unless a concrete technical limitation or an explicit task requirement makes a platform-specific implementation necessary.

Rules:

- prefer one shared screen and one shared UI state/event contract for both Android and iOS;
- reuse the existing shared UI kit and theme rather than creating platform duplicates;
- do not split a screen into two platform implementations merely because it contains one platform-specific capability;
- platform-specific integration must not weaken the UI model, preview, or screen decomposition requirements in this skill.

## 17. Isolate platform dependencies from shared UI

Common UI must not directly depend on Android or iOS framework classes.

Rules:

- keep platform-specific APIs, system views, permissions, and device integrations behind an appropriate project-level boundary;
- expose only the information and actions required by the shared UI;
- keep the UI-facing representation of meaningful components in dedicated UI models, following Section 3;
- do not introduce a new abstraction layer unless the task actually requires platform integration;
- do not bind a shared screen to a particular platform implementation.

## 18. Keep user events separate from external data streams

`UiEvent` and `ViewModel.onEvent(event)` represent actions initiated through the UI. External updates are not user actions.

Rules:

- route UI actions through `onEvent` as required by Section 1;
- do not wrap emissions from repositories, sensors, timers, or other continuous sources in fake user `UiEvent` objects;
- receive external data through the appropriate existing data flow and use it to update presentation state;
- expose prepared UI state to Compose instead of sending domain/data objects directly to components;
- do not put external stream processing or business decisions into Composable functions.

## 19. Keep frequently updated UI state efficient

Some screen data may change frequently, including timers, animations, progress indicators, and measurements.

Rules:

- define state ownership and reading scopes so frequent updates do not unnecessarily cause unrelated UI parts to recompose;
- keep expensive processing and domain calculations out of Composable functions;
- use the simplest mechanism supported by the existing architecture; do not introduce new state-management frameworks or premature optimizations;
- preserve the same `UiState` / UI model / `onEvent` rules for dynamic screens as for ordinary screens.

## Final checklist before completing a UI task

Before finishing, verify all of the following:

- all screen actions go through `onEvent` rather than arbitrary ViewModel methods;
- Compose/UI models do not directly use domain/data models;
- domain data is mapped into UI models before reaching Compose when needed;
- standalone composite components receive a UI model instead of a list of related parameters;
- component variants are not modeled with meaningless combinations of Boolean flags;
- invalid state combinations are made impossible through types where practical;
- standalone components and their UI models live together in their own UI kit packages;
- the branch includes current main and the final diff preserves accepted teammate UI kit and screens;
- screen mode and hint visibility are represented in UiState;
- the feature contains the screen and genuinely feature-specific UI only;
- complex UI is decomposed by meaning without micro-fragmentation;
- business logic is absent from Composable functions;
- static icons/images are stored as resources rather than manually drawn through Canvas without a reason;
- state lives at the correct level;
- every meaningful component has a Preview;
- significant component variants have Previews;
- every screen has a Preview;
- Previews use presentation-only data and do not depend on ViewModels or production dependencies;
- no extra layers outside the current task scope were implemented;
- shared screens, UI models, and reusable components are in `commonMain` by default;
- platform-specific code is isolated and modified only when the current task requires it;
- common UI does not directly depend on Android/iOS framework types;
- shared static assets use Compose Multiplatform resources unless a platform-specific requirement applies;
- shared `commonMain` UI can be inspected with Compose Previews using test presentation state;
- external streams are not disguised as user `UiEvent` actions;
- frequently changing state does not cause avoidable work in unrelated UI components;
- mandatory UI models and Preview requirements remain in effect for all applicable shared components.
