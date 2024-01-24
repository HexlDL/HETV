plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.tv.libcommon"
    compileSdk = 33

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src/main/assets")
            }
        }
    }

    viewBinding { enable = true }

}


val version = "1.0.0"
val roomVersion = "2.5.1"
val mediaVersion = "1.0.1"
dependencies {

    api("androidx.core:core-ktx:1.9.0")

    api("androidx.leanback:leanback:$version")
//    api("androidx.leanback:leanback-paging:$version")
//    api("androidx.leanback:leanback-tab:$version")

    api("com.github.bumptech.glide:glide:4.15.0")

    api("androidx.appcompat:appcompat:1.6.1")
    api("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    api("androidx.constraintlayout:constraintlayout:2.1.4")

    // TV provider support library
    api("androidx.tvprovider:tvprovider:1.0.0")

    // JSON parsing
    api("com.google.code.gson:gson:2.9.0")

    // Room library
    api("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // exoplayer
    api("androidx.media3:media3-exoplayer:$mediaVersion")
    api("androidx.media3:media3-ui:$mediaVersion")
    api("androidx.media3:media3-exoplayer-dash:$mediaVersion")
    api("androidx.media3:media3-exoplayer-hls:$mediaVersion")
}