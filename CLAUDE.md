# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**My Workout Routine** is a Fitness Tracker Android application built with modern Android development practices. The app allows users to create and manage workout cards, build training plans (max 4 cards per plan), and track workouts via a home widget with timer functionality.

**Package**: `com.myworkoutroutine`

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material3
- **Architecture**: Clean Architecture, MVVM, Repository Pattern
- **Dependency Injection**: Hilt (Dagger)
- **Database**: Room
- **Concurrency**: Kotlin Coroutines & Flow
- **Background Work**: WorkManager
- **Home Widget**: Glance (Jetpack Glance for widgets)

## Module Architecture

This project uses a multi-module architecture for separation of concerns and scalability:

```
app/                          # Main application module
├── MainActivity.kt           # Entry point with bottom navigation
├── MyWorkoutApp.kt          # Application class with @HiltAndroidApp

core/
├── domain/                   # Business logic layer (pure Kotlin)
│   ├── model/               # Domain entities (WorkoutCard, TrainingPlan, WorkoutSession)
│   ├── repository/          # Repository interfaces
│   └── usecase/             # Use cases for business operations
│
├── data/                    # Data layer implementation
│   ├── local/
│   │   ├── entity/         # Room entities with mappers
│   │   ├── dao/            # Room DAOs
│   │   └── WorkoutDatabase.kt
│   ├── repository/         # Repository implementations
│   └── di/                 # Hilt data module
│
└── ui/                      # Shared UI components
    ├── theme/              # Material3 theme (Carbon Fiber #1A1A1D + Red #E63946)
    └── components/         # Reusable composables

feature/
├── workouts/               # Workout CRUD feature
│   ├── WorkoutsViewModel.kt
│   ├── AddEditWorkoutViewModel.kt
│   ├── WorkoutsScreen.kt
│   └── AddEditWorkoutScreen.kt
│
└── settings/              # Settings feature
    └── SettingsScreen.kt

widget/                    # Home widget implementation
├── WorkoutWidget.kt      # Glance widget
└── actions/              # Widget action callbacks
```

## Key Architecture Patterns

### Clean Architecture Layers

1. **Domain Layer** (`core/domain`): Pure Kotlin, no Android dependencies
   - Contains business entities, repository interfaces, and use cases
   - Use cases encapsulate single business operations
   - Repository interfaces define data contracts

2. **Data Layer** (`core/data`): Implements domain contracts
   - Room entities map to domain models via extension functions
   - Repository implementations use DAOs
   - Hilt provides dependencies via DataModule

3. **Presentation Layer** (`feature/*`): UI and ViewModels
   - ViewModels use use cases (not repositories directly)
   - State flows for reactive UI
   - Compose screens observe ViewModels

### Data Flow

```
UI (Compose) → ViewModel → UseCase → Repository → DAO → Room Database
     ↑                                     ↓
     └──────────── StateFlow/Flow ─────────┘
```

## Build Commands

This is a Gradle-based Android project. Common commands:

### Build the project
```bash
./gradlew build
```

### Clean build
```bash
./gradlew clean build
```

### Run tests
```bash
./gradlew test
```

### Run tests for a specific module
```bash
./gradlew :core:domain:test
./gradlew :feature:workouts:test
```

### Install debug build on device
```bash
./gradlew installDebug
```

### Generate APK
```bash
./gradlew assembleDebug
./gradlew assembleRelease
```

## Important Business Rules

### Training Plans
- **Maximum 4 workout cards per training plan** - enforced in `SaveTrainingPlanUseCase`
- Only one training plan can be marked as "current" at a time
- Setting a new current plan automatically unmarks the previous one

### Workout Cards
- Exercises are stored as comma-separated strings in Room, converted to List<String> in domain
- Difficulty levels: BEGINNER, INTERMEDIATE, ADVANCED
- Duration is stored in minutes as an integer

### Widget
- Uses Jetpack Glance for Compose-style widget development
- Timer functionality designed to integrate with WorkManager
- Widget receiver defined in app module's AndroidManifest.xml

## Theme Colors

The app uses a dark theme with carbon fiber and red accents:

- **Primary Background**: Carbon Fiber `#1A1A1D`
- **Surface**: Carbon Fiber Light `#2D2D30`
- **Primary/Accent**: Fitness Red `#E63946`
- **Text**: Off White `#F5F5F7`

