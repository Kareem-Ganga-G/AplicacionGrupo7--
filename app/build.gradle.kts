plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.aplicaciongrupo7"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.aplicaciongrupo7"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation:1.5.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    // Gson debe estar en dependencies, no en android
    implementation("com.google.code.gson:gson:2.10.1")

    // Si necesitas navigation para Compose
    implementation("androidx.navigation:navigation-compose:2.7.3")
    //Kotest
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")

    //Junit 5
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")

    //MockN
    testImplementation("io.mockk:mockk:1.13.10")

    //Compose UI test
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.2")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.2")



}