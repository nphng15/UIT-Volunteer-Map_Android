# Android Kotlin Clean Architecture Team Guide

> A practical team standard for building Android apps with **Kotlin-first**, **Clean Architecture**, **DRY**, **testability**, and **maintainable module boundaries**.

---

## 1. Scope and default assumptions

This guide assumes:

- You are building an **Android mobile app**
- New development is **Kotlin-first**
- UI is **Jetpack Compose** by default for new screens
- Architecture uses:
  - **UI layer**
  - **Data layer**
  - **Optional Domain layer** when business logic is complex or reused
- Dependency injection uses **Hilt**
- Async work uses **Coroutines + Flow**
- Persistence uses **Room/DataStore**
- Networking uses **Retrofit + OkHttp** (or equivalent)
- The codebase may contain **both Java and Kotlin**, but new code should be written in Kotlin unless there is a strong reason not to

If your project is **legacy XML/ViewBinding**, **offline-first**, **chat/realtime**, **fintech**, **white-label**, or **SDK-heavy**, keep the same principles but adjust module boundaries as described later in this document.

---

## 2. Architecture goals

The codebase should optimize for:

1. **Readability**
2. **Separation of concerns**
3. **Low coupling**
4. **High cohesion**
5. **Testability**
6. **Predictable state management**
7. **Easy onboarding for new team members**
8. **Safe scaling as the team and product grow**

The main idea is simple:

- UI should only care about rendering state and sending user actions
- Business rules should live outside screens
- Data access should be hidden behind repositories
- Shared code should be centralized carefully, not abstracted too early

---

## 3. Team principles

## 3.1 Clean code rules

- Prefer **small, focused classes**
- Prefer **composition over inheritance**
- Use **base classes sparingly**
- Keep **framework code at the edges**
- Keep **business rules framework-agnostic**
- Favor **immutable state**
- Make dependencies **explicit**
- Name classes by **responsibility**, not by technology only
- Keep files close to the feature they belong to

## 3.2 DRY done correctly

DRY does **not** mean “move every repeated line into a base class”.

Use DRY only when the repeated logic is:

- truly shared
- stable
- conceptually the same
- likely to stay the same across screens/features

Do **not** create abstractions just because two classes look similar today.

Good DRY:
- shared error mapper
- shared network wrapper
- shared date formatter
- shared pagination helper
- reusable UI components
- reusable use cases

Bad DRY:
- giant `BaseViewModel`
- giant `BaseFragment`
- one abstract repository for unrelated features
- one generic use case abstraction that hides business meaning
- one mapper to convert every type into every other type

## 3.3 Feature-first organization

Organize by **feature**, not by technical type only.

Prefer:
- `feature/auth`
- `feature/home`
- `feature/profile`

Avoid a project that is only:
- `activities/`
- `fragments/`
- `adapters/`
- `models/`
- `utils/`

That technical-only structure becomes hard to navigate as the project grows.

---

## 4. Recommended architecture style

Use this mental model:

```text
UI -> Domain (optional) -> Data
```

### UI layer
Responsible for:
- rendering state
- collecting user input
- calling ViewModel actions
- reacting to state changes

### Domain layer
Responsible for:
- business rules
- reusable logic
- orchestration across repositories
- feature use cases

Use it when:
- logic is reused by multiple screens
- ViewModels become too large
- business rules are non-trivial
- workflows combine multiple repositories

### Data layer
Responsible for:
- repositories
- local data sources
- remote data sources
- DTO/entity mapping
- caching
- conflict resolution
- persistence and retrieval

---

## 5. Small app vs medium/large app

## 5.1 Small app structure

Use a **single app module** with feature packages.

```text
app/
  src/main/java/com/example/app/
    core/
    feature/
      auth/
      home/
      profile/
```

This is enough when:
- 1 to 3 developers
- few features
- simple business logic
- low reuse pressure

## 5.2 Medium/large app structure

Use **multi-module**.

```text
:app
:core:common
:core:ui
:core:designsystem
:core:model
:core:network
:core:database
:core:datastore
:core:navigation
:core:testing
:feature:auth
:feature:home
:feature:profile
:feature:settings
```

Use multi-module when:
- multiple developers work in parallel
- build times are increasing
- features are large or independent
- feature ownership matters
- reuse and release boundaries matter

### Suggested responsibility per module

- `:app`  
  App entry point, navigation host, DI startup, app-wide wiring

- `:core:common`  
  Common interfaces, constants, dispatchers, result wrappers, extension functions

