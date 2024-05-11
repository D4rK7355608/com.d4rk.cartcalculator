import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.android.gms.oss-licenses-plugin")
}
android {
    compileSdk = 34
    namespace = "com.d4rk.cartcalculator"
    defaultConfig {
        applicationId = "com.d4rk.cartcalculator"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.0.1"
        archivesName = "${applicationId}-v${versionName}"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations += listOf(
            "en" ,
            "de" ,
            "es" ,
            "fr" ,
            "hi" ,
            "hu" ,
            "in" ,
            "it" ,
            "ja" ,
            "ro" ,
            "ru" ,
            "tr" ,
            "sv" ,
            "bg" ,
            "pl" ,
            "uk"
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
            versionNameSuffix = null
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt") , "proguard-rules.pro"
            )
        }
        debug {
            multiDexEnabled = true
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = true
            versionNameSuffix = null
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt") , "proguard-rules.pro"
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
        viewBinding = true
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
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
    implementation("com.google.android.material:material:1.12.0")
    implementation(libs.play.services.oss.licenses)
    implementation(libs.review.ktx)
    implementation(libs.app.update.ktx)

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
    implementation(libs.ui.tooling)
    implementation("androidx.datastore:datastore-core:1.1.1")
    implementation(libs.androidx.navigation.compose)

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation(libs.androidx.core.splashscreen)
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation(libs.androidx.work.runtime.ktx)

    // Other
    implementation("com.airbnb.android:lottie:6.4.0")
    implementation("me.zhanghai.android.fastscroll:library:1.3.0")

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}