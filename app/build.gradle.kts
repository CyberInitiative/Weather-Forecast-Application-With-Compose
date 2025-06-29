plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.1.10"
    id("com.google.devtools.ksp")

    // TODO remove later maybe
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.weathercompose"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.weathercompose"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // https://mvnrepository.com/artifact/androidx.glance/glance
    implementation(libs.androidx.glance)
    // https://mvnrepository.com/artifact/androidx.glance/glance-appwidget
    implementation(libs.androidx.glance.appwidget)
    // For interop APIs with Material 3
    implementation (libs.androidx.glance.material3)
    // For interop APIs with Material 2
    implementation (libs.androidx.glance.material)

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-android
    implementation(libs.kotlinx.coroutines.android)
    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation(libs.gson)
    // https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-gson
    implementation(libs.converter.gson)
    // https://mvnrepository.com/artifact/com.squareup.retrofit2/retrofit
    implementation(libs.retrofit)
    // https://mvnrepository.com/artifact/io.insert-koin/koin-android
    implementation(libs.koin.android.v404)
    // https://mvnrepository.com/artifact/io.insert-koin/koin-androidx-compose
    implementation(libs.koin.androidx.compose.v404)
    // https://mvnrepository.com/artifact/io.insert-koin/koin-androidx-workmanager
    implementation(libs.koin.androidx.workmanager)
    // https://mvnrepository.com/artifact/androidx.navigation/navigation-compose
    implementation(libs.androidx.navigation.compose)
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json
    implementation(libs.kotlinx.serialization.json)
    // https://mvnrepository.com/artifact/androidx.room/room-runtime
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    // https://mvnrepository.com/artifact/androidx.constraintlayout/constraintlayout-compose
    implementation(libs.androidx.constraintlayout.compose)
    // https://mvnrepository.com/artifact/androidx.datastore/datastore-preferences
    implementation(libs.androidx.datastore.preferences)
    // https://mvnrepository.com/artifact/androidx.datastore/datastore-core
    implementation(libs.androidx.datastore.core)
    // https://mvnrepository.com/artifact/androidx.work/work-runtime-ktx
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.junit.ktx)

    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // https://mvnrepository.com/artifact/androidx.room/room-testing
    androidTestImplementation(libs.room.testing)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}