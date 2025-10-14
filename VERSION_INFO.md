# Version Information

This document lists all dependency versions used in the project.

## Build System

- **Gradle**: 8.0
- **Android Gradle Plugin**: 8.1.4
- **Kotlin**: 1.9.10
- **KSP**: 1.9.10-1.0.13

## Android SDK

- **Compile SDK**: 34
- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 34
- **JDK**: 17

## Core Dependencies

### Compose
- **BOM**: 2023.10.01
- **Compiler**: 1.5.3
- Material3: From BOM
- UI: From BOM
- Icons Extended: Latest from BOM

### Jetpack
- **Core KTX**: 1.12.0
- **Lifecycle Runtime**: 2.6.2
- **ViewModel Compose**: 2.6.2
- **Activity Compose**: 1.8.1
- **Navigation Compose**: 2.7.5
- **Splash Screen**: 1.0.1

### Dependency Injection
- **Hilt**: 2.48
- **Hilt Navigation Compose**: 1.1.0

### Database
- **Room**: 2.6.0

### Asynchronous
- **Coroutines**: 1.7.3

### Widget
- **Glance**: 1.0.0
- **Glance AppWidget**: 1.0.0
- **Glance Material3**: 1.0.0

### Background Tasks
- **WorkManager**: 2.9.0
- **Hilt Work**: 1.1.0

## Compatibility Notes

### Kotlin 1.9.10 Compatibility
- Works with Compose Compiler 1.5.3
- Compatible with Android Gradle Plugin 8.1.4
- KSP version: 1.9.10-1.0.13

### Known Compatible Combinations
✅ **Tested and Working**:
- Kotlin 1.9.10 + Compose 1.5.3 + AGP 8.1.4
- Hilt 2.48 + KSP 1.9.10-1.0.13
- Room 2.6.0 + KSP 1.9.10-1.0.13

## Upgrading Dependencies

When upgrading, check these compatibility requirements:

1. **Kotlin ↔ Compose Compiler**: Must match versions
   - Kotlin 1.9.10 → Compose Compiler 1.5.3
   - Kotlin 1.9.20 → Compose Compiler 1.5.4

2. **Kotlin ↔ KSP**: Must match major.minor version
   - Kotlin 1.9.10 → KSP 1.9.10-x

3. **AGP ↔ Gradle**: Check compatibility matrix
   - AGP 8.1.x → Gradle 8.0+
   - AGP 8.2.x → Gradle 8.2+

## Build Troubleshooting

If you encounter build errors:

1. **Gradle Sync Issues**:
   ```bash
   ./gradlew clean
   ./gradlew --stop
   ./gradlew build
   ```

2. **Version Conflicts**:
   - Check Kotlin version matches Compose compiler
   - Verify KSP version matches Kotlin version
   - Ensure AGP is compatible with Gradle

3. **Cache Issues**:
   ```bash
   ./gradlew clean
   rm -rf .gradle
   rm -rf build
   ./gradlew build
   ```

## References

- [Kotlin-Compose Compatibility](https://developer.android.com/jetpack/androidx/releases/compose-kotlin)
- [AGP-Gradle Compatibility](https://developer.android.com/studio/releases/gradle-plugin)
- [KSP Releases](https://github.com/google/ksp/releases)
