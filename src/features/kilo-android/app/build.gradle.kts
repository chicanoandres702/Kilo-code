// [Parent Feature/Milestone] Kilo Android App
// [Subtask] Configure Android project with libtermux-android dependency
// [Law Check] 35 lines

buildscript {
    ext.kotlin_version = "1.9.22"
    dependencies {
        classpath "com.android.tools.build:gradle:8.2.2"
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}

plugins {
    id "com.android.application"
    id "org.jetbrains.kotlin.android"
}

android {
    namespace "com.kilocli.android"
    compileSdk 34

    defaultConfig {
        applicationId "com.kilocli.android"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
        ndk { abiFilters += listOf("arm64-v8a") }
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packagingOptions {
        pickFirst "**/libtermux_jni.so"
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets", "../../assets")
        }
    }
}

dependencies {
    implementation("com.github.libtermux:libtermux-android:1.0.0")
    implementation("com.github.libtermux:terminal-view:1.0.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui:1.6.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.2")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
}