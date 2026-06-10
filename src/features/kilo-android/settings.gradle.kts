// [Parent Feature/Milestone] Kilo Android App
# [Subtask] Gradle settings for Kilo Android
# [Law Check] 10 lines

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
        maven("https://jitpack.io")
    }
}

rootProject.name = "KiloAndroid"
include(":app")