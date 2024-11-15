plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.tinderempleosapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.tinderempleosapp"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Dependencias básicas de la UI
    implementation(libs.appcompat)
    implementation(libs.material) // Material Components
    implementation(libs.activity)
    implementation(libs.constraintlayout)


    // Dependencia específica de Material Design para fuentes y componentes
    implementation("com.google.android.material:material:1.6.0")
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database) // Versión 1.4.0 o superior

    // Dependencias de pruebas
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}