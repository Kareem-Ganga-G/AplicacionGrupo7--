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

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    signingConfigs {
        create("release") {
            storeFile = file("C:/Users/PC/Desktop/KeyStore/KeyStore.jks")
            storePassword = "Grupo7"
            keyAlias = "key0"
            keyPassword = "Grupo7"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
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

    // CONFIGURACIÓN DE TESTS
    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.isReturnDefaultValues = true
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))  // Actualizado
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation:1.5.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    dependencies {
        // Material Icons
        implementation("androidx.compose.material:material-icons-extended:1.5.4")

        // O si no funciona, prueba con:
        implementation("androidx.compose.material:material-icons-core:1.5.4")

        // También necesitas:
        implementation("androidx.compose.material3:material3:1.1.2")
    }

    implementation("androidx.activity:activity-compose:1.8.0")

    implementation("io.coil-kt:coil-compose:2.5.0")


    implementation("com.google.accompanist:accompanist-permissions:0.32.0")


    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    implementation("androidx.navigation:navigation-compose:2.7.4")

    implementation("androidx.activity:activity-ktx:1.8.0")

    implementation("androidx.exifinterface:exifinterface:1.3.6")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation(libs.androidx.camera.core)


    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testImplementation("org.junit.vintage:junit-vintage-engine:5.9.2")

    testImplementation("org.mockito:mockito-core:5.4.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.mockito:mockito-inline:4.11.0")

    testImplementation("androidx.test:core:1.5.0")
    testImplementation("androidx.test:core-ktx:1.5.0")
    testImplementation("androidx.test:runner:1.5.2")
    testImplementation("androidx.test:rules:1.5.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))  // Actualizado
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")
}

tasks.withType<Test> {
    useJUnitPlatform()
}