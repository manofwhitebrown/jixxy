// Pure Kotlin/JVM module. No android.* imports allowed here (ENGINE rule).
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin { jvmToolchain(17) }

dependencies {
    // Flow is used by SettingsRepository. Coordinates inline until PLATFORM adds them to the catalog.
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
}