- `:core:ui`  
  Reusable UI primitives that are not brand-specific

- `:core:designsystem`  
  Theme, typography, colors, components, dimensions

- `:core:model`  
  Shared models used across features only when truly shared

- `:core:network`  
  Retrofit services, interceptors, API wrappers, network config

- `:core:database`  
  Room database, DAOs, entities

- `:core:datastore`  
  Preferences, user settings, small local state

- `:core:navigation`  
  Shared route definitions or navigation contracts

- `:core:testing`  
  Fakes, test utilities, test dispatchers, common rules

- `:feature:*`  
  Feature-specific UI, domain, data, navigation, tests

---

## 6. The folder structure inside a feature

For each feature, keep the same internal structure.

```text
feature/profile/
  src/main/java/com/example/feature/profile/
    data/
      local/
      remote/
      mapper/
      repository/
    domain/
      model/
      repository/
      usecase/
    presentation/
      component/
      ProfileRoute.kt
      ProfileScreen.kt
      ProfileViewModel.kt
      ProfileUiState.kt
      ProfileAction.kt
      ProfileEffect.kt
      ProfileUiMapper.kt
    di/
      ProfileModule.kt
```

### Notes

- `data/` contains implementation details
- `domain/` contains contracts and business logic
- `presentation/` contains screen logic and UI state
- `di/` keeps feature dependency wiring local

If your feature is small, you may skip some folders until needed.

---

## 7. The most important naming conventions

Use naming that makes intent obvious.

### Presentation
- `ProfileScreen`
- `ProfileRoute`
- `ProfileViewModel`
- `ProfileUiState`
- `ProfileAction`
- `ProfileEffect`

### Domain
- `GetProfileUseCase`
- `UpdateProfileUseCase`
- `ObserveProfileUseCase`

### Data
- `ProfileRepository` (domain contract)
- `DefaultProfileRepository` or `OfflineFirstProfileRepository` (implementation)
- `ProfileRemoteDataSource`
- `ProfileLocalDataSource`
- `ProfileDto`
- `ProfileEntity`

### Mapping
- `ProfileMapper`
- `ProfileUiMapper`
- `ProfileEntityMapper`

### Tests
- `GetProfileUseCaseTest`
- `ProfileViewModelTest`
- `FakeProfileRepository`

---

## 8. Base classes: what to do and what to avoid

Base classes are useful only when they remove real, repeated boilerplate **without hiding behavior**.

## 8.1 Allowed base classes

Use them only when they stay small and stable.

### A. `BaseViewModel` (thin only)

Allowed for:
- common coroutine launch wrappers
- common error/state helper methods
- shared logger access if truly needed

Do not put:
- navigation logic
- screen-specific state
- feature-specific business rules
- Android `Context`
- repository references shared by unrelated features

Example:

```kotlin
abstract class BaseViewModel : ViewModel() {

    protected fun launchSafely(
        block: suspend CoroutineScope.() -> Unit,
        onError: (Throwable) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                block()
            } catch (t: Throwable) {
                onError(t)
            }
        }
    }
}
```

Keep it tiny. If it starts growing, replace shared logic with:
- extension functions
- delegates
- helper classes
- use cases

### B. `BaseFragment<VB : ViewBinding>` (only for XML/View system)

If your app uses XML + ViewBinding, this can be acceptable to handle:
- binding creation
- binding cleanup in `onDestroyView`

If you are **Compose-first**, you probably do **not** need `BaseFragment`.

### C. `BaseActivity`

A minimal base activity is acceptable if it only centralizes:
- edge-to-edge setup
- locale handling
- analytics hookup
- global error display
- window insets config

Avoid turning it into a god class.

## 8.2 Avoid these base abstractions

Avoid:
- `BaseRepository<T>`
- `BaseUseCase<P, R>` for everything
- `BaseAdapter` that knows about too many screens
- `BaseScreen` that mixes UI, navigation, analytics, loading, auth, and permissions
- giant `BaseViewModel` with dozens of helper methods

Use **composition** instead of inheritance whenever possible.

---

## 9. Core patterns to use

## 9.1 Repository pattern

Repositories are the entry point from UI/domain into data.

Responsibilities:
- combine local and remote sources
- cache and persist
- map models
- hide data source details
- provide a stable API to the rest of the app

Example contract:

```kotlin
interface ProfileRepository {
    suspend fun getProfile(userId: String): Profile
    fun observeProfile(userId: String): Flow<Profile>
    suspend fun updateProfile(profile: Profile)
}
```

