# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Fast Laps is a Wear OS (Android) Formula 1 app for smartwatches. It provides race results, championship standings, full season calendar, F1 news, Tiles, Complications, and session notifications. Published on Google Play Store as `com.leandro.fastlaps`.

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

- **ViewModel**: `RaceViewModel` is the single, centralized ViewModel managing all state via multiple `StateFlow` fields (races, standings, news, loading/error states) with in-memory caching (5-30 min per data source)
- **Repositories**: `DriverStandingsRepository`, `NewsRepository` abstract the data layer
- **DI**: Manual `ViewModelProvider.Factory` — no Hilt/Dagger
- **Navigation**: Compose Navigation in `AppNavigation.kt` with routes: `mainScreen`, `sessionList`, `calendar`, `pilotList`, `constructors`, `news`, `about`, `raceResults/{round}`
- **UI**: Wear-specific Compose (`ScalingLazyColumn`, `Chip`, `Card`) via `androidx.wear.compose` with rotary bezel scroll support
- **Tiles**: `NextRaceTileService` (countdown), `StandingsTileService` (top 5 drivers) using ProtoLayout Material
- **Complications**: `NextRaceComplicationService`, `ChampionshipComplicationService` for watch face data
- **Notifications**: `SessionNotificationWorker` via WorkManager, alerts 15 min before and at session start

### Package Namespace

Source files live under `com.example.fastlaps.presentation` but the build namespace and applicationId are `com.leandro.fastlaps`. Resource imports use `com.leandro.fastlaps.R`. This mismatch is intentional via Gradle's `namespace` config.

### API Integration

Two data sources:

| Source | Base URL | Purpose |
|--------|----------|---------|
| Jolpica F1 API (Ergast-compatible) | `https://api.jolpi.ca/ergast/` | Race schedule, results, driver & constructor standings |
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
- **Current version**: 3.0 (versionCode 14)

## Key Patterns

- Screens use `collectAsState()` to observe ViewModel StateFlows
- All screens support rotary bezel scroll via `onRotaryScrollEvent` + `FocusRequester`
- Error handling follows a pattern of `*ErrorState` StateFlows paired with retry callbacks
- Loading states are per-data-source (`isLoading`, `isLoadingDrivers`, etc.)
- ViewModel caches data with timestamps (`forceRefresh` parameter bypasses cache)
- News URLs open on paired phone via `RemoteActivityHelper`
- Team colors and country flags are centralized in `F1Constants` object
- Tiles use `CallbackToFutureAdapter` for async data fetching
- Complications use `SuspendingComplicationDataSourceService` for coroutine support
- Notifications use `WorkManager` periodic worker (15 min interval)
