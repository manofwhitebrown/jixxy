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

    signingConfigs {
        // Throwaway debug key committed on purpose (DEVOPS request): CI builds update in place.
        getByName("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "jixxydebug"
            keyPassword = "android"
        }
        // Release key only exists when the Owner has set these env vars / GitHub secrets.
        val keystoreB64 = System.getenv("JIXXY_KEYSTORE_B64")
        if (!keystoreB64.isNullOrBlank()) {
            create("release") {
                val f = layout.buildDirectory.file("release.keystore").get().asFile
                f.parentFile.mkdirs()
                f.writeBytes(java.util.Base64.getDecoder().decode(keystoreB64.trim()))
                storeFile = f
                storePassword = System.getenv("JIXXY_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("JIXXY_KEY_ALIAS")
                keyPassword = System.getenv("JIXXY_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
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
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.datastore.preferences)
    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.junit)
    testImplementation("org.json:json:20240303") // real org.json for JVM unit tests (android.jar stubs throw)
}