Implementation example:

```kotlin
class DefaultProfileRepository @Inject constructor(
    private val remote: ProfileRemoteDataSource,
    private val local: ProfileLocalDataSource,
) : ProfileRepository {

    override suspend fun getProfile(userId: String): Profile {
        val cached = local.getProfile(userId)
        if (cached != null) return cached.toDomain()

        val remoteProfile = remote.getProfile(userId)
        local.saveProfile(remoteProfile.toEntity())
        return remoteProfile.toDomain()
    }

    override fun observeProfile(userId: String): Flow<Profile> {
        return local.observeProfile(userId).map { it.toDomain() }
    }

    override suspend fun updateProfile(profile: Profile) {
        local.saveProfile(profile.toEntity())
        remote.updateProfile(profile.toDto())
    }
}
```

## 9.2 Use case pattern

Each use case should have **one business responsibility**.

Good examples:
- `LoginUseCase`
- `ValidateEmailUseCase`
- `ObserveProfileUseCase`
- `CalculateCartTotalUseCase`

Example:

```kotlin
class GetProfileUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(userId: String): Profile {
        return repository.getProfile(userId)
    }
}
```

## 9.3 State holder pattern

Use a ViewModel as the screen-level state holder.

Example:

```kotlin
data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: ProfileUiModel? = null,
    val error: UiMessage? = null
)
```

```kotlin
sealed interface ProfileAction {
    data object Load : ProfileAction
    data class Save(val name: String) : ProfileAction
    data object Retry : ProfileAction
}
```

```kotlin
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfile: GetProfileUseCase,
    private val updateProfile: UpdateProfileUseCase,
    private val mapper: ProfileUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.Load,
            ProfileAction.Retry -> loadProfile()
            is ProfileAction.Save -> saveProfile(action.name)
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { getProfile("current_user") }
                .onSuccess { profile ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            profile = mapper.toUi(profile),
                            error = null
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = UiMessage.from(throwable)
                        )
                    }
                }
        }
    }

    private fun saveProfile(name: String) {
        viewModelScope.launch {
            runCatching { updateProfile(name) }
                .onSuccess { loadProfile() }
                .onFailure { throwable ->
                    _uiState.update { it.copy(error = UiMessage.from(throwable)) }
                }
        }
    }
}
```

## 9.4 Mapper pattern

Map between layers explicitly.

Recommended mapping boundaries:
- `Dto -> Entity` or `Dto -> Domain`
- `Entity -> Domain`
- `Domain -> UiModel`

Do not send raw DTOs directly into the UI.

## 9.5 Strategy pattern

Use Strategy when behavior changes by environment or business rule.

Examples:
- payment provider
- tax calculation
- pricing rules
- login method
- sorting rules
- validation rules

Example:

```kotlin
interface PasswordValidator {
    fun validate(value: String): ValidationResult
}

class DefaultPasswordValidator : PasswordValidator {
    override fun validate(value: String): ValidationResult {
        return when {
            value.length < 8 -> ValidationResult.Invalid("Minimum 8 characters")
            else -> ValidationResult.Valid
        }
    }
}
```

## 9.6 Adapter pattern

Wrap third-party SDKs behind your own interface.

Good for:
- analytics SDKs
- crash reporting
- payments
- chat SDKs
- maps
- biometric wrappers

This protects your app from SDK churn.

## 9.7 Factory pattern

Use Factory when object creation is complex or variant-based.

Good for:
- building screen arguments
- creating worker requests
- constructing dynamic UI blocks
- notification builders

## 9.8 Result wrapper pattern

Use a typed result wrapper when the UI must understand known failures explicitly.

Example:

```kotlin
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(val error: AppError) : AppResult<Nothing>
}
```

Use this when:
- failures are part of normal business flow
- error handling must be explicit
- multiple layers need structured error info

Do not wrap everything blindly if standard exceptions are already enough.

---

## 10. UI state, events, and side effects

A clean screen contract usually has:

- `UiState`: long-lived renderable state
- `Action`: user intent from UI to ViewModel
- `Effect` (optional): one-off effects such as navigation or toast/snackbar

Recommended shape:

```kotlin
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSubmitEnabled: Boolean = false,
    val error: UiMessage? = null,
    val isLoggedIn: Boolean = false
)
```

```kotlin
sealed interface LoginAction {
    data class EmailChanged(val value: String) : LoginAction
    data class PasswordChanged(val value: String) : LoginAction
    data object Submit : LoginAction
}
```

