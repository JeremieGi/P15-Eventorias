import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false

    id("com.google.gms.google-services") version "4.4.2" apply false
    id("org.sonarqube") version "5.1.0.4882"
}

buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:5.1.0.4882")
    }
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val sonarToken: String = localProperties.getProperty("TOKEN_SONAR_CLOUD") ?: System.getenv("TOKEN_SONAR_CLOUD")

sonar {
    properties {
        property("sonar.projectKey", "JeremieGi_P15-Eventorias")
        property("sonar.organization", "jeremieg")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.tests", "src/test/java, src/androidTest/java")
        property("sonar.language", "kotlin")
        property("sonar.sources", "src/main/java, src/main/kotlin")
        property("sonar.junit.reportPaths", "build/test-results/testDebugUnitTest")
        property("sonar.coverage.jacoco.xmlReportPaths","build/reports/coverage/androidTest/debug/connected/report.xml")
        property("sonar.token",sonarToken)

        // Forcer l'échec en cas de fichier manquant
        property("sonar.qualitygate.wait", "true") // Vérifie que les conditions de qualité sont respectées
        property("sonar.scanner.forceFailure", "true") // Fait échouer l'analyse en cas d'erreur
    }
}