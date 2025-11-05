plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization.plugin)
}

fun String.runCommand(): String = providers.exec {
    commandLine(split("\\s(?=(?:[^'\"`]*(['\"`])[^'\"`]*\\1)*[^'\"`]*$)".toRegex()))
    isIgnoreExitValue = true
}.standardOutput.asText.get().trim()

val mockitoAgent: Configuration by configurations.creating {
    isTransitive = false
    isCanBeConsumed = true
}

android {
    namespace = "io.github.jo_bitsch.aoa_control"
    compileSdk = 36

    buildFeatures {
        compose = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "io.github.jo_bitsch.aoa_control"
        minSdk = 24
        targetSdk = 36

        versionCode = "git rev-list --count main".runCommand().ifEmpty { "1" }.toInt()
        versionName = "git describe --tags --long --dirty --match=v[0-9]*".runCommand().ifEmpty { "unknown" }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        jvmToolchain(21)
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }

        jniLibs {
            // as the following dependency is distributed already stripped,
            // mark it to suppress the warning:
            // "Unable to strip the following libraries, packaging them as they are: libandroidx.graphics.path.so"
            keepDebugSymbols += "**/libandroidx.graphics.path.so"
        }
    }

    dependenciesInfo {
        includeInApk = true
        includeInBundle = true
    }

    buildToolsVersion = "36.1.0"


    testOptions {
        unitTests {
//            isIncludeAndroidResources = true

            //isReturnDefaultValues = true
            all { test ->
                test.jvmArgs("-javaagent:${mockitoAgent.asPath}")
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    ///implementation(libs.androidx.ui.graphics)
    implementation(libs.jsch)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.foundation)

    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.kotlin.reflect)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)

    testImplementation(libs.conscrypt.openjdk.uber)
    implementation(libs.cbor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.cbor)

    mockitoAgent(libs.mockito) {isTransitive = false}
}
