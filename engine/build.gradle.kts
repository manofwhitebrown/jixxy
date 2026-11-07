// Pure Kotlin/JVM module. No android.* imports allowed here (ENGINE rule).
plugins {
    alias(libs.plugins.kotlin.jvm)
    jacoco
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

// Coverage (T-E3-4). Reported, not gated: the number is printed so the team sees it honestly.
// Target >= 85% line coverage on app/jixxy/engine/rules and app/jixxy/engine/selector.
jacoco { toolVersion = "0.8.12" }

tasks.named<Test>("test") { finalizedBy("jacocoTestReport") }

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn("test")
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    doLast {
        val xml = reports.xml.outputLocation.get().asFile
        if (!xml.exists()) return@doLast
        val text = xml.readText()
        fun lineCounter(block: String): String? =
            Regex("""<counter type="LINE" missed="(\d+)" covered="(\d+)"/>""").findAll(block).lastOrNull()?.let {
                val missed = it.groupValues[1].toInt()
                val covered = it.groupValues[2].toInt()
                val total = missed + covered
                if (total == 0) "n/a" else "%.1f%% (%d/%d lines)".format(covered * 100.0 / total, covered, total)
            }
        val rows = mutableListOf("engine total: ${lineCounter(text.substringAfterLast("</package>")) ?: "n/a"}")
        Regex("""<package name="([^"]+)">(.*?)</package>""", RegexOption.DOT_MATCHES_ALL).findAll(text).forEach {
            rows += "${it.groupValues[1].removePrefix("app/jixxy/engine/")}: ${lineCounter(it.groupValues[2]) ?: "n/a"}"
        }
        val summary = rows.joinToString("\n")
        println("ENGINE COVERAGE\n$summary")
        println("::notice title=Engine coverage::" + rows.joinToString("; "))
        layout.buildDirectory.file("reports/coverage-summary.txt").get().asFile.writeText(summary + "\n")
    }
}
