# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Fast Laps is a Wear OS (Android) Formula 1 app for smartwatches. It provides live race positions, championship standings, and F1 news. Published on Google Play Store as `com.leandro.fastlaps`.

## Development Commands

All commands run from the project root (the `gradlew` wrapper is at the root level).

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew bundleRelease          # Create release AAB for Play Store
./gradlew installDebug           # Install debug build on connected device/emulator
./gradlew clean                  # Clean build artifacts
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests on device
```

Note: There are currently no test files in the project. The test commands are listed for when tests are added.

## Architecture

### MVVM with Jetpack Compose for Wear OS

Single-module app (`app/`) using MVVM pattern:

- **ViewModel**: `RaceViewModel` is the single, centralized ViewModel managing all state via multiple `StateFlow` fields (positions, standings, news, loading/error states)
- **Repositories**: `SessionRepository`, `DriverStandingsRepository`, `NewsRepository` abstract the data layer
- **DI**: Manual `ViewModelProvider.Factory` — no Hilt/Dagger
- **Navigation**: Compose Navigation in `AppNavigation.kt` with routes: `mainScreen`, `sessionList`, `pilotList`, `constructors`, `news`, `about`, `sessionResults/{sessionKey}`
- **UI**: Wear-specific Compose (`ScalingLazyColumn`, `Chip`, `Card`) via `androidx.wear.compose`

### Package Namespace

Source files live under `com.example.fastlaps.presentation` but the build namespace and applicationId are `com.leandro.fastlaps`. Resource imports use `com.leandro.fastlaps.R`. This mismatch is intentional via Gradle's `namespace` config.

### API Integration

Three data sources, each with its own Retrofit instance:

| Source | Base URL | Purpose |
|--------|----------|---------|
| OpenF1 API | `https://api.openf1.org/` | Real-time sessions, drivers, positions |
| Ergast API (Jolpica mirror) | `https://api.jolpi.ca/ergast/` | Historical driver & constructor standings |
| Motorsport.com RSS | `motorsport.com/rss/f1/news/` | F1 news (EN & ES feeds) |

Network stack: Retrofit 2.9.0 + OkHttp + Gson + Coroutines.

### Internationalization

English/Spanish toggle stored in SharedPreferences. Language switch triggers `activity.recreate()` to apply the new locale. String resources in `values/strings.xml` (EN) and `values-es/strings.xml` (ES).

## Build Configuration

- **Min SDK**: 30 (Wear OS requirement)
- **Target/Compile SDK**: 35
- **Kotlin**: 2.0.21 with Compose compiler
- **Gradle**: 8.11.1 wrapper, AGP 8.9.2
- **Version catalog**: `gradle/libs.versions.toml`
- **Standalone Wear app**: `wearAppUnbundled=true`
- **Current version**: 1.63 (versionCode 12)

## Key Patterns

- Screens use `collectAsState()` to observe ViewModel StateFlows
- Error handling follows a pattern of `*ErrorState` StateFlows paired with retry callbacks
- Loading states are per-data-source (`isLoading`, `isLoadingDrivers`, etc.)
- News URLs open on paired phone via `RemoteActivityHelper`
- Team colors are hardcoded in `DriverStandingItem` and `ConstructorStandingItem`
