# AGENTS.md

## Cursor Cloud specific instructions

### Project Overview

Android Wi-Fi Direct (P2P) file transfer application. Single-module Gradle project (`app/`), Java-only, using AGP 3.6.3 and Gradle 5.6.4.

### Environment Requirements

- **JDK 8** is required (AGP 3.6.3 is incompatible with JDK 11+). Installed at `/usr/lib/jvm/java-8-openjdk-amd64`.
- **Android SDK** is installed at `/opt/android-sdk` with platform `android-29` and `build-tools;29.0.3`.
- Environment variables (`JAVA_HOME`, `ANDROID_HOME`, `ANDROID_SDK_ROOT`) are configured in `~/.bashrc`.

### Common Commands

| Task | Command |
|---|---|
| Build debug APK | `./gradlew assembleDebug` |
| Run unit tests | `./gradlew test` |
| Run lint | `./gradlew lint` |
| Clean build | `./gradlew clean` |

### Known Gotchas

- **Lint has pre-existing errors**: `./gradlew lint` fails with exit code 1 due to missing `ACCESS_FINE_LOCATION` permission annotations on `WifiP2pManager` calls. These are existing issues in the codebase, not regressions.
- **No emulator-based E2E testing**: The app relies on Wi-Fi Direct (P2P), which is not supported by standard Android emulators. True E2E testing requires two physical Android devices.
- **sdkmanager requires JDK 11+**: If you need to install additional SDK components, temporarily set `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64` for `sdkmanager` commands, then switch back to JDK 8 for Gradle.
- **jcenter deprecation warnings**: The project uses `jcenter()` repository which is in read-only mode. Dependencies still resolve but may emit warnings.
