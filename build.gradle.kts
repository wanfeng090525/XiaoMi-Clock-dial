buildscript {
    repositories {
        maven { url = uri("https://dl.google.com/dl/android/maven2") }
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.13.0")
        // Kotlin 必须与 haze 2.0.0-rc01 对齐：其 POM 要求 kotlin-stdlib 2.4.20，
        // 低版本编译器读不了更高版本的 Kotlin 元数据（incompatible metadata version）。
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.4.20")
        classpath("org.jetbrains.kotlin:compose-compiler-gradle-plugin:2.4.20")
    }
}
