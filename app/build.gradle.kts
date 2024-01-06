plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.kannan.runningtrack"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kannan.runningtrack"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
        viewBinding = true
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
}

dependencies {

    implementation(libs.andoirdx.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.sdp.android)
    implementation(libs.bundles.navigation.components)
    implementation(libs.bundles.room)
    ksp(libs.room.ksp.complier)
    implementation(libs.hilt.android)
    ksp(libs.bundles.dagger.hilt.ksp)
    implementation(libs.splash)
    implementation(libs.glide)
    ksp(libs.gilde.ksp)
    implementation(libs.timber)
    implementation(libs.bundles.google.maps.location.services)
    implementation(libs.mpchart)

}