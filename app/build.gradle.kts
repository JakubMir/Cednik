import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
    alias(libs.plugins.com.google.dagger.hilt.android)
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").reader())

android {
    namespace = "com.example.cednik"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.cednik"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "SMTP_URL", properties.getProperty("smtpUrl"))
            buildConfigField("String", "SMTP_USERNAME", properties.getProperty("smtpUsername"))
            buildConfigField("String", "SMTP_PASSWORD", properties.getProperty("smtpPassword"))
            buildConfigField("String", "SMTP_FROM_MAIL", properties.getProperty("smtpFromMail"))
        }
        debug {
            buildConfigField("String", "SMTP_URL", properties.getProperty("smtpUrl"))
            buildConfigField("String", "SMTP_USERNAME", properties.getProperty("smtpUsername"))
            buildConfigField("String", "SMTP_PASSWORD", properties.getProperty("smtpPassword"))
            buildConfigField("String", "SMTP_FROM_MAIL", properties.getProperty("smtpFromMail"))
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
        viewBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/LICENSE.md"
        }
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation(libs.navigation.compose)

    implementation(libs.room.ktx)
    implementation(libs.room.viewmodel)
    implementation(libs.room.lifecycle)
    implementation(libs.room.runtime)
    kapt(libs.room.compiler.kapt)


    implementation(libs.hilt)
    implementation(libs.hilt.compose)
    kapt(libs.hilt.compiler.kapt)


    implementation(libs.moshi)
    kapt(libs.moshi.kapt)

    implementation(libs.lifecycle)

    implementation(libs.googlemap)
    implementation(libs.googlemap.compose)
    implementation(libs.googlemap.foundation)

    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation ("com.google.accompanist:accompanist-permissions:0.30.1")

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation ("com.google.android.gms:play-services-location:21.3.0")
    implementation ("com.google.android.libraries.places:places:3.4.0")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    implementation("androidx.compose.material:material-icons-extended:1.6.8")

    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.datastore:datastore-preferences-core:1.1.1")

    // non google library for location fetch and permission handle
    implementation (libs.locus)

    // non google library for emails
    implementation("com.github.nedimf:maildroid:v0.1.1-release")
}