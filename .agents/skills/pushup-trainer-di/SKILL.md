---
name: pushup-trainer-di
description: Project-wide Koin dependency injection rules for PushUp Trainer (Kotlin Multiplatform and shared Compose UI). Use when adding, changing, reviewing, testing, or wiring modules, ViewModels, repositories, use cases, platform dependencies, or DI aggregators.
---

# PushUp Trainer — Dependency Injection Rules

This skill defines the dependency injection conventions for **PushUp Trainer** (`shared` common code, Android, and iOS).

These rules are project-specific. Do not generalize them to unrelated projects.

**All code and module names below are conceptual unless they already exist in the repository.** Verify the actual structure before editing. Do not create an entire illustrated hierarchy merely because it appears in this skill.

The required DI approach is **Koin with the classic DSL**. If DI has not yet been initialized, these rules govern its introduction; they do not authorize creating unused modules or dependencies.

## 1. Core principle

DI must mirror the architecture of the project.

Do not collect unrelated registrations in one large module.

Each architectural area owns its own registrations, and higher-level modules only aggregate lower-level modules with `includes(...)`.

Expected direction:

```text
leaf module
    ↓
area aggregator
    ↓
layer aggregator
    ↓
platform/project aggregator
```

Do not skip these levels by registering everything directly in the root module.

## 2. Use classic Koin DSL only

Use Koin classic DSL (following existing project conventions if DI has already been configured).

Allowed examples:

```kotlin
single { RepositoryImpl(get(), get()) }
factory { SomeUseCase(get()) }
viewModel { SomeViewModel(get()) }
```

Do not introduce:

- Koin annotations;
- Koin KSP;
- generated Koin modules;
- manual dependency containers;
- service locator patterns.

KSP may still exist in the project for unrelated tooling such as Room. Do not confuse that with Koin.

## 3. `includes(...)` is the only module aggregation mechanism

Aggregate Koin modules like this:

```kotlin
val sharedDataModules = module {
    includes(
        sharedRemoteModules,
        sharedLocalModules,
        sharedRepositoryModules,
    )
}
```

Do **not** aggregate modules through:

```kotlin
listOf(...)
buildList { ... }
addAll(...)
flatten()
plus(...)
List<Module>
```

Do not pass lists of modules around just to build the DI graph.

Do not manually duplicate child registrations in an aggregator.

## 4. Aggregators aggregate only

An aggregator should normally contain only `includes(...)`.

Bad:

```kotlin
val androidAppModules = module {
    includes(
        sharedModules,
        sharedAndroidModules,
    )

    viewModel { WorkoutViewModel(get()) }
    single { SomeRepositoryImpl(get()) }
}
```

Good:

```kotlin
val androidAppModules = module {
    includes(
        sharedModules,
        sharedAndroidModules,
    )
}
```

Leaf registrations belong in the module owned by the corresponding package or feature.

## 5. DI registration belongs to the package that owns the implementation

Keep DI close to the architectural area it configures.

Examples:

```text
data/remote/di/
data/local/di/
data/repository/di/

domain/usecase/di/

features/<feature>/di/
```

Do not place repository implementation bindings in `domain`.

Do not place ViewModel registrations in the application root just because the root can see them.

Do not create a generic global `di` package containing every registration in the application.

## 6. Repository DI belongs to data

Repository interfaces belong to:

```text
domain/repository/
```

Repository implementations belong to:

```text
data/repository/
```

The Koin binding between the interface and implementation belongs to:

```text
data/repository/di/
```

Conceptually:

```kotlin
single<ExampleRepository> {
    ExampleRepositoryImpl(
        localDataSource = get(),
        remoteDataSource = get(),
    )
}
```

`SharedRepositoryModules` is part of the **data layer**, not the domain layer.

Never include repository implementation DI directly inside `SharedDomainModules`.

## 7. Use case DI belongs to domain

Use cases live in:

```text
domain/usecase/
```

Their registrations live in:

```text
domain/usecase/di/
```

The use-case module is aggregated by the domain aggregator.

Conceptually:

```kotlin
val sharedDomainModules = module {
    includes(
        sharedUseCaseModules,
    )
}
```

Do not place use-case registrations in:

- data modules;
- repository modules;
- Android or iOS application root modules.

## 8. Common shared hierarchy

Preserve the existing shared DI hierarchy if one is already present. When initially introducing DI, build only the levels and leaf modules needed by current production code.

