# AGENTS.md

## Cursor Cloud specific instructions

### Project overview

This is **MyWiFiApplication**, an Android app for peer-to-peer file transfer over Wi-Fi Direct (Wi-Fi P2P). It uses Gradle 5.6.4 with Android Gradle Plugin 3.6.3, targeting Android API 29 (min SDK 19).

### Environment requirements

- **JDK 11** (`/usr/lib/jvm/java-11-openjdk-amd64`) — AGP 3.6.3 is incompatible with JDK 17+.
- **Android SDK** at `/opt/android-sdk` with `platforms;android-29` and `build-tools;29.0.3` installed.
- Environment variables must be set (persisted in `~/.bashrc`):
  - `JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64`
  - `ANDROID_HOME=/opt/android-sdk`
  - `ANDROID_SDK_ROOT=/opt/android-sdk`

### Key commands

| Task | Command |
|---|---|
| Build debug APK | `./gradlew assembleDebug` |
| Run unit tests | `./gradlew test` |
| Run lint | `./gradlew lint` |
| Clean build | `./gradlew clean` |

### Non-obvious caveats

- **Lint fails with `abortOnError true` (default)**: The codebase has 4 pre-existing `MissingPermission` lint errors related to Wi-Fi P2P APIs. This is a known issue in the existing code. Running `./gradlew lint` will fail — this is expected. The build (`assembleDebug`) and unit tests succeed normally.
- **`sdkmanager` requires JDK 17+**: If you need to install additional SDK packages, temporarily use `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64` for `sdkmanager` commands, then switch back to JDK 11 for Gradle builds.
- **No Android emulator/device available**: This is a Wi-Fi Direct app that cannot be tested on emulators (Wi-Fi P2P is not supported). The "hello world" for this project is a successful `assembleDebug` producing `app/build/outputs/apk/debug/app-debug.apk`.
- **`jcenter()` repository**: The project uses jcenter which is in read-only mode. Dependencies still resolve but no new packages can be published there.
