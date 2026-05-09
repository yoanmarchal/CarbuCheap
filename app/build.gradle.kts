plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

val releaseStoreFile = System.getenv("RELEASE_STORE_FILE")
    ?: providers.gradleProperty("RELEASE_STORE_FILE").orNull
val releaseStorePassword = System.getenv("RELEASE_STORE_PASSWORD")
    ?: providers.gradleProperty("RELEASE_STORE_PASSWORD").orNull
val releaseKeyAlias = System.getenv("RELEASE_KEY_ALIAS")
    ?: providers.gradleProperty("RELEASE_KEY_ALIAS").orNull
val releaseKeyPassword = System.getenv("RELEASE_KEY_PASSWORD")
    ?: providers.gradleProperty("RELEASE_KEY_PASSWORD").orNull

val hasReleaseSigning = listOf(
    releaseStoreFile,
    releaseStorePassword,
    releaseKeyAlias,
    releaseKeyPassword
).all { !it.isNullOrBlank() }

android {
    namespace = "com.ym.carbucheap"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.ym.carbucheap"
        minSdk = 26
        targetSdk = 37
        versionCode = 4
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = file(releaseStoreFile!!)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            ndk {
                // FULL = extrait les symboles de debug complets des .so (libs tierces incluses)
                // Améliore l'analyse de crash dans Play Console / Android Vitals
                debugSymbolLevel = "FULL"
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    packaging {
        jniLibs {
            keepDebugSymbols += "**/*.so"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    debugImplementation(libs.compose.ui.tooling)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.okhttp.logging)

    // Coroutines
    implementation(libs.coroutines.android)

    // Location
    implementation(libs.play.services.location)

    // Splash
    implementation(libs.splashscreen)

    // DataStore
    implementation(libs.datastore.preferences)

    // Serialization (routes type-safe Navigation Compose)
    implementation(libs.kotlinx.serialization.json)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}