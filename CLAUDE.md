# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Fast Laps is a dual-platform Formula 1 app that provides live race positions, championship standings, and F1 news for both Wear OS (Android) and HarmonyOS smartwatches. The app fetches data from the Ergast API (historical data) and OpenF1 API (real-time data).

## Development Commands

### Android (Wear OS)
Navigate to the `Android/` directory for all Android-related commands:

```bash
cd Android
```

**Build Commands:**
- `./gradlew build` - Build the entire project
- `./gradlew assembleDebug` - Build debug APK
- `./gradlew assembleRelease` - Build release APK/AAB
- `./gradlew bundleRelease` - Create release App Bundle (AAB)

**Development:**
- `./gradlew installDebug` - Install debug build on connected device
- `./gradlew clean` - Clean build artifacts

**Testing:**
- `./gradlew test` - Run unit tests
- `./gradlew connectedAndroidTest` - Run instrumented tests

### HarmonyOS
Navigate to the `HarmonyOS/` directory for HarmonyOS development. The project uses DevEco Studio with hvigor build system.

**Build Commands:**
```bash
cd HarmonyOS
# DevEco Studio handles builds through GUI, but hvigor can be used via CLI
# Build project: handled by DevEco Studio
# Clean: handled by DevEco Studio
```

## Architecture Overview

### Android (Wear OS) Structure
- **Package**: `com.example.fastlaps.presentation` (transitioning to `com.leandro.fastlaps`)
- **Architecture**: MVVM with Jetpack Compose
- **Key Components**:
  - `MainActivity.kt` - Entry point with locale management and theme setup
  - `WearApp.kt` - Main Compose app with navigation setup and ViewModel injection
  - `RaceViewModel.kt` - Centralized state management for race data
  - `AppNavigation.kt` - Navigation between screens using Compose Navigation

### Data Layer (Android)
- **Network**: Retrofit with OkHttp for API calls
  - `ErgastApiService.kt` - Historical F1 data (standings, results)
  - `OpenF1ApiService.kt` - Real-time race positions and telemetry
- **Models**: Data classes for API responses (`DriverResponse`, `SessionResponse`, `NewsModel`, etc.)
- **Repositories**: Abstract data access layer for ViewModels (`DriverStandingsRepository`, `NewsRepository`, `SessionRepository`)

### UI Layer (Wear OS Optimized)
- **Screens**: MainScreen, PilotsScreen, ConstructorsScreen, SessionListScreen, SessionResultsScreen, NewsScreen, AboutScreen
- **Components**: Reusable UI components (`DriverStandingItem`, `NewsCard`, `LoadingIndicator`, `ErrorMessage`, etc.)
- **Theme**: Wear OS specific theming with FastlapsTheme
- **Navigation**: Compose Navigation with deep linking support

### Key Features (Android)
- **Internationalization**: English/Spanish language toggle with activity recreation
- **Real-time Data**: Live race positions during F1 events via OpenF1 API
- **News Integration**: RSS feed parsing for F1 news from Motorsport.com
- **Wear OS Integration**: Optimized for small screens, rotary input, and touch gestures
- **Remote Interactions**: Opens URLs on paired phone via RemoteActivityHelper

### Dependencies Management (Android)
- Version catalog in `gradle/libs.versions.toml`
- Wear OS specific Compose libraries (`compose-material`, `compose-foundation`)
- Retrofit 2.9.0 for networking with Gson converter
- RSS parser for news feeds
- Wear remote interactions for phone integration

### Build Configuration (Android)
- **Min SDK**: 30 (Wear OS requirement)
- **Target SDK**: 35
- **Kotlin**: 2.0.21 with Compose compiler
- **Deployment**: Unbundled Wear app (`wearAppUnbundled=true`)

### HarmonyOS Structure
- **Language**: JavaScript (FA model) - legacy HarmonyOS structure
- **Entry Point**: `entry/src/main/js/MainAbility/app.js`
- **UI**: Traditional HML/CSS/JS architecture
- **Build**: DevEco Studio with hvigor build system
- **Config**: `build-profile.json5` with SDK 5.1.1(19) target, 5.1.0(18) compatibility
- **Internationalization**: `i18n/` folder with en-US.json and zh-CN.json

## Migration Status

The project is actively being migrated from Android Wear OS to HarmonyOS. Current status:
- **Android**: Full feature implementation with all screens and functionality
- **HarmonyOS**: Basic template structure using FA model (older HarmonyOS architecture)
- **Target**: Migrate to Stage model with ArkTS for modern HarmonyOS development

## Development Notes

- The Android version is the primary, fully-featured platform
- HarmonyOS version uses older FA (Feature Ability) model - consider migrating to Stage model with ArkTS
- Both platforms target smartwatch form factors with circular UI optimizations
- Language switching in Android requires activity recreation to apply locale changes
- Real-time data integration depends on F1 session schedules and API availability
- News sources: Motorsport.com (EN) and lat.motorsport.com (ES)
- The app is unofficial and not affiliated with Formula 1