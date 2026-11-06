plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "app.jixxy"
    // API 36 = Android 16; Google Play requires new apps/updates to target 36 from 2026-08-31
    // (https://developer.android.com/google/play/requirements/target-sdk).
    compileSdk = 36

    defaultConfig {
        applicationId = "app.jixxy"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.0.1"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin { jvmToolchain(17) }

    buildFeatures { compose = true }
}

dependencies {
    implementation(project(":engine"))
    implementation(project(":character"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
}
