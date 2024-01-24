plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.tv.libplayer"
    compileSdk = 33

}

dependencies {
    implementation(project(mapOf("path" to ":TVLibCommon")))

}