// In your project-level build.gradle.kts (e.g., build.gradle.kts in the root of your project)

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // Note: kapt and google-services plugin IDs are *applied* in the app-level build.gradle.kts,
    // but their plugin definition might be implicitly part of the build.gradle.kts plugins block
    // or declared in settings.gradle.kts for version catalogs.
    // If you explicitly added 'id("com.google.gms.google-services") apply false' here, that's fine too.
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Firebase plugin for Google Services. This classpath is correct here.
        classpath("com.google.gms:google-services:4.3.15") // Keep this version as you specified
        // It's good practice to align the google-services plugin version with the latest stable if possible.
        // As of July 2025, it's 4.4.2
    }
}