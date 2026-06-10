// [Parent Feature/Milestone] Kilo Android App
// [Subtask] Root build.gradle.kts
// [Law Check] 15 lines

plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