```kotlin
sealed interface LoginEffect {
    data object NavigateHome : LoginEffect
    data class ShowMessage(val message: String) : LoginEffect
}
```

### Rule of thumb

- Keep **persistent** data in `UiState`
- Keep **user intents** in `Action`
- Use `Effect` only for truly one-time behavior

If your team wants to stay very strict, model as much as possible as state instead of one-off event channels.

---

## 11. Kotlin and Java coexistence rules

If your project still contains Java, use these rules:

### Default rule
- **All new features should be Kotlin-first**

### Keep Java only when:
- the module is legacy and not worth rewriting yet
- a third-party SDK or old code path is tightly coupled to Java
- migration cost is currently higher than the business benefit

### Interop rules
- keep public APIs simple
- avoid exposing unnecessarily fancy Kotlin constructs across Java boundaries
- use `@JvmStatic`, `@JvmOverloads`, `@JvmField` only when needed
- do not duplicate logic in both Java and Kotlin
- migrate feature by feature, not file by file randomly

### Team recommendation
Pick one language for new code. In 2026, that should almost always be **Kotlin** for Android app code.

---

## 12. Dependency injection setup

Use Hilt for dependency injection.

### App class

```kotlin
@HiltAndroidApp
class MyApp : Application()
```

### Repository binding

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindProfileRepository(
        impl: DefaultProfileRepository
    ): ProfileRepository
}
```

### Data source provider

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .build()
    }
}
```

### Dispatcher qualifiers (recommended)

```kotlin
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher
```

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
```

---

## 13. Recommended cross-cutting utilities

Keep these in `:core:common` or another core module.

### Good shared utilities
- `AppDispatchers` or dispatcher qualifiers
- `UiMessage`
- `AppError`
- `Logger`
- date/time formatter
- number/currency formatter
- extension functions
- network error parser
- pagination helper
- validation helpers
- resource provider only if really needed

### Do not dump everything into `utils/`

Replace a vague `utils` package with meaningful packages such as:
- `error/`
- `formatter/`
- `validation/`
- `extensions/`
- `dispatcher/`

---

## 14. Recommended setup order for a new project

## Step 1: Create the project
- Create a new Android project in Android Studio
- Use Kotlin
- Prefer Compose for new UI
- Configure namespace and package names clearly

## Step 2: Decide module strategy
- Start single-module if the app is small
- Start multi-module if the app is expected to scale quickly or has multiple contributors

## Step 3: Add baseline dependencies
Recommended categories:
- Hilt
- Coroutines
- Lifecycle/ViewModel
- Navigation
- Room
- Retrofit/OkHttp
- Testing libraries
- Static analysis tools

## Step 4: Configure version catalog
Use `libs.versions.toml` to centralize dependency versions.

## Step 5: Create core modules or core packages
Start with:
- common
- ui/design system
- network
- database
- testing

## Step 6: Define project-wide contracts
Create shared contracts for:
- `UiState`
- `Action`
- `UiMessage`
- `AppError`
- result handling
- dispatcher qualifiers

## Step 7: Build one feature end-to-end
Do not try to build every module first.
Create one vertical slice:

```text
login screen
-> viewmodel
-> use case
-> repository
-> remote/local data source
-> mapper
-> tests
```

Then copy the pattern.

## Step 8: Add quality gates
Set up:
- formatting
- static analysis
- CI build
- test tasks
- branch protection
- PR checklist

## Step 9: Document the conventions
Put this file in the root of the repository and treat it as the team standard.

---

## 15. Example end-to-end feature template

```text
feature/login/
  presentation/
    LoginRoute.kt
    LoginScreen.kt
    LoginViewModel.kt
    LoginUiState.kt
    LoginAction.kt
  domain/
    usecase/
      LoginUseCase.kt
      ValidateEmailUseCase.kt
  domain/
    repository/
      AuthRepository.kt
  data/
    remote/
      AuthApi.kt
      AuthRemoteDataSource.kt
    local/
      SessionLocalDataSource.kt
    repository/
      DefaultAuthRepository.kt
    mapper/
      AuthMapper.kt
  di/
    AuthModule.kt
