# Skill: Kilo Android App

## Description
Embed Kilo CLI arm64 binary in Android app via libtermux-android, run `kilo serve` as server, provide mobile UI control.

## Key Technologies
- libtermux-android for embedded Linux environment
- Kilo CLI arm64 binary for agent execution
- Kotlin/Jetpack Compose for Android UI
- WebSocket/SSE for client-server communication

## Architecture
```
Android App
├── LibTermux (embedded Linux)
│   ├── kilo binary (arm64)
│   ├── kilo serve --port 0 (dynamically allocated)
│   └── Shared preferences for config
├── TCP Server Socket
│   └── Receives HTTP/SSE requests from Activity UI
└── Activity (Jetpack Compose)
    ├── TerminalView (optional)
    ├── Chat UI
    └── Connection manager
```

## Implementation Steps
1. Create LibTermux integration module
2. Download and extract Kilo arm64 binary to assets
3. Initialize Termux and install kilo on startup
4. Start `kilo serve` as background service
5. Build Activity UI to connect to local server
6. Handle authentication via KILO_API_KEY env var