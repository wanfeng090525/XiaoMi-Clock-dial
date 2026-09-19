buildscript {
    repositories {
        maven { url = uri("https://dl.google.com/dl/android/maven2") }
        mavenCentral()
    }
    dependencies {
        // AGP 9.1.0 是硬性要求：haze 2.0.0-rc01 由 JetBrains Compose Multiplatform 构建，
        // 其 POM 传递依赖 org.jetbrains.compose.ui:ui:1.12.0 与
        // org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.11.0，
        // 这两个库要求 AGP >= 9.1.0 且 compileSdk >= 37。
        // 降低自己声明的 AndroidX 版本无法绕开（传递依赖会被拉到高版本）。
        classpath("com.android.tools.build:gradle:9.1.0")
        // Kotlin 必须与 haze 2.0.0-rc01 对齐：其 POM 要求 kotlin-stdlib 2.4.20，
        // 低版本编译器读不了更高版本的 Kotlin 元数据（incompatible metadata version）。
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.20")
        classpath("org.jetbrains.kotlin:compose-compiler-gradle-plugin:2.4.20")
    }
}