Common registrations, including shared Compose feature ViewModels, live in `shared/src/commonMain` where their implementations are common. They must not be duplicated in Android and iOS modules.

Conceptually:

```text
SharedCoreModules
    └── common core/network modules, if needed

SharedDataModules
    ├── SharedRemoteModules, if needed
    ├── SharedLocalModules, if needed
    └── SharedRepositoryModules, if needed

SharedDomainModules
    └── SharedUseCaseModules, if needed

SharedFeaturesModules
    └── common feature-owned ViewModel/presentation modules, if needed

SharedModules
    ├── SharedCoreModules
    ├── SharedDataModules
    ├── SharedDomainModules
    └── SharedFeaturesModules
```

Example aggregation when all the illustrated areas exist:

```kotlin
val sharedDataModules = module {
    includes(
        sharedRemoteModules,
        sharedLocalModules,
        sharedRepositoryModules,
    )
}

val sharedDomainModules = module {
    includes(sharedUseCaseModules)
}

val sharedFeaturesModules = module {
    includes(workoutModule)
}

val sharedModules = module {
    includes(
        sharedCoreModules,
        sharedDataModules,
        sharedDomainModules,
        sharedFeaturesModules,
    )
}
```

`workoutModule` is an illustrative name, not a claim that this feature or module already exists.

Use the **actual names already present in the project**. Do not rename existing modules merely to make them match an example. Do not create empty module layers or placeholder dependencies for future work.

## 9. Platform-specific shared hierarchy (Android and iOS)

Android- and iOS-specific implementations must remain in their respective platform source sets (or existing platform-specific modules).

Conceptually:

```text
shared/src/androidMain
    SharedAndroidModules
        ├── SharedAndroidCoreModules, if needed
        └── SharedAndroidDataModules, if needed

shared/src/iosMain
    SharedIosModules
        ├── SharedIosCoreModules, if needed
        └── SharedIosDataModules, if needed
```

Example when the relevant modules actually exist:

```kotlin
val sharedAndroidModules = module {
    includes(sharedAndroidCoreModules, sharedAndroidDataModules)
}

val sharedIosModules = module {
    includes(sharedIosCoreModules, sharedIosDataModules)
}
```

Do not force identical leaf-module layouts on the two platforms when their dependencies differ. Both sides follow the same ownership and aggregation rules.

Platform-specific examples include:

- Android `Context`, Android-only service implementations, or Android-specific database builders;
- iOS Foundation adapters or other iOS-only service implementations;
- platform-specific file access, background work, permissions, or notification infrastructure;
- platform HTTP engines where applicable.

These are illustrative categories, not requirements to introduce any of these services.

Platform-specific implementations must not leak into `commonMain` registrations. Shared contracts may be declared in common code; their Android/iOS bindings belong to the corresponding platform DI.

## 10. Platform boundaries must remain explicit

Common code must depend on abstractions, not Android or iOS implementations.

Bad:

```text
common use case
    ↓
Android-only or iOS-only API
```

Good:

```text
common use case
    ↓
common interface
    ↓
platform implementation registered in androidMain / iosMain
```

The same rule applies to:

- database builders;
- filesystem access;
- background scheduling;
- platform HTTP engines;
- permissions and notification infrastructure;
- other platform-only device APIs.

Do not move a platform implementation into common code merely to simplify DI. Do not add a common abstraction unless a real dependency boundary requires one.

## 11. Shared feature DI is feature-owned

Features with shared Compose UI own their shared ViewModel registrations in `commonMain`, near their presentation implementation.

For example, conceptually:

```text
shared/src/commonMain/kotlin/.../features/<feature>/
    di/
        <Feature>Module.kt
    presentation/
        <Feature>ViewModel.kt
        ...
```

```kotlin
val workoutModule = module {
    viewModel { WorkoutViewModel(get()) }
}
```

`WorkoutViewModel` is an example, not a statement that the class already exists. Register only actual dependencies used by its constructor.

Do not register shared feature ViewModels directly in `AndroidAppModules` or `IosAppModules`. Do not duplicate their registrations on both platforms.

If a ViewModel is genuinely platform-specific, its registration belongs to that platform's feature/area leaf module, not a common feature module.

## 12. Shared features use an aggregator

Shared feature modules are aggregated through a dedicated shared feature aggregator when feature modules exist.

Conceptually:

