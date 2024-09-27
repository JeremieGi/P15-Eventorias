import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)

    id("com.google.gms.google-services")

}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val apiKey: String = localProperties.getProperty("MAPS_API_KEY") ?: ""

android {
    namespace = "com.openclassrooms.p15_eventorias"
    compileSdk = 34

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

    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.6.1")

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

}