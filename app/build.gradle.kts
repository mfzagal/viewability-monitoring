plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.meli.viewability.monitoring"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.meli.viewability.monitoring"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "PARTNER_NAME", "\"com.iab.omid.sampleapp\"")
        buildConfigField("String", "VENDOR_KEY", "\"dummyVendor\"")
        buildConfigField("String", "VALIDATION_SCRIPT_URL", "\"complianceomsdk.iabtechlab.com\"")
        buildConfigField("String", "VALIDATION_SCRIPT_URL_OVERRIDE", "\"complianceomsdk.iabtechlab.com\"")
        buildConfigField("String", "VERIFICATION_URL", "\"https://omsdk-public.herokuapp.com/omid-validation-verification-script-v1.js\"")
        buildConfigField("String", "VERIFICATION_PARAMETERS", "\"http://omid-android-reference-app/sendMessage?msg=\"")
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    //OMSDK
    implementation(files("libs/omsdk-android-1.4.10-release.aar"))

    //Hilt
    implementation ("com.google.dagger:hilt-android:2.50")
    kapt("androidx.hilt:hilt-compiler:1.1.0")
    kapt("com.google.dagger:hilt-android-compiler:2.50")

    //Firebase
    implementation (platform("com.google.firebase:firebase-bom:31.4.0"))
    implementation ("com.google.firebase:firebase-firestore-ktx")
    implementation ("com.google.firebase:firebase-perf-ktx")

    //Base
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

kapt {
    correctErrorTypes = true
}