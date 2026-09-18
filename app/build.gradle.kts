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

    // Kotlin 2.4 已移除 kotlinOptions，改用 compilerOptions（AGP 8.13 两者都还认，
    // 但旧写法会打 deprecation 警告，部分配置下直接报错）。
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
        }
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
    // 注意：core / lifecycle / activity / Compose BOM 必须留在 AGP 8.13.0 能支持的版本上。
    // 更高的版本（core 1.19.0 / lifecycle 2.11.0 / compose-bom 2026.09.00）会要求
    // AGP 9.1.0+ 且 compileSdk 37+，本项目是 AGP 8.13.0 + compileSdk 36，会直接
    // 在 :app:checkDebugAarMetadata 阶段失败。Kotlin 编译器升到 2.4.20 即可满足
    // haze 2.0.0-rc01 的元数据要求，无需连带升级这些 AndroidX 库。
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
    // 液态玻璃真实背景模糊：Haze 库当前最新已发布版本是 2.0.0-rc01（Maven Central
    // 上能查到的最新非 SNAPSHOT 版本；2.0 线仍是 rc 阶段，API 相对 1.x 有破坏性重构，
    // 具体见下方 Components.kt 里的 hazeBlur(input, style) 用法：rc01 的模糊入口是
    // hazeBlur，属性写在 HazeBlurStyle { } 作用域里，不再是 1.x 的 hazeEffect/属性赋值）。
    // Blur 效果在 2.0 起拆成独立的 haze-blur 模块，必须一起引入。
    implementation("dev.chrisbanes.haze:haze:2.0.0-rc01")
    implementation("dev.chrisbanes.haze:haze-blur:2.0.0-rc01")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
