import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}


android {
    namespace = "com.pollub.awpfog"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pollub.awpfog"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val mapsKeyFile = rootProject.file("secret.properties")
        val properties = Properties()
        if (mapsKeyFile.exists()) {
            properties.load(mapsKeyFile.inputStream())
        }

        val apiKey = properties.getProperty("MAPS_API_KEY") ?: ""
        manifestPlaceholders["GOOGLE_KEY"] = apiKey
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Google Maps Compose
    implementation("com.google.maps.android:maps-compose:6.1.2")

    // Opcjonalnie: biblioteka dla Clustering, Street View, itp.
    implementation("com.google.maps.android:maps-compose-utils:6.1.2")

    // Opcjonalnie: biblioteka dla ScaleBar i innych widgetów
    implementation("com.google.maps.android:maps-compose-widgets:6.1.2")

    implementation("com.google.android.gms:play-services-maps:19.0.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}