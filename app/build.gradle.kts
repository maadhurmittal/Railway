plugins {
    // Android application plugin (using alias from version catalogs or replace with literal)
    alias(libs.plugins.android.application)

    // Kotlin Android plugin
    alias(libs.plugins.kotlin.android)

    // Kotlin Jetpack Compose plugin
    alias(libs.plugins.kotlin.compose)

    // Google Services plugin for Firebase integration
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.railway"   // Your app namespace
    compileSdk = 36                     // SDK version to compile against

    defaultConfig {
        applicationId = "com.example.railway"
        minSdk = 26                     // Minimum Android version your app supports
        targetSdk = 36                  // Targeted Android version

        versionCode = 1                 // Version code for Play Store
        versionName = "1.0"             // Version name for display

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false     // Disable code shrinking for release (enable if you add Proguard rules)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11   // Java 11 compatibility
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"               // JVM target for Kotlin
    }

    buildFeatures {
        compose = true                 // Enable Jetpack Compose
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3" // Match your Compose compiler version
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}" // Avoid packaging license duplicates
        }
    }
}

dependencies {
    // Core AndroidX libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose BOM to manage Compose versions
    implementation(platform(libs.androidx.compose.bom))

    // Compose UI libraries
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    // Material Design 3 (Material You)
    implementation(libs.androidx.material3)

    // Firebase platform BOM, auth, firestore and storage
    implementation(platform("com.google.firebase:firebase-bom:32.1.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation(libs.material) // Keep this if you're using Material Components views alongside Compose

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Compose test libs (match Compose BOM version)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug tools for compose preview and testing
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}