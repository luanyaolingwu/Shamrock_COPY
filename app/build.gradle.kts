import com.android.build.api.dsl.ApplicationExtension
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.temporal.ChronoUnit

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "1.8.10"
}

android {
    namespace = "moe.fuqiuluo.shamrock"
    ndkVersion = "25.1.8937393"
    compileSdk = 34

    defaultConfig {
        applicationId = "moe.fuqiuluo.shamrock"
        minSdk = 27
        targetSdk = 34
        versionCode = getCurrentVersionCode()
        versionName = "1.0.5" + "-r${getGitCommitCount()}" + "-Miao${getVersionName()}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        @Suppress("UnstableApiUsage")
        externalNativeBuild {
            cmake {
                cppFlags += ""
            }
        }
    }

    buildTypes {
        release {
            //先前的设置为: isMinifyEnabled = false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    android.applicationVariants.all {
        outputs.map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach {
                val abiName = when (val abi = it.outputFileName.split("-")[1].split(".apk")[0]) {
                    "app" -> "all"
                    "x64" -> "x86_64"
                    else -> abi
                }
                it.outputFileName = "Shamrock-v${versionName}-${abiName}.apk"
            }
    }

    flavorDimensions.add("mode")

    productFlavors {
        create("app") {
            dimension = "mode"
            ndk {
                abiFilters.add("arm64-v8a")
                abiFilters.add("x86_64")
            }
        }
        create("arm64") {
            dimension = "mode"
            ndk {
                abiFilters.add("arm64-v8a")
            }
        }
        create("x64") {
            dimension = "mode"
            ndk {
                abiFilters.add("x86_64")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/*"
            excludes += "/META-INF/NOTICE.txt"
            //excludes += "/META-INF/DEPENDENCIES.txt"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/notice.txt"
            excludes += "/META-INF/dependencies.txt"
            excludes += "/META-INF/LGPL2.1"
            excludes += "/META-INF/ASL2.0"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/io.netty.versions.properties"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/license.txt"
            excludes += "/META-INF/*.kotlin_module"
            excludes += "/META-INF/services/reactor.blockhound.integration.BlockHoundIntegration"
        }
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    configureAppSigningConfigsForRelease(project)
    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

fun configureAppSigningConfigsForRelease(project: Project) {
    val keystorePath: String? = System.getenv("KEYSTORE_PATH")
    if (keystorePath.isNullOrBlank()) {
        return
    }
    project.configure<ApplicationExtension> {
        signingConfigs {
            create("release") {
                storeFile = file(System.getenv("KEYSTORE_PATH"))
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
                enableV2Signing = true
            }
        }
        buildTypes {
            release {
                signingConfig = signingConfigs.findByName("release")
            }
            debug {
                signingConfig = signingConfigs.findByName("release")
            }
        }
    }
}

fun getGitCommitHash(): String {
    val builder = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
    val process = builder.start()
    val reader = process.inputReader()
    val hash = reader.readText().trim()
    return if (hash.isNotEmpty()) ".$hash" else ""
}

fun getGitCommitCount(): String {
    val process = Runtime.getRuntime().exec("git rev-list --count HEAD")
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    val commitCount = reader.readLine()?.toIntOrNull() ?: 0
    reader.close()
    process.waitFor()
    return (commitCount + 1).toString()
}

fun getCurrentMonthTimestamp(): Long {
    val currentDate = LocalDate.now()
    val startOfMonth = currentDate.withDayOfMonth(1)
    return ChronoUnit.MONTHS.between(LocalDate.of(1970, 1, 1), startOfMonth)
}

fun getCurrentVersionCode(): Int{
    //return (getCurrentMonthTimestamp()).toInt()
    return (getGitCommitCount()).toInt()
    //return ((getCurrentMonthTimestamp()).toInt() + (getGitCommitCount()).toInt())
}

fun getVersionName(): String {
    return getGitCommitHash()
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    //noinspection GradleDynamicVersion
    implementation("com.google.accompanist:accompanist-pager:0.31.5+")
    //noinspection GradleDynamicVersion
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.31.5+")
    //noinspection GradleDynamicVersion useless
    // implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0+")
    implementation("io.coil-kt:coil:2.4.0")
    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.16")

    val ktorVersion = "2.3.3"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    //implementation("io.ktor:ktor-serialization-kotlinx-protobuf:$ktorVersion")

    implementation(project(":xposed"))

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.06.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}