```text
shared/src/commonMain/.../features/
├── workout/di/WorkoutModule.kt
├── <another-feature>/di/<AnotherFeature>Module.kt
└── di/SharedFeaturesModules.kt
```

Example:

```kotlin
val sharedFeaturesModules = module {
    includes(
        workoutModule,
        anotherFeatureModule,
    )
}
```

These names are illustrative. The aggregator should include only feature modules that actually exist.

The shared feature aggregator becomes reachable through `SharedModules`; neither Android nor iOS application roots should need to know every shared ViewModel.

Use a platform-specific feature aggregator only if that platform really has feature-owned registrations not shared with the other platform.

## 13. Platform application composition roots

Each platform has its own small, declarative application composition root. Each composes the common graph with the appropriate platform graph.

Conceptually:

```kotlin
val androidAppModules = module {
    includes(
        sharedModules,
        sharedAndroidModules,
    )
}

val iosAppModules = module {
    includes(
        sharedModules,
        sharedIosModules,
    )
}
```

If a platform genuinely contains its own feature aggregator, include it at its owning platform aggregation level, not by registering each ViewModel in the application root.

Do not introduce empty `sharedAndroidModules` / `sharedIosModules` merely to imitate the examples. Preserve existing naming conventions, so the actual application roots may be named differently.

Application roots should not contain unrelated leaf registrations for:

- ViewModels;
- repositories;
- use cases;
- local/remote data sources;
- platform infrastructure;
- feature-specific services.

Exceptions require a concrete existing architectural reason, not convenience.

## 14. Koin must start once per application runtime

Koin is initialized exactly once for an Android application runtime and exactly once for a separate iOS application runtime. These are two different application processes, not two starts inside the same one.

Use the established application entry point or common initialization function invoked by the respective platform bootstrap. Preserve the project's existing Koin startup strategy rather than introducing a second one.

Do not:

- start Koin again from an Android Activity after it has already started;
- start it again from an iOS view/controller after startup;
- start Koin from a feature or screen Composable;
- create a second Koin application for a screen;
- start a separate Koin graph just for a shared ViewModel;
- combine multiple independent startup mechanisms within the same application runtime.

Both platform roots must include the real shared modules through the existing `includes(...)` hierarchy. Platform-specific dependencies are supplied by that platform's graph.

When DI is not initialized yet, choose one initialization strategy as part of an explicitly requested DI setup task. Do not create initialization code during unrelated feature work.

## 15. Constructor injection only

Dependencies must be explicit constructor parameters.

Good:

```kotlin
class SomeRepositoryImpl(
    private val localDataSource: SomeLocalDataSource,
    private val remoteDataSource: SomeRemoteDataSource,
)
```

Bad:

```kotlin
class SomeRepositoryImpl {
    private val dependency = GlobalContext.get().get<Something>()
}
```

Do not use:

- `GlobalContext`;
- `getKoin()` inside production classes;
- hidden service lookup;
- application-wide singleton holders;
- static dependency containers.

`get()` belongs inside Koin module declarations, not inside business classes.

## 16. Do not create unnecessary DI modules

Do not create one module per class.

A new module is justified when it represents a meaningful architectural area or feature.

Good examples:

```text
SharedRepositoryModules
SharedFeaturesModules
WorkoutModule
SharedIosModules
```

Usually unnecessary:

```text
OneActionModule
OneButtonModule
OneUseCaseModule
OneMapperModule
```

Prefer an existing appropriate leaf module when it already owns the registration.

## 17. Do not move classes just to satisfy DI

DI follows architecture.

Architecture must not be distorted merely because Koin registration is convenient elsewhere.

For example:

- a repository implementation remains in data;
- a use case remains in domain;
- a shared ViewModel remains in its common feature;
- a genuinely platform-specific ViewModel remains in its platform feature;
- platform-only infrastructure remains in the matching platform source set.

Fix the module hierarchy instead of moving the class to the wrong layer.

## 18. Preserve existing scopes unless the task requires a change

Do not casually change:

```text
single
factory
viewModel
```

for existing registrations.

Respect the lifetime semantics already established by the project.

Typical project expectations include:

- shared repositories: singleton;
- shared network client/configuration: singleton;
- platform services used as infrastructure: singleton where already established;
- ViewModels: `viewModel`;
- use cases: follow the existing `SharedUseCaseModules` convention.

Before adding a new registration, inspect neighboring registrations and follow the existing pattern.

