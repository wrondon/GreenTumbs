//plugins {
//    id("com.android.library")
//    id("org.jetbrains.kotlin.android")
//}

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.kotlin.serial)
}


android {
    namespace = "com.greenthumbs.demo.core.network"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        aidl = false
        buildConfig = false
        renderScript = false
        shaders = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

//    implementation("androidx.core:core-ktx:1.8.0")
//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("com.google.android.material:material:1.8.0")
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


//    implementation(libs.androidx.room.runtime)
//    implementation(libs.androidx.room.ktx)
//    ksp(libs.androidx.room.compiler)

    implementation(project(":core-ui"))
    implementation(project(":core-data"))
    implementation(project(":feature-suggestion"))
    androidTestImplementation(project(":core-testing"))

    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    // Hilt and instrumented tests.
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.android.compiler)
    // Hilt and Robolectric tests.
    testImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.android.compiler)

    // Local tests: jUnit, coroutines, Android runner
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    // Instrumented tests: jUnit rules and runners
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)

    //Other Useful external Libraries
    implementation(libs.gson)
    implementation(libs.glide)
    implementation(libs.square.retrofit)
    implementation(libs.square.retrofit.converter.gson)
    implementation(libs.square.retrofit.converter.moshi)

    implementation(libs.square.moshi)
    implementation(libs.square.moshi.kotlin)
    implementation(libs.square.moshi.adapters)

    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.webview)

    implementation(libs.coil)
    implementation(libs.coil.compose)

    implementation(libs.timber)

    implementation(libs.rx.android)
    implementation(libs.rx.java)
    implementation(libs.rx.kotlin)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.serialization.gson)
}