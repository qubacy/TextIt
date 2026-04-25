import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

fun getProperty(name: String): String? =
    localProperties.getProperty(name)
        ?: providers.gradleProperty(name).orNull

android {
    namespace = "net.qubacy.textit"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "net.qubacy.textit"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = providers.gradleProperty("generalVersionCode").get().toInt()
        versionName = providers.gradleProperty("generalVersionName").get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        val rawStoreFile = getProperty("RELEASE_STORE_FILE")

        create("release") {
            if (!rawStoreFile.isNullOrEmpty()) {
                storeFile = rootProject.file(rawStoreFile)
                storePassword = getProperty("RELEASE_STORE_PASSWORD")
                keyAlias = getProperty("RELEASE_KEY_ALIAS")
                keyPassword = getProperty("RELEASE_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {}

        release {
            isMinifyEnabled = false
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
//    kotlinOptions {
//        jvmTarget = "11"
//    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":composeApp"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation(project.dependencies.platform(libs.firebase.bom))
    implementation(libs.firebase.cloudMessaging)
    implementation(libs.firebase.analytics)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.testExt.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}