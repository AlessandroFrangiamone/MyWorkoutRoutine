# My Workout Routine

A modern Android fitness tracker application built with Jetpack Compose and Clean Architecture.

## Features

- **Workout Cards CRUD**: Create, read, update, and delete workout cards with exercises, duration, and difficulty levels
- **Training Plans**: Build custom training plans with up to 4 workout cards
- **Current Plan System**: Set and manage your active training plan
- **Home Widget**: Track your workout time with a Glance-powered home screen widget
- **Dark Theme**: Beautiful carbon fiber design (#1A1A1D) with red accents (#E63946)

## Tech Stack

- **Kotlin** - Programming language
- **Jetpack Compose** - Modern declarative UI
- **Material3** - Material Design components
- **Hilt** - Dependency injection
- **Room** - Local database
- **Coroutines & Flow** - Asynchronous programming
- **WorkManager** - Background task scheduling
- **Glance** - Home screen widgets
- **Clean Architecture** - Separation of concerns
- **MVVM** - Presentation pattern

## Project Structure

Multi-module architecture:
- `app` - Main application module
- `core/domain` - Business logic and entities
- `core/data` - Data layer with Room database
- `core/ui` - Shared UI components and theme
- `feature/workouts` - Workout management feature
- `feature/settings` - Settings feature
- `widget` - Home screen widget implementation

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34
- Minimum SDK 26 (Android 8.0)

### Building

```bash
# Clone the repository
git clone <repository-url>

# Build the project
./gradlew build

# Run on device/emulator
./gradlew installDebug
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :core:domain:test
```

## Architecture

The app follows Clean Architecture principles with three main layers:

1. **Domain Layer** (`core/domain`): Business logic, entities, and repository interfaces
2. **Data Layer** (`core/data`): Repository implementations and Room database
3. **Presentation Layer** (`feature/*`): ViewModels and Compose UI

## License

This project is created for educational purposes.
