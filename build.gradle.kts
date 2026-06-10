// [Parent Feature/Milestone] Kilo Android App
// [Subtask] Root Gradle configuration
// [Law Check] 20 lines

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
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "KiloAndroid"
include(":app")

tasks.register("clean").configure {
    dependsOn("clean")
}