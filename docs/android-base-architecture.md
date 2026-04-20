# Android Base Architecture

## Suggested package layout

```text
app/
  app/
    MainActivity.kt
    UITVolunteerApplication.kt
    di/
    navigation/
  core/
    common/
      coroutine/
      error/
      result/
      ui/
      usecase/
    database/
    network/
  features/
    auth/
    campaign/
      data/
      domain/
      presentation/
    event/
    locality/
    team/
```

## Base classes already scaffolded

- `BaseViewModel`: Shared state/effect handling for presentation.
- `SuspendUseCase` and `FlowUseCase`: Shared domain use case contracts.
- `AppResult`: Unified success/error wrapper.
- `AppError`: Shared error model across data/domain/presentation.
- `BaseRemoteDataSource`: Android equivalent of a backend `ApiBase`.
- `DispatcherProvider`: Makes coroutine dispatchers injectable and testable.
- `UiMessage`, `UiState`, `UiEffect`: Reusable UI contracts.

## Library stack

- `Hilt`: Dependency injection.
- `Retrofit + OkHttp`: HTTP client and API declaration.
- `Room`: Local cache / offline-first support.
- `Lifecycle + ViewModel + Coroutines`: UI state management.
- `Timber`: Structured logging.

## Android equivalent for backend ApiBase

In Android, do not put all networking logic into a single giant `ApiBase` class.
Split it into these responsibilities instead:

- `ApiService`: Retrofit interface per feature.
- `NetworkModule`: Creates `OkHttpClient`, `Retrofit`, interceptors.
- `BaseRemoteDataSource`: Wraps `safeApiCall()` and maps exceptions.
- `AuthInterceptor`: Injects access token into requests.
- `TokenProvider`: Reads and stores auth tokens.
- `Repository`: Combines remote + local data sources for the domain layer.

## Feature roadmap for this project

- `auth`: Login, role resolution, token/session handling.
- `campaign`: Campaign list, campaign detail, campaign map.
- `team`: Team management for admin.
- `locality`: Local activity management for commander/admin.
- `event`: Campaign event management.
- `account`: Commander account management for admin.