Do not invent a new scope policy in the middle of an unrelated task.

## 19. Singleton semantics may be part of correctness

Some components rely on a single shared instance.

Hypothetical example: a coordination service owns a shared request semaphore, mutex, or per-operation lock map. If its registration is `single`, changing it to `factory` creates separate coordinators and can break synchronization guarantees even though compilation and basic resolution succeed.

The same caution applies to shared session state, caches, schedulers, and other stateful infrastructure *when such dependencies actually exist*.

Always inspect behavior and lifetime assumptions before changing a DI scope. Do not infer singleton requirements from the class name alone.

## 20. Do not duplicate the same dependency in multiple modules

A dependency should have one clear owner.

Do not register the same implementation in:

```text
SharedRepositoryModules
and
SharedDataModules
and
AndroidAppModules
```

The leaf module registers it once.

Aggregators include the leaf module.

## 21. Do not bypass an existing aggregator

If the project already has:

```text
SharedRemoteModules
    ↓
SharedDataModules
    ↓
SharedModules
```

do not also include `SharedRemoteModules` directly in `SharedModules`.

Bad:

```kotlin
val sharedModules = module {
    includes(
        sharedRemoteModules,
        sharedLocalModules,
        sharedRepositoryModules,
        sharedDataModules,
    )
}
```

Good:

```kotlin
val sharedModules = module {
    includes(
        sharedCoreModules,
        sharedDataModules,
        sharedDomainModules,
    )
}
```

Each aggregation level should have one responsibility.

## 22. Do not flatten the graph

Do not optimize away architectural levels because “Koin can resolve it anyway”.

This:

```text
WorkoutModule
    ↓
SharedFeaturesModules
    ↓
SharedModules
    ↓
AndroidAppModules / IosAppModules
```

is intentional.

This:

```text
WorkoutViewModel
    ↓
AndroidAppModules / IosAppModules
```

may compile, but violates the project structure.

Correct DI is not only about successful resolution. It must preserve ownership and module boundaries.

## 23. Feature DI must not pull unrelated layers into the feature

A feature module may register the feature's ViewModel and presentation-specific dependencies.

Do not use the feature DI module as a place to re-register:

- repositories;
- remote data sources;
- shared use cases;
- database objects;
- network clients.

If the ViewModel later needs a use case:

```kotlin
viewModel {
    WorkoutViewModel(
        startWorkoutUseCase = get(),
    )
}
```

The use case still remains registered in the shared domain DI where it belongs.

## 24. Avoid speculative DI

Do not inject dependencies “for the future”.

If an existing ViewModel currently has no business dependency, do not inject:

```text
SomeUseCase
SomeRepository
SomeApi
```

just because they may be needed later.

Add a dependency when production code actually needs it.

## 25. Respect task scope

If a task changes only a ViewModel constructor, normally only its owning DI registration should need modification.

Do not use a small constructor change as justification to reorganize unrelated DI.

Before editing multiple DI files, ask:

> Is each changed module actually required to make this dependency reachable through the existing hierarchy?

If not, leave it unchanged.

## 26. Read the existing graph before editing

Before making DI changes:

1. inspect the class whose dependencies changed;
2. locate its current Koin registration;
3. inspect the owning leaf module;
4. inspect the immediate aggregator;
5. follow the chain up to the application composition root;
6. inspect neighboring registrations for naming and scope conventions.

Do not infer the graph only from filenames.

## 27. Preserve existing names

Use the actual module names already established by the project.

Do not rename:

- module variables;
- module files;
- aggregators;
- packages

unless the task explicitly requires a rename.

Examples in this skill describe architecture, not a migration request.

## 28. DI changes must be minimal

A correct DI change usually has a small diff.

Examples:

### Adding a new feature ViewModel

Expected:

```text
+ feature module registration
+ feature aggregator include
```

If the aggregator already includes the feature module, only the leaf registration may change.

### Adding a repository implementation

Expected:

```text
+ binding in SharedRepositoryModules
```

Do not also edit top-level modules if the repository module is already included transitively.

## 29. Verify transitive inclusion before adding another `includes`

Before adding an include, confirm the child module is not already reachable.

Do not duplicate module inclusion at multiple levels “just to be safe”.

The hierarchy should be explicit, not redundant.

## 30. Koin graph tests are architectural tests

When a DI change affects the graph, update or add the smallest relevant graph test if the project supports such a test.

