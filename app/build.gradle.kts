import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

val localSigningPropertiesFile = rootProject.file("keystore.properties")
val localSigningProperties = Properties().apply {
    if (localSigningPropertiesFile.exists()) {
        localSigningPropertiesFile.inputStream().use { load(it) }
    }
}
val localSigningStorePath = localSigningProperties.getProperty("storeFile")?.trim().orEmpty()
val localSigningStoreFile = if (localSigningStorePath.isNotEmpty()) rootProject.file(localSigningStorePath) else null
val hasLocalReleaseSigning = listOf("storeFile", "storePassword", "keyAlias", "keyPassword")
    .all { !localSigningProperties.getProperty(it).isNullOrBlank() } &&
    (localSigningStoreFile?.exists() == true)

android {
    namespace = "com.mahmodhota.worldfood3dadventure"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.mahmodhota.worldfood3dadventure"
        minSdk = 26
        targetSdk = 37
        versionCode = 10001
        versionName = "1.0.0-rc1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (hasLocalReleaseSigning) {
            create("release") {
                storeFile = localSigningStoreFile
                storePassword = localSigningProperties.getProperty("storePassword")
                keyAlias = localSigningProperties.getProperty("keyAlias")
                keyPassword = localSigningProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (hasLocalReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "DebugProbesKt.bin"
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(platform("com.google.firebase:firebase-bom:34.16.0"))
    implementation("com.google.firebase:firebase-analytics")

    releaseImplementation(
        "com.google.firebase:firebase-appcheck-playintegrity"
    )

    debugImplementation(
        "com.google.firebase:firebase-appcheck-debug"
    )
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.firebase.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}