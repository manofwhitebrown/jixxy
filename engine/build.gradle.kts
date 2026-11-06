// Pure Kotlin/JVM module. No android.* imports allowed here (ENGINE rule).
plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin { jvmToolchain(17) }

dependencies {
    // Flow is used by SettingsRepository.
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

// Validates the line bank (ids, length, slots, banned topics). Run: ./gradlew :engine:validateLines
tasks.register<JavaExec>("validateLines") {
    group = "verification"
    description = "Validates app/src/main/assets/lines/** against the content rules."
    classpath = project.extensions.getByType(org.gradle.api.tasks.SourceSetContainer::class.java)["main"].runtimeClasspath
    mainClass.set("app.jixxy.engine.tools.ValidateLinesKt")
    args(rootProject.file("app/src/main/assets/lines").absolutePath, "--coverage")
}
