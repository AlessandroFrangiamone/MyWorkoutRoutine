pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MyWorkoutRoutine"
include(":app")
include(":core:domain")
include(":core:data")
include(":core:ui")
include(":feature:workouts")
include(":feature:trainings")
include(":feature:settings")
include(":widget")
