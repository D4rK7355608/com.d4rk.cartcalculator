import java.util.Properties

plugins {
    alias(notation = libs.plugins.androidApplication)
    alias(notation = libs.plugins.jetbrainsKotlinAndroid)
    alias(notation = libs.plugins.jetbrainsKotlinParcelize)
    alias(notation = libs.plugins.kotlin.serialization)
    alias(notation = libs.plugins.googlePlayServices)
    alias(notation = libs.plugins.googleFirebase)
    alias(notation = libs.plugins.compose.compiler)
    alias(notation = libs.plugins.devToolsKsp)
    alias(notation = libs.plugins.about.libraries)
    alias(notation = libs.plugins.mannodermaus)
}

android {
    compileSdk = 35
    namespace = "com.d4rk.cartcalculator"
    defaultConfig {
        applicationId = "com.d4rk.cartcalculator"
        minSdk = 23
        targetSdk = 35
        versionCode = 87
        versionName = "1.2.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        @Suppress("UnstableApiUsage")
        androidResources.localeFilters += listOf(
            "en" ,
            "bg-rBG" ,
            "de-rDE" ,
            "es-rGQ" ,
            "fr-rFR" ,
            "hi-rIN" ,
            "hu-rHU" ,
            "in-rID" ,
            "it-rIT" ,
            "ja-rJP" ,
            "pl-rPL" ,
            "pt-rBR" ,
            "ro-rRO" ,
            "ru-rRU" ,
            "sv-rSE" ,
            "th-rTH" ,
            "tr-rTR" ,
            "uk-rUA" ,
            "zh-rTW" ,
        )
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release")

        val signingProps = Properties()
        val signingFile = rootProject.file("signing.properties")

        if (signingFile.exists()) {
            signingProps.load(signingFile.inputStream())

            signingConfigs.getByName("release").apply {
                storeFile = file(signingProps["STORE_FILE"].toString())
                storePassword = signingProps["STORE_PASSWORD"].toString()
                keyAlias = signingProps["KEY_ALIAS"].toString()
                keyPassword = signingProps["KEY_PASSWORD"].toString()
            }
        } else {
            android.buildTypes.getByName("release").signingConfig = null
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
        }
        debug {
            isDebuggable = true
        }
    }

    buildTypes.forEach { buildType ->
        with(buildType) {
            isMinifyEnabled = false
            isShrinkResources = false
            multiDexEnabled = true
            proguardFiles(getDefaultProguardFile(name = "proguard-android-optimize.txt") , "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
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

    // App Core
    implementation(dependencyNotation = "com.github.D4rK7355608:AppToolkit:0.0.75") {
        isTransitive = true
    }

    // Compression
    implementation(dependencyNotation = libs.msgpack.core)
    implementation(dependencyNotation = libs.lz4.java)

    // KSP
    ksp(dependencyNotation = libs.androidx.room.compiler)
    implementation(dependencyNotation = libs.androidx.room.ktx)
    implementation(dependencyNotation = libs.androidx.room.runtime)

    // Test
    testImplementation(dependencyNotation = libs.bundles.testing)
    debugImplementation(dependencyNotation = libs.androidx.ui.test.manifest)
    androidTestImplementation(dependencyNotation = libs.bundles.androidTesting)
}