Tests should verify that important dependencies resolve through the **real module hierarchy**.

Examples (when these dependencies exist):

- common repositories and use cases resolve from `SharedModules`;
- shared feature ViewModels are reachable transitively through `SharedFeaturesModules`;
- Android platform bindings resolve through the real Android application graph;
- iOS platform bindings resolve through the real iOS application graph, in an appropriate iOS-capable test environment.

Do not build a parallel test-only DI graph that bypasses production aggregators. Where a platform runtime is required, use narrowly scoped test substitutions only for unavailable external resources, without replacing the production aggregation hierarchy.

Do not assume a graph test already exists. If the task cannot run a platform's tests in the current environment, state that limitation explicitly rather than claiming full cross-platform validation.

## 31. Preserve existing DI regression coverage

If the project already has DI graph tests or other DI regression coverage, preserve them.

When DI changes:

- run the relevant existing graph tests when the required target/toolchain is available;
- do not weaken existing assertions;
- do not replace real production modules with ad-hoc test graphs merely to make a test pass;
- if a graph test fails, inspect the production hierarchy and fix production DI rather than bypassing it.

When the project has no DI tests yet, do not invent their existence. Add the smallest relevant tests when the current task requires new graph coverage, following available KMP test infrastructure.

## 32. Build and test after DI changes

Run tests and builds that are relevant to the DI code actually changed, using tasks present in the **current** PushUp Trainer Gradle configuration.

For example:

- common DI changes: compile/test the relevant shared targets;
- Android DI changes: run relevant Android tests and the Android build;
- iOS DI changes: compile/test the relevant iOS target where an Apple toolchain is available;
- shared feature ViewModel registrations: verify their actual reachability through the common and affected platform graphs.

Inspect `./gradlew tasks` (or `gradlew.bat tasks` on Windows) and existing build scripts before selecting commands. Do not copy task paths from another project and assume they exist.

Do not declare DI correct based solely on compilation if relevant graph tests exist. If iOS validation cannot run on the current machine, report that limitation explicitly; do not claim iOS was tested.

## 33. Review the final diff

Before finishing a DI task:

1. inspect `git diff`;
2. verify registrations are in their owning leaf modules;
3. verify aggregators contain only the expected `includes(...)`;
4. verify no list-based module aggregation was introduced;
5. verify no dependency was registered twice;
6. verify no platform dependency leaked into common code;
7. verify shared feature ViewModels are not registered separately on Android and iOS;
8. verify no unrelated DI file changed;
9. verify no service locator was introduced.

Run:

```powershell
git diff --check
```

## 34. Anti-pattern checklist

Reject or refactor a solution if it introduces any of these:

```text
all registrations in AndroidAppModules or IosAppModules
all registrations in SharedModules
List<Module>
listOf(modules...)
buildList { add(module) }
manual addAll/flatten
duplicate module inclusion
repository DI inside domain
ViewModel registration in root application DI
platform implementation in common DI
GlobalContext
getKoin() inside production classes
manual AppDependencies container
multiple Koin start points
module per class
speculative dependencies
duplicated bindings
```

## 35. Preferred mental model

Think of the DI graph as a tree of ownership, with common feature registrations included by each platform's application root.

```text
SharedModules (commonMain)
├── SharedCoreModules
├── SharedDataModules
│   ├── remote/local leaf modules (when needed)
│   └── repository leaf modules (when needed)
├── SharedDomainModules
│   └── use-case leaf modules (when needed)
└── SharedFeaturesModules
    └── feature-owned ViewModel leaf modules (when needed)

AndroidAppModules                  IosAppModules
├── SharedModules                  ├── SharedModules
└── SharedAndroidModules           └── SharedIosModules
    └── Android leaf modules           └── iOS leaf modules
```

The two application roots include the same common module definitions, but Koin is started independently in each application's runtime. Common feature ViewModels belong to common feature leaf modules; only genuinely platform-specific registrations belong to platform leaf modules.

A leaf owns registrations.

An aggregator owns only its children.

The application root owns only top-level areas.

Do not add empty branches that the actual project does not need. This ownership structure should remain visible in the code.

## 36. Final rule

Do not ask only:

> “Will Koin resolve this?”

Ask both:

> “Will Koin resolve this?”

and:

> “Is this registration owned by the correct architectural area, and is it reached through the correct `includes(...)` hierarchy?”

The task is complete only when both answers are yes.
