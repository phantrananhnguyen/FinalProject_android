plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.finalproject_android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.finalproject_android"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    defaultConfig {
        vectorDrawables.useSupportLibrary = true
    }
}

dependencies {

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    implementation("org.mapsforge:mapsforge-map-android:0.14.0")
    implementation("org.mapsforge:mapsforge-themes:0.14.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
}