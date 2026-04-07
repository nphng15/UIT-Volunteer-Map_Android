# Android Architecture And File Organization Rule

## 1. Scope

- Applies to the entire `UITVolunteerMap` project.
- The project currently follows a single-module `:app` setup using Kotlin, Jetpack Compose, Hilt, Coroutines, and Navigation Compose.
- The codebase should stay easy to read, easy to onboard into, easy to scale, and ready for future modularization if needed.

## 2. Architectural Goals

- Organize by feature, not purely by technology type.
- Keep the UI state-driven, testable, and predictable.
- Separate business logic from Android framework code.
- Hide data sources behind repositories.
- Make code ownership obvious from the folder structure.
- Minimize coupling between features.

## 3. Root Package Structure

Source code should always live under these three main areas:

```text
com.example.uitvolunteermap
├── app
├── core
└── features
```

Responsibility convention:

- `app/`
  Contains `Application`, `MainActivity`, the app root, `NavHost`, theme bootstrap, and app-level wiring.
- `core/`
  Contains code shared across multiple features.
- `features/`
  Each feature manages its own presentation, domain, and data layers.

## 4. Dependency Direction

Default dependency direction:

```text
presentation -> domain -> data
```

Mandatory rules:

- `presentation` may depend on `domain` and `core`, but must not import Retrofit, Room, DTOs, or repository implementations.
- `domain` should be pure Kotlin whenever possible and should not contain Android UI or framework dependencies.
- `data` is responsible for repository implementations, remote/local sources, and mapping data into domain models.
- `core` should contain only truly shared code; do not move feature-specific business logic into `core`.
- One feature must not import another feature's `presentation` layer.

## 5. Feature-Based Organization

Do not organize the app like this at the root level:

```text
viewmodel/
screen/
repository/
model/
```

Always prefer:

```text
features/
  home/
  campaign/
  profile/
```

Inside each feature, split by layer when needed.

Suggested structure:

```text
features/
  home/
    presentation/
      HomeRoute.kt
      HomeScreen.kt
      HomeUiState.kt
      HomeUiEffect.kt
      HomeUiEvent.kt
      HomeViewModel.kt
    domain/
      model/
      repository/
      usecase/
    data/
      model/
      mapper/
      datasource/
      repository/
    di/
```

If a feature contains multiple sub-screens, they should still stay under the same feature owner:

```text
features/
  campaign/
    presentation/
      detail/
      map/
    domain/
    data/
```

## 6. Package Names Must Match Folder Paths

The `package` declaration must match the real file path on disk exactly.

This is not acceptable:

```text
features/campaign/presentation/map/
```

with a package like:

```kotlin
com.example.uitvolunteermap.features.campaignmap.presentation
```

Rules to keep:

- The folder structure is the source of truth.
- Package names must follow the folder structure 1:1.
- If a screen belongs to the `campaign` feature, both its path and package must stay under `features.campaign...`.

## 7. Presentation Rules

For Compose screens, prefer the screen contract pattern:

- `Route`
  Creates `hiltViewModel()`, collects state/effect, and wires navigation and lifecycle.
- `Screen`
  Renders UI only. Receives immutable state and callbacks.
- `UiState`
  Represents the state used to render the UI.
- `UiEffect`
  Represents one-shot events such as navigation, snackbar, toast, or permission requests.
- `UiEvent`
  Represents user intents when the screen is large enough to justify modeling actions explicitly.
- `ViewModel`
  Handles events, calls use cases, and updates state/effects. It must not contain UI rendering code.

Mandatory presentation rules:

- Do not pass `NavController` into `Screen`.
- Do not call repositories directly from composables.
- `Screen` should be as stateless as possible and only keep local UI state when truly necessary.
- Use `StateFlow` for screen state.
- Do not place one-shot effects inside `UiState`.
- Any screen that is not trivially simple should be split into `Route` and `Screen`.

## 8. Domain Rules

Create `domain/` when a feature has meaningful business logic, a workflow worth modeling, or logic that should be separated from data sources.

Use cases should be created when:

- logic is reused across multiple screens,
- multiple repositories need to be coordinated,
- validation or business rules are non-trivial,
- the `ViewModel` is becoming too large.

Use cases are not required when:

- the action is only a trivial pass-through,
- the logic is not meaningful enough yet to justify extraction.

Domain rules:

- Domain models are not DTOs.
- Repository interfaces should live in the domain package of the feature that owns that business logic.
- Do not place unrelated usecases to another screen like placing `campaign` use cases inside `features/HOME/domain`.
- Only move code into `core` when it is truly shared across multiple features.

## 9. Data Rules

`data/` owns:

- repository implementations,
- remote models (`Dto`),
- local entities,
- mappers,
- remote/local data sources,
- caching and persistence decisions.

Mandatory rules:

- API models must use the `Dto` suffix.
- Database models must use the `Entity` suffix.
- Mapping logic must be explicit and placed in `mapper/`.
- Repositories must return domain models or an app result wrapper, not DTOs to presentation.
- Presentation must not import `data.model`.

Suggested setup:

```text
domain/repository/HomeRepository.kt
data/repository/HomeRepositoryImpl.kt
```

`Fake*Repository` should only be used for prototypes, previews, mock flows, or early-stage development. Once real network or local data sources exist, replace them with explicit implementations.

## 10. Core Rules

Only move code into `core/` if it is shared, stable, and not feature-specific.

Good candidates:

- `core/common/result`
- `core/common/error`
- `core/ui`
- reusable system-level composables
- dispatchers, logger wrappers, and platform-level helpers

Do not move code into `core/` too early:

- feature-specific models,
- feature-specific mappers,
- feature-specific validation,
- feature-specific use cases.

## 11. Dependency Injection Rules

Use Hilt consistently.

- App-level modules may live in `app/di`.
- Feature-specific bindings should live in `features/<feature>/di` as the feature grows.
- Prefer `@Binds` for interface-to-implementation bindings.
- Use `@Provides` only when creating third-party objects or objects that require more complex builder logic.
- DI modules should live close to the owner of the implementation.

## 12. File And Class Naming Rules

- Each file should contain only one public top-level type whenever possible.
- Name files based on responsibility, not vague generic terms.
- Prefer meaningful names such as `GetCampaignDetailUseCase`, `CampaignMapUiState`, and `CampaignDetailRoute`.

## 13. Repository-Specific Standardization

When touching the current codebase, move toward this structure:

```text
features/
  campaign/
    presentation/
      detail/
      map/
    domain/
      model/
      repository/
      usecase/
    data/
```

Specific standardization goals:

- `CampaignDetail*` and `CampaignMap*` should belong to the same `campaign` feature owner.
- Global navigation can still live in `app/navigation`, but route composables and screen contracts must stay inside their owning feature.

## 14. Future Expansion Rules

Keep the project single-module while it remains manageable.

Consider splitting into modules when:

- the number of features clearly grows,
- build time becomes a bottleneck,
- many developers work in parallel,
- the boundaries between features and core are already stable.

Possible future modularization:

```text
:app
:core:common
:core:ui
:feature:home
:feature:campaign
```

Do not split into modules before package ownership is clean.

## 15. Review Checklist

Before merging architecture-related changes, check:

- Do package names and file paths match 100%?
- Does each feature own its own screen, ViewModel, use case, repository, and model?
- Is presentation depending on DTOs or repository implementations?
- Does domain depend on Android UI classes?
- Is the code placed in `core` truly shared?
- Are `Route` and `Screen` properly separated for Compose screens?
- Do file names and class names clearly communicate ownership and responsibility?
