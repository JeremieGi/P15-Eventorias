import com.android.build.gradle.BaseExtension
import java.util.Properties
import java.util.Base64

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

// En build sur GitHub Action, les variables d'envirronnement sont créés dans le script yaml
val apiKey: String = localProperties.getProperty("MAPS_API_KEY") ?: System.getenv("MAPS_API_KEY")

val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("keystore.properties")
// Si le fichier keystore existe (= build en local)
if (keystorePropertiesFile.exists()) {
    // Chargement du fichier keystore.properties (non présent sur gitHub)
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}
else{
    // Build depuis GitHub Action
    // Charger depuis les secrets GitHub (Déclarer dans GitHub -> mon repo -> Settings -> Actions secrets and variables)

    keystoreProperties["storePassword"] = System.getenv("KEYSTORE_PASSWORD")
    keystoreProperties["keyAlias"] = System.getenv("KEY_ALIAS")
    keystoreProperties["keyPassword"] = System.getenv("KEY_PASSWORD")

    // le storeFile est stocké dans les secrets mais encodé en base 64, il faut donc le décoder
    val encodedbase64content = System.getenv("KEYSTORE_BASE64")
    val decodedKeystore = Base64.getDecoder().decode(encodedbase64content)

    val keystoreFileTemp = layout.buildDirectory.dir("temp_keystore.jks").get().asFile

    // pour que le répertoire parent existe (sinon erreur "No such file or directory")
    keystoreFileTemp.parentFile.mkdirs()

    // Créer un fichier temporaire pour stocker le keystore décodé
    keystoreFileTemp.writeBytes(decodedKeystore)

    keystoreProperties["storeFile"] = keystoreFileTemp.absolutePath

}

// Le fichier google-services.json est exclu de Git
// Son contenu est dans un secret GitHub KEY_GOOGLE_SERVICES_JSON_CONTENT (non encodé en Base)
// Il faut donc recréer ce fichier en envirronement GitHub
// Vu que je n'ai pas besoin de son contenu dans l'appli (comme par exemple la cléd'APi Google),
// Sa création est faite dans android.yml

//// Créer le fichier google-services.json à partir du secret
//val googleServicesJson : String? = System.getenv("GOOGLE_SERVICES_JSON") // Déclaration explicite du type pouré viter le warning : Declaration has type inferred from a platform call, which can lead to unchecked nullability issues. Specify type explicitly as nullable or non-nullable.
//if (googleServicesJson != null) {
//    // Chemin du fichier google-services.json dans le répertoire app
//    val googleServicesFile = file("${projectDir}/app/google-services.json")
//
//    // Écrire le contenu du secret dans le fichier
//    googleServicesFile.writeText(googleServicesJson)
//}

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

    //val debugTree = fileTree("${buildDir}/tmp/kotlin-classes/debug") => DEPRECATED
    val debugTree = fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug"))
    val mainSrc = androidExtension.sourceSets.getByName("main").java.srcDirs

    classDirectories.setFrom(debugTree)
    sourceDirectories.setFrom(files(mainSrc))
//    executionData.setFrom(fileTree(buildDir) { => DEPRECATED
//        include("**/*.exec", "**/*.ec")
//    })
    executionData.setFrom(fileTree(layout.buildDirectory) {
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