plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "app.jixxy.character"
    compileSdk = 36

    defaultConfig { minSdk = 26 }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin { jvmToolchain(17) }
}

dependencies {
    // Enums only (CharacterState, ReactionKind, PauseSpec, PauseChoiceKind). Exposed in the public API, hence `api`.
    api(project(":engine"))
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
}
