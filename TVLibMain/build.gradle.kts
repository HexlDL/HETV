plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.tv.libmain"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
    }
    viewBinding { enable = true }
}

dependencies {
    implementation(project(mapOf("path" to ":TVLibCommon")))

}