```

### `LoginUseCase.kt`

```kotlin
class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val validateEmail: ValidateEmailUseCase,
) {
    suspend operator fun invoke(email: String, password: String): AppResult<Unit> {
        val emailResult = validateEmail(email)
        if (emailResult is ValidationResult.Invalid) {
            return AppResult.Error(AppError.Validation(emailResult.message))
        }
        return repository.login(email, password)
    }
}
```

### `AuthRepository.kt`

```kotlin
interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<Unit>
    suspend fun logout()
    fun isLoggedIn(): Flow<Boolean>
}
```

### `DefaultAuthRepository.kt`

```kotlin
class DefaultAuthRepository @Inject constructor(
    private val remote: AuthRemoteDataSource,
    private val sessionLocal: SessionLocalDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<Unit> {
        return runCatching {
            val token = remote.login(email, password)
            sessionLocal.saveToken(token)
            Unit
        }.fold(
            onSuccess = { AppResult.Success(Unit) },
            onFailure = { AppResult.Error(AppError.Network(it.message ?: "Login failed")) }
        )
    }

    override suspend fun logout() {
        sessionLocal.clear()
    }

    override fun isLoggedIn(): Flow<Boolean> = sessionLocal.observeToken().map { !it.isNullOrBlank() }
}
```

---

## 16. Testing strategy

## 16.1 What to test first

Highest-value tests:
1. use cases
2. mappers
3. reducers/state transformations
4. repositories
5. ViewModels
6. critical UI flows

## 16.2 Test types

### Local unit tests
Use for:
- use cases
- validators
- mappers
- pure business logic
- repository logic with fakes

### Instrumented/UI tests
Use for:
- navigation
- login flow
- checkout flow
- permission-sensitive flows
- database integration if needed
- critical user journeys

## 16.3 Test doubles

Prefer:
- `FakeProfileRepository`
- `FakeClock`
- `FakeAnalytics`
- `FakeSessionStore`

Use mocks only when fakes are too expensive or the dependency is too interaction-heavy.

## 16.4 Testing rules for the team

- Every new use case should have unit tests
- Every mapper should have unit tests if the mapping matters
- Every ViewModel should have happy path + failure path tests
- Every critical feature should have at least one UI/integration smoke test

---

## 17. Error handling standard

Define errors centrally.

Example:

```kotlin
sealed interface AppError {
    data class Network(val message: String) : AppError
    data class Validation(val message: String) : AppError
    data class Unauthorized(val message: String = "Unauthorized") : AppError
    data class Unknown(val throwable: Throwable) : AppError
}
```

Map technical errors into user-safe UI messages in one place.

Example:

```kotlin
data class UiMessage(val value: String) {
    companion object {
        fun from(throwable: Throwable): UiMessage {
            return UiMessage(throwable.message ?: "Something went wrong")
        }
    }
}
```

Rules:
- never expose raw backend/internal errors directly to the UI
- map errors at boundaries
- keep error types explicit for business-sensitive flows

---

## 18. Where to place common code

Use this rule:

### Put code in a core/shared module only if:
- it is used by multiple features
- it has stable meaning
- it does not depend on feature-specific context

### Keep code inside the feature if:
- it is only used by one feature
- naming only makes sense inside that feature
- it may change with product behavior

This avoids “core module bloat”.

---

## 19. Rules for ViewModels

- one ViewModel per screen or route
- keep ViewModel focused on screen state orchestration
- no Android `Context`
- no direct Retrofit/Room usage
- no UI widgets or framework references
- use use cases/repositories instead of talking to data sources directly
- expose immutable state
- accept actions through methods or an action dispatcher

Good:
- `ProfileViewModel`
- `CheckoutViewModel`
- `HomeViewModel`

Avoid:
- one ViewModel shared by many unrelated screens
- giant “global app viewmodel”

---

## 20. Rules for repositories

- one repository per business aggregate or data responsibility
- hide source details
- map data before exposing it
- cache intentionally
- avoid leaking DTO/entity types
- repositories should not become giant service locators

Good:
- `AuthRepository`
- `ProfileRepository`
- `CartRepository`

Avoid:
- `MainRepository`
- `AppRepository`
- `CommonRepository`

---

## 21. Rules for use cases

A use case should:
- have one clear purpose
- be named as an action
- be easy to test
- stay small
- avoid mutable internal state

Good:
- `PlaceOrderUseCase`
- `ObserveCartUseCase`
- `SyncProfileUseCase`

Avoid:
- `UserUseCase`
- `AppUseCase`
- `MainInteractor`

---

## 22. Static analysis and formatting

Strongly recommended:
- **ktlint** or **Spotless**
- **detekt**
- consistent IDE formatting rules
- PR template with architecture checklist

Suggested checklist:
- no direct data source access from UI
- no DTO exposed to UI
- no giant ViewModel
- tests added for use case / VM changes
- new code follows feature package/module pattern

---

## 23. CI checks to run on every pull request

Recommended minimum:
- compile
- unit tests
- static analysis
- formatting validation

Recommended Gradle tasks:
```bash
./gradlew build
./gradlew test
./gradlew lint
```

Add instrumented tests for release-critical flows on protected branches or nightly CI if runtime cost is high.

---

## 24. Decision table by app type

## 24.1 If your app is offline-first
Add:
- local-first repository strategy
- sync engine
- conflict resolution policy
- WorkManager jobs
- cache expiration rules

## 24.2 If your app is chat/realtime
Add:
- websocket or realtime data source
- event store
- optimistic UI handling
- message delivery state machine
- stronger retry policy

## 24.3 If your app is e-commerce
Add:
- cart module
- checkout module
- pricing strategy
- promotion strategy
- inventory sync boundaries

## 24.4 If your app is fintech/healthcare
Add:
- encryption boundaries
- audit logging
- stricter error types
- permission and session policies
- stronger testing and review rules

## 24.5 If your app is white-label/multi-brand
Add:
- brand config module
- theme abstraction
- build flavor strategy
- environment config boundaries

## 24.6 If your app is SDK-heavy
Add:
- adapter layer per SDK
- wrapper interfaces
- isolation tests
- fallback strategies when SDK behavior changes

---

## 25. Default team standard I recommend

If you want one strong default, use this:

- **Kotlin-first**
- **Compose-first**
- **Single-activity app**
- **Feature-first structure**
- **Hilt**
- **Coroutines + Flow**
- **Repository pattern**
- **Use cases only where logic is complex or reused**
- **Thin base classes only**
- **Mappers at boundaries**
- **Detekt + formatter + CI**
- **Multi-module only when team/app size justifies it**

---

## 26. Practical starter blueprint

If I were starting your project today, I would use:

```text
:app
:core:common
:core:designsystem
:core:network
:core:database
:core:testing
:feature:auth
:feature:home
:feature:profile
:feature:settings
```

And inside each feature:

```text
presentation/
domain/
data/
di/
```

That gives you clean boundaries without overengineering.

---

## 27. Red flags to avoid

- giant base classes
- one repository for everything
- DTOs directly rendered in UI
- business logic inside composables/fragments/activities
- screen logic inside adapters
- direct Retrofit calls from ViewModel
- using `utils` as a dumping ground
- creating domain layer for trivial apps just because a blog said so
- abstracting too early
- mixing feature code across many unrelated packages

---

## 28. Questions that affect the final structure

Answer these before locking the architecture:

1. Is the app **new** or **legacy**?
2. Are you using **Compose** or **XML/ViewBinding**?
3. Is the app expected to be **small**, **medium**, or **large** in 12 months?
4. Will it be **offline-first**?
5. Does it need **realtime** features?
6. Is it **white-label** or multi-brand?
7. Are there many **third-party SDKs**?
8. Do you need to keep **Java** for a long time, or can you migrate toward Kotlin?

---

## 29. Final recommendation

Do not chase a “perfect architecture”.
Choose one clear standard the whole team can follow.

The winning formula is usually:

- clear feature boundaries
- explicit dependencies
- simple ViewModels
- repositories as data entry points
- optional use cases for meaningful business logic
- thin shared abstractions
- good tests
- good documentation
- consistency across features

Consistency across the team is usually more valuable than an overly clever architecture.

---

## 30. References

- [Android Architecture Recommendations](https://developer.android.com/topic/architecture/recommendations)
- [Android UI Layer Guide](https://developer.android.com/topic/architecture/ui-layer)
- [Android Data Layer Guide](https://developer.android.com/topic/architecture/data-layer)
- [Android Domain Layer Guide](https://developer.android.com/topic/architecture/domain-layer)
- [Android App Modularization Guide](https://developer.android.com/topic/modularization)
- [Dependency Injection in Android](https://developer.android.com/training/dependency-injection)
- [Hilt on Android](https://developer.android.com/training/dependency-injection/hilt-android)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Kotlin Flow Guide](https://kotlinlang.org/docs/flow.html)
- [Android Testing Overview](https://developer.android.com/training/testing)
- [Android Testing Strategies](https://developer.android.com/training/testing/fundamentals/strategies)

---

## 31. One-line summary for the team

> Build by feature, keep UI dumb, keep business logic explicit, hide data behind repositories, use base classes sparingly, and prefer simple consistent patterns over clever abstractions.
