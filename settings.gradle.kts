// [Parent Feature/Milestone] Kilo Android App
// [Subtask] Root settings.gradle.kts
// [Law Check] 20 lines

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "KiloAndroid"
include(":app", ":lib")
