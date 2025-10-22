# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CineSync is an Android application built with Jetpack Compose that allows users to browse popular movies and manage their movie lists. The app implements authentication, movie browsing with lazy loading, and integrates with a backend API.

**Technology Stack:**
- Kotlin 2.2.20
- Jetpack Compose (Material3)
- Retrofit 3.0.0 for API communication
- Kotlinx Serialization for JSON parsing
- OkHttp 5.2.1 with logging interceptor
- Navigation Compose for navigation
- Coil for image loading
- EncryptedSharedPreferences for secure token storage

## Build Commands

**Build the project:**
```bash
./gradlew build
```

**Run tests:**
```bash
# Unit tests
./gradlew test

# Instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests "com.mason.cinesync.ExampleUnitTest"
```

**Install on device/emulator:**
```bash
./gradlew installDebug
```

**Clean build:**
```bash
./gradlew clean
```

**Check dependencies:**
```bash
./gradlew dependencies
```

## Architecture

The app follows **MVVM (Model-View-ViewModel)** architecture with the following layers:

### Layer Structure

**UI Layer (`ui/`):**
- Jetpack Compose screens in `ui/screen/`
- Navigation handled by `navigation/NavGraph.kt`
- Theme configuration in `ui/theme/`

**ViewModel Layer (`viewmodel/`):**
- ViewModels manage UI state using StateFlow
- Factory classes for ViewModel instantiation with dependencies
- Example: `PopularMoviesViewModel` uses sealed class `PopularMoviesUiState` for state management

**Repository Layer (`repository/`):**
- Single source of truth for data operations
- Repositories call service interfaces and handle data transformation
- Example: `MovieRepository`, `AuthRepository`, `UsersRepository`

**Service Layer (`service/`):**
- Retrofit service interfaces defining API endpoints
- All methods are suspend functions for coroutine support
- Example: `MovieService`, `AuthService`, `UsersService`

**Model Layer (`model/`):**
- DTOs in `model/dto/` using kotlinx.serialization
- Enums in `model/enum/`

### Authentication Flow

**Token Management (`token/TokenManager`):**
- Singleton object managing JWT tokens
- Uses EncryptedSharedPreferences for secure storage
- Must be initialized in `CineSyncApplication.onCreate()`
- Provides `logoutFlow: SharedFlow<Unit>` for logout event propagation
- Key methods: `saveToken()`, `getToken()`, `hasValidToken()`, `clearAll()`, `notifyLogout()`

**Auth Interceptor (`interceptor/AuthInterceptor`):**
- Automatically adds `Authorization: Bearer <token>` header to requests
- Handles 401 responses by clearing token and triggering logout flow
- Integrated into OkHttp client in `RetrofitInstance`

**Navigation (`navigation/NavGraph.kt`):**
- Routes defined in `Routes` object: `LOGIN`, `REGISTER`, `MOVIES`
- Start destination determined by `TokenManager.hasValidToken()`
- Listens to `TokenManager.logoutFlow` to navigate to login on logout/401

**Flow:**
1. User logs in → AuthRepository saves token via TokenManager
2. All API requests automatically include token via AuthInterceptor
3. If 401 received → Interceptor clears token and calls `TokenManager.notifyLogout()`
4. NavGraph observes logoutFlow and navigates to login screen

### Retrofit Configuration

**RetrofitInstance (`retrofit/RetrofitInstance`):**
- BASE_URL: `http://10.0.2.2:8080/` (Android emulator localhost)
- Configured with kotlinx.serialization converter
- OkHttpClient includes AuthInterceptor and logging interceptor (debug only)
- Timeout: 30 seconds for connect/read/write
- Use `RetrofitInstance.createService<T>()` to create service instances

### Lazy Loading Pattern

The app implements pagination for movie lists:
- ViewModel tracks current page, total pages, and loading state
- `loadNextPage()` function prevents duplicate loading
- Success state maintains accumulated list: `(currentMovies + newResults).distinctBy { it.id }`
- Error state preserves previously loaded movies
- UI triggers loading when scrolling near end of list

## Key Files

- `CineSyncApplication.kt` - Application class, initializes TokenManager
- `MainActivity.kt` - Single activity hosting Compose navigation
- `navigation/NavGraph.kt` - Navigation graph with auth flow handling
- `retrofit/RetrofitInstance.kt` - Singleton Retrofit configuration
- `token/TokenManager.kt` - Secure token storage and logout event management
- `interceptor/AuthInterceptor.kt` - Automatic token injection and 401 handling

## Important Notes

- **Android Emulator Backend URL:** Use `10.0.2.2` instead of `localhost` to reach host machine
- **TokenManager Initialization:** Must call `TokenManager.init(context)` before any token operations
- **Network Security:** Custom network security config at `res/xml/network_security_config.xml` allows cleartext traffic (development only)
- **Min SDK:** 24 (Android 7.0), Target SDK: 36
- **All repository methods use suspend functions** - must be called from coroutines
- **ViewModel State Management:** Use sealed classes for UI states (Loading, Success, Error)
