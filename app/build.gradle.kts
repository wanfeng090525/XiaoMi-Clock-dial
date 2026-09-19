import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    // 注意：AGP 9.0 起 Kotlin 支持已内建，不能再 apply 'org.jetbrains.kotlin.android'
    // （否则报 "The 'org.jetbrains.kotlin.android' plugin is no longer required for
    //  Kotlin support since AGP 9.0"）。
    // Compose 编译器插件仍然需要单独 apply。
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.watchface.idtool"
    // compileSdk 必须 >= 37：haze 2.0.0-rc01 传递依赖的 compose ui 1.12.0 /
    // lifecycle 2.11.0 都要求在 37 或更高版本上编译。
    compileSdk = 37
    buildToolsVersion = "37.0.0"

    defaultConfig {
        applicationId = "com.watchface.idtool"
        minSdk = 24
        targetSdk = 37
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

// AGP 9.0 起 Kotlin 由 AGP 内建，kotlin { } 块提升到顶层。
// kotlinOptions 在 Kotlin 2.4 已移除，统一用 compilerOptions。
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}

dependencies {
    // 版本随 AGP 9.1.0 + compileSdk 37 一同抬升：haze 2.0.0-rc01 的传递依赖
    // 已经把 compose ui 拉到 1.12.x / lifecycle 拉到 2.11.0，显式声明对齐可避免
    // 版本冲突解析出意外组合。
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
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
