plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
}

android {
    namespace = "ru.hihit.cobuy"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.hihit.cobuy"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField("String", "PUSHER_APP_ID", "\"${project.property("pusher.app_id")}\"")
        buildConfigField("String", "PUSHER_KEY", "\"${project.property("pusher.key")}\"")
        buildConfigField("String", "PUSHER_CLUSTER", "\"${project.property("pusher.cluster")}\"")
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
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

val cameraxVersion = "1.0.2"
dependencies {
    implementation("net.engawapg.lib:zoomable:1.6.2")
    implementation("com.pusher:pusher-java-client:2.4.2")
    implementation("androidx.compose.runtime:runtime:1.6.8")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.8")
    implementation("androidx.compose.runtime:runtime-rxjava2:1.6.8")

    implementation("com.google.accompanist:accompanist-navigation-animation:0.34.0")

    implementation(libs.gson)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.kotlinx.coroutines.core)
    // Retrofit
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.jetbrains.kotlinx.serialization.json)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
// Retrofit with Scalar Converter

    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    //Barcode
    implementation(libs.barcode.scanning)
    //Camera Permission
    implementation(libs.accompanist.permissions)

    implementation(libs.core)
    implementation(libs.google.accompanist.swiperefresh)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.coil.compose)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}