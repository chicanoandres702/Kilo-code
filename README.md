# Kilo Android

Android app that runs Kilo CLI inside an embedded Termux environment, exposing `kilo serve` as a local server.

## Architecture

```
Android App
├── LibTermux (embedded Linux via libtermux-android)
│   ├── kilo-linux-arm64 binary (extracted to assets)
│   ├── kilo serve --port 0 (dynamically allocated)
│   └── Node.js + bash (dependencies)
├── KiloServerService (foreground service)
│   └── Manages kilo serve process
└── MainActivity (Jetpack Compose)
    ├── Chat UI
    └── TerminalView (optional)
```

## Features

- **Embedded Termux**: No root required, runs Linux environment in app private storage
- **Kilo CLI arm64**: Latest binary embedded as asset
- **Local Server**: `kilo serve` runs on dynamic port (0 = auto-assign)
- **Background Service**: Foreground service keeps server alive
- **Mobile UI**: Chat interface for agent interaction

## Requirements

- Android 7.0+ (API 24+)
- arm64 device or emulator
- ~50MB storage for bootstrap + Kilo binary

## Setup

1. Clone repository
2. Download `kilo-linux-arm64.tar.gz` from [Kilo releases](https://github.com/Kilo-Org/kilocode/releases)
3. Extract `kilo` binary to `src/features/kilo-android/assets/`
4. Build with Android Studio or Gradle

## Usage

```bash
# In Android app
1. Launch app
2. Wait for bootstrap installation (~30MB download)
3. Configure API keys in Settings
4. Chat with Kilo agent
```

## Project Structure

```
src/features/kilo-android/
├── app/            # Android UI components
├── lib/            # Core logic
├── assets/         # Embedded Kilo binary
└── utils/          # Helper utilities
```