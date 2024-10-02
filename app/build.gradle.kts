import com.android.build.gradle.BaseExtension
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)

    id("com.google.gms.google-services")
    id("jacoco")

}

tasks.withType<Test> {
    extensions.configure(JacocoTaskExtension::class) {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val apiKey: String = localProperties.getProperty("MAPS_API_KEY") ?: ""

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("keystore.properties")
// Si le fichier keystore existe (= build en local)
if (keystorePropertiesFile.exists()) {
    // Chargement du fichier keystore.properties (non présent sur gitHub)
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}
else{
    // Charger depuis les secrets GitHub (Déclarer dans GitHub -> mon repo -> Settings -> Actions secrets and variables)
    keystoreProperties["storeFile"] = System.getenv("KEYSTORE_PATH")
        ?: System.getProperty("storeFile") // Si défini comme paramètre dans Gradle
    keystoreProperties["storePassword"] = System.getenv("KEYSTORE_PASSWORD")
        ?: System.getProperty("storePassword")
    keystoreProperties["keyAlias"] = System.getenv("KEY_ALIAS")
        ?: System.getProperty("keyAlias")
    keystoreProperties["keyPassword"] = System.getenv("KEY_PASSWORD")
        ?: System.getProperty("keyPassword")
}

// val key: String = keystoreProperties.getProperty("maclé") ?: ""

android {
    signingConfigs {
        create("release") {
            storeFile =
                file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword =  keystoreProperties.getProperty("keyPassword")
        }
    }
    namespace = "com.openclassrooms.p15_eventorias"
    compileSdk = 34

    testCoverage {
        version = "0.8.8"
    }

    defaultConfig {
        applicationId = "com.openclassrooms.p15_eventorias"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        //testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunner = "com.openclassrooms.p15_eventorias.CustomTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Add the API key from local.properties to BuildConfig
        buildConfigField("String", "MAPS_API_KEY", "\"$apiKey\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = true // code obfusqué (https://developer.android.com/build/shrink-code?hl=fr)

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

val androidExtension = extensions.getByType<BaseExtension>()

// TODO Denis : Tu saurais exclure les classes Hilt du rapport de couverture + lecture du compte-rendu
// + pour le rendu, il est demandé un rapport de test. J'ai mis test unitaire / instrumenté / couverture
val jacocoTestReport by tasks.registering(JacocoReport::class) {
    dependsOn("testDebugUnitTest", "createDebugCoverageReport")
    group = "Reporting"
    description = "Generate Jacoco coverage reports"

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val debugTree = fileTree("${buildDir}/tmp/kotlin-classes/debug")
    val mainSrc = androidExtension.sourceSets.getByName("main").java.srcDirs

    classDirectories.setFrom(debugTree)
    sourceDirectories.setFrom(files(mainSrc))
    executionData.setFrom(fileTree(buildDir) {
        include("**/*.exec", "**/*.ec")
    })
}

dependencies {

    //kotlin
    implementation(platform(libs.kotlin.bom))

//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    //implementation(libs.androidx.espresso.contrib)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    androidTestImplementation(libs.androidx.espresso.contrib)

    implementation(libs.kotlinx.coroutines.android)

    // Google Fonts certificates (consider alternatives if this approach changes)
    //implementation("androidx.compose.material:material-icons-extended:1.7.1")

    //DI
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Coil = affichage d'URL dans un champ Image (comme Glide)
    implementation(libs.coil.compose)

    // Firebase

    // Import the Firebase BoM
    // garantit que toutes les bibliothèques Firebase utilisées dans un projet sont compatibles entre elles.
    implementation(platform(libs.firebase.bom))

    // Authentification
    implementation(libs.firebase.ui.auth)

    // Firestore = base de données NoSQL
    implementation(libs.firebase.firestore)

    // Firebase Storage (Stockage des images)
    implementation(libs.firebase.storage)

    // Firebase -> système de notification
    implementation(libs.firebase.messaging)

    // Tests

    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)

    androidTestImplementation(libs.hilt.android.testing)
    //kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.51.1")

    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

}