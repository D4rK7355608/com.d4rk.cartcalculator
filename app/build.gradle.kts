plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.googlePlayServices)
    alias(libs.plugins.googleOssServices)
    alias(libs.plugins.googleFirebase)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.devToolsKsp)
}

android {
    compileSdk = 35
    namespace = "com.d4rk.cartcalculator"
    defaultConfig {
        applicationId = "com.d4rk.cartcalculator"
        minSdk = 23
        targetSdk = 35
        versionCode = 62
        versionName = "1.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations += listOf(
            "en",
            "bg-rBG",
            "de-rDE",
            "es-rGQ",
            "fr-rFR",
            "hi-rIN",
            "hu-rHU",
            "in-rID",
            "it-rIT",
            "ja-rJP",
            "pl-rPL",
            "pt-rBR",
            "ro-rRO",
            "ru-rRU",
            "sv-rSE",
            "th-rTH",
            "tr-rTR",
            "uk-rUA",
            "zh-rTW",
        )
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            multiDexEnabled = true
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile(name = "proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        debug {
            multiDexEnabled = true
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile(name = "proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }


    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    bundle {
        storeArchive {
            enable = true
        }
    }
}

dependencies {
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.perf)

    // Google
    implementation(libs.play.services.ads)
    implementation(libs.billing)
    implementation(libs.material)
    implementation(libs.play.services.oss.licenses)
    implementation(libs.review.ktx)
    implementation(libs.app.update.ktx)
    implementation(libs.volley)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.animation.core)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.graphics.shapes)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.ui.tooling)
    implementation(libs.datastore.preferences)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.navigation.compose)

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.work.runtime.ktx)

    // KSP
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)

    // Kotlin
    implementation(libs.kotlinx.coroutines.android)

    // Image Compression
    implementation(libs.coil.compose)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}