### Color Resources Organization
- **XML Resources**: `core/ui/src/main/res/values/colors.xml` - Centralized color definitions
- **Compose Theme**: `core/ui/theme/Color.kt` - Kotlin Color objects for Compose
- **Theme Application**: `core/ui/theme/Theme.kt` - Material3 ColorScheme
- **Widget Colors**: `widget/src/main/java/.../WidgetColors.kt` - Glance ColorProvider instances

**Best Practice**: Always use color resources from XML or theme colors. Never hardcode color values in composables.

## Dependency Injection (Hilt)

### Module Structure
- `@HiltAndroidApp` on `MyWorkoutApp`
- `@AndroidEntryPoint` on `MainActivity`
- `@HiltViewModel` on all ViewModels
- DataModule (`core/data/di/DataModule.kt`) provides:
  - Room database instance
  - DAOs
  - Repository implementations

### Adding New Features
1. Create use cases in `core/domain/usecase/`
2. Inject use cases into ViewModels via constructor
3. ViewModels automatically injected into Composables via `hiltViewModel()`

## Navigation

The app uses Jetpack Navigation Compose:
- Bottom navigation with 2 tabs: Workouts, Settings
- Navigation graph defined in `MainActivity.kt`
- Routes:
  - `workouts` - Main workout cards list
  - `add_edit_workout/{cardId}` - Add/edit form (cardId=0 for new)
  - `settings` - Settings screen

## Common Development Tasks

### Adding a New Use Case
1. Create interface method in `WorkoutRepository` (if needed)
2. Implement in `WorkoutRepositoryImpl`
3. Create use case class in `core/domain/usecase/`
4. Inject into ViewModel

### Adding a New Screen
1. Create Composable in appropriate `feature/*` module
2. Create ViewModel with `@HiltViewModel`
3. Add route to NavHost in `MainActivity`
4. Update navigation calls

### Modifying Database Schema
1. Update entity in `core/data/local/entity/`
2. Update DAO if queries change
3. Increment database version in `WorkoutDatabase`
4. Add migration if needed (for production)

## Testing Notes

- Unit tests for use cases should mock repository
- ViewModel tests should mock use cases
- Repository tests should use in-memory Room database
- UI tests use `@HiltAndroidTest`

## Widget Development

The widget uses Glance, which provides a Compose-like API:
- Widget content in `WorkoutWidget.kt`
- Actions handled via `ActionCallback` implementations
- Widget metadata in `widget/src/main/res/xml/workout_widget_info.xml`
- Registered in app's AndroidManifest.xml

To update widget, modify Glance composables and use GlanceAppWidget APIs.

## Code Standards and Best Practices

### String Resources
**IMPORTANT**: Never hardcode strings in code. Always use string resources.

- **Location**: Each module has its own `res/values/strings.xml`
- **In Composables**: Use `stringResource(R.string.resource_id)`
- **In ViewModels**: Use `UiText` sealed class (see `core/ui/util/UiText.kt`)
  - `UiText.StringResource(R.string.resource_id)` for localized strings
  - `UiText.DynamicString(value)` for runtime-generated strings
  - Convert to String in Composables with `.asString()`

Example ViewModel error handling:
```kotlin
_uiEvent.emit(
    UiEvent.ShowError(
        UiText.StringResource(R.string.error_name_empty)
    )
)
```

### Color Resources
**IMPORTANT**: Never hardcode color hex values.

- **Compose UI**: Use `MaterialTheme.colorScheme.*`
- **Widget (Glance)**: Use `WidgetColors.*` object
- **Adding New Colors**:
  1. Add to `core/ui/src/main/res/values/colors.xml`
  2. Add Kotlin Color object to `Color.kt`
  3. Map to Material3 theme in `Theme.kt`

### Content Descriptions
Always provide content descriptions for accessibility:
```kotlin
Icon(
    imageVector = Icons.Default.Edit,
    contentDescription = stringResource(R.string.edit)
)
```

### Resource Naming Conventions
- **Strings**: `feature_purpose` (e.g., `workouts_title`, `error_name_empty`)
- **Colors**: `semantic_name` (e.g., `carbon_fiber`, `fitness_red`)
- **Dimensions**: `what_where_size` (e.g., `padding_card_large`)

## Localization

All user-facing text is in string resources, making the app localization-ready:
1. Strings are in `res/values/strings.xml` (default English)
2. To add a language, create `res/values-{lang}/strings.xml`
3. ViewModels use `UiText` for framework-independent localization
