# Kilo Android App - Architecture

## Overview
Android application that embeds Kilo CLI (arm64) inside Termux environment via libtermux-android library.

## Key Components

### 1. LibTermux Integration (`lib/`)
- Creates embedded Linux environment
- Extracts Kilo arm64 binary to app-private storage
- Runs `kilo serve` as background service

### 2. Core Models (`lib/models.kt`)
- `KiloTermuxConfig`: Configuration data class
- `KiloServerState`: Server lifecycle states
- `CommandResult`: Command output wrapper
- `OutputLine`: Sealed class for streaming output

### 3. UI Layer (`app/`)
- `MainActivity`: Jetpack Compose entry point
- `KiloChatScreen`: Chat interface with agent
- `KiloApplication`: Application singleton

### 4. Service Layer (`app/KiloServerService.kt`)
- Foreground service for persistent server
- Handles server lifecycle
- Manages port allocation

### 5. Settings (`utils/settings.kt`)
- Encrypted storage for API keys
- Using AndroidX Security Crypto

## Directory Structure

```
src/features/kilo-android/
├── app/                    # Android app module
│   ├── build.gradle.kts    # App dependencies
│   ├── MainActivity.kt       # Main UI
│   ├── KiloServerService.kt  # Background server
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/kilocli/android/
│       │   ├── ui/theme/Theme.kt
│       │   └── KiloApplication.kt
│       └── res/values/colors.xml
├── lib/                    # Core logic
│   ├── models.kt           # Data classes
│   └── kilotermux.kt       # Core integration
├── assets/                 # Embedded Kilo binary (kilo-linux-arm64)
├── scripts/                # Build scripts
│   └── download-kilo.sh    # Fetch latest Kilo binary
├── build.gradle.kts        # Project config
└── settings.gradle.kts       # Module includes
```

## Getting Started

1. Download Kilo binary: `./scripts/download-kilo.sh`
2. Open in Android Studio
3. Build and deploy to arm64 device/emulator
4. Configure API key in app settings
5. Start chatting with Kilo agent

## Notes

- Binary must be extracted from `kilo-linux-arm64.tar.gz`
- Uses JitPack for libtermux-android
- Foreground service keeps server alive when minimized