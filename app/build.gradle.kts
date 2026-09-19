import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.watchface.idtool"
    compileSdk = 36
    buildToolsVersion = "36.0.0"

    defaultConfig {
        applicationId = "com.watchface.idtool"
        minSdk = 24
        targetSdk = 36
        versionCode = 39
        versionName = "3.9"

        // 微验 SDK 仅提供 arm64-v8a 的 libwyverify.so
        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("keystore/watchface.jks")
            storePassword = "android"
            keyAlias = "watchface-key"
            keyPassword = "android"
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.findByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1,NOTICE}"
            excludes += "/META-INF/versions/9/**"
        }
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2025.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("dev.rikka.shizuku:api:13.1.5")
    implementation("dev.rikka.shizuku:provider:13.1.5")
    // 液态玻璃真实背景模糊：固定使用 haze 1.6.10（Maven Central 稳定版）。
    // 说明：2.0.0-rc01 在 Maven Central 上的产物没有 blurEffect{} 构建器
    // （那是本地私有构建才有的 API），CI 上必然编译失败；1.6.10 的
    // hazeEffect { blurRadius/tints/noiseFactor } 属性式 API 已验证可构建，
    // 且方状亮块问题由「effect 之后紧跟 clip」的方案修复（见 Components.kt）。
    implementation("dev.chrisbanes.haze:haze:1.6.10")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
