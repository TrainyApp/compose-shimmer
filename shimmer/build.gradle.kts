@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.android.build.api.variant.impl.KotlinMultiplatformAndroidCompilationImpl
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import java.util.*

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.dokka)
    alias(libs.plugins.publish)
}

fun KotlinHierarchyBuilder.withAndroidLibrary() {
    withAndroidTarget()
    withCompilations {
        it is KotlinMultiplatformAndroidCompilationImpl
    }
}

kotlin {
    applyHierarchyTemplate {
        withSourceSetTree(KotlinSourceSetTree.main, KotlinSourceSetTree.test)
        common {
            group("skiko") {
                withIos()
                withJs()
                withJvm()
                withWasmJs()
            }
            withAndroidLibrary()
        }
    }


    android {
        compileSdk {
            version = release(libs.versions.targetSdk.get().toInt())
            minSdk = libs.versions.minSdk.get().toInt()
        }
        namespace = "com.valentinilk.shimmer"

//        compileOptions {
//            sourceCompatibility = JavaVersion.VERSION_1_8
//            targetCompatibility = JavaVersion.VERSION_1_8
//        }
    }
//    androidTarget {
//        publishLibraryVariants("release")
//        compilerOptions {
//            jvmTarget.set(JvmTarget.JVM_1_8)
//        }
//    }

    jvm()

    js(IR) {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.foundation)
            api(libs.compose.runtime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

mavenPublishing {
    coordinates(
        "com.trainyapp.shimmer",
        "compose-shimmer",
        "1.4.0-SNAPSHOT",
    )
    pom {
        name.set("Compose Shimmer")
        description.set("Shimmer library for Jetpack Compose and Compose Multiplatform")
        url.set("https://github.com/valentinilk/compose-shimmer/")
        inceptionYear.set("2021")

        scm {
            connection.set("scm:git:git://github.com/valentinilk/compose-shimmer.git")
            developerConnection.set("scm:git:git://github.com/valentinilk/compose-shimmer.git")
            url.set("https://github.com/valentinilk/compose-shimmer/")
        }

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("valentinilk")
                name.set("Valentin Ilk")
                url.set("https://github.com/valentinilk/")
            }
        }
    }
}

publishing {
    repositories {
        maven("https://europe-west3-maven.pkg.dev/mik-music/trainyapp") {
            credentials {
                username = "_json_key_base64"
                password = System.getenv("GOOGLE_KEY")?.toByteArray()?.let {
                    Base64.getEncoder().encodeToString(it)
                }
            }

            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}
