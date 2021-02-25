package dev.ricknout.rugbyranker.buildsrc

object Versions {
    const val compileSdk = 30
    const val minSdk = 24
    const val targetSdk = 30
    const val androidGradlePlugin = "7.0.0-alpha07"
    const val ktlint = "0.40.0"
    const val coil = "1.1.1"
    const val insetter = "0.5.0"
    const val accompanist = "0.5.1"
    object Kotlin {
        const val kotlin = "1.4.30"
        const val coroutines = "1.4.2"
    }
    object AndroidX {
        const val core = "1.5.0-beta02"
        const val activity = "1.3.0-alpha03"
        const val fragment = "1.3.0"
        const val appCompat = "1.3.0-beta01"
        const val constraintLayout = "2.1.0-alpha2"
        const val recyclerView = "1.2.0-beta02"
        const val drawerLayout = "1.1.1"
        const val swipeRefreshLayout = "1.2.0-alpha01"
        const val viewPager2 = "1.1.0-alpha01"
        const val emoji = "1.2.0-alpha03"
        const val browser = "1.3.0"
        const val room = "2.3.0-beta02"
        const val navigation = "2.3.3"
        const val work = "2.7.0-alpha01"
        const val hilt = "1.0.0-alpha03"
        const val dataStore = "1.0.0-alpha07"
        const val compose = "1.0.0-alpha12"
        object Lifecycle {
            const val lifecycle = "2.3.0"
            const val compose = "1.0.0-alpha01"
        }
        object Paging {
            const val paging = "3.0.0-beta01"
            const val compose = "1.0.0-alpha07"
        }
        object Test {
            const val core = "1.4.0-alpha04"
            const val jUnit = "1.1.3-alpha04"
            const val runner = "1.4.0-alpha04"
        }
    }
    object Google {
        const val material = "1.3.0"
        const val hilt = "2.32-alpha"
        object OssLicenses {
            const val ossLicenses = "17.0.0"
            const val gradlePlugin = "0.10.2"
        }
    }
    object Square {
        const val okHttp = "4.9.1"
        const val retrofit = "2.9.0"
    }
}

object Libs {
    const val androidGradlePlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"
    const val ktlint = "com.pinterest:ktlint:${Versions.ktlint}"
    const val coil = "io.coil-kt:coil:${Versions.coil}"
    const val insetter = "dev.chrisbanes.insetter:insetter:${Versions.insetter}"
    object Kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.Kotlin.kotlin}"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin.kotlin}"
        object Coroutines {
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.coroutines}"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Kotlin.coroutines}"
        }
    }
    object AndroidX {
        const val appCompat = "androidx.appcompat:appcompat:${Versions.AndroidX.appCompat}"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.AndroidX.constraintLayout}"
        const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.AndroidX.recyclerView}"
        const val drawerLayout = "androidx.drawerlayout:drawerlayout:${Versions.AndroidX.drawerLayout}"
        const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.AndroidX.swipeRefreshLayout}"
        const val viewPager2 = "androidx.viewpager2:viewpager2:${Versions.AndroidX.viewPager2}"
        const val emoji = "androidx.emoji:emoji:${Versions.AndroidX.emoji}"
        const val browser = "androidx.browser:browser:${Versions.AndroidX.browser}"
        object Core {
            const val ktx = "androidx.core:core-ktx:${Versions.AndroidX.core}"
        }
        object Activity {
            const val ktx = "androidx.activity:activity-ktx:${Versions.AndroidX.activity}"
            const val compose = "androidx.activity:activity-compose:${Versions.AndroidX.activity}"
        }
        object Fragment {
            const val ktx = "androidx.fragment:fragment-ktx:${Versions.AndroidX.fragment}"
        }
        object Lifecycle {
            const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.AndroidX.Lifecycle.lifecycle}"
            const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.AndroidX.Lifecycle.lifecycle}"
            const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.AndroidX.Lifecycle.compose}"
        }
        object Room {
            const val runtime = "androidx.room:room-runtime:${Versions.AndroidX.room}"
            const val compiler = "androidx.room:room-compiler:${Versions.AndroidX.room}"
            const val ktx = "androidx.room:room-ktx:${Versions.AndroidX.room}"
        }
        object Paging {
            const val runtime = "androidx.paging:paging-runtime:${Versions.AndroidX.Paging.paging}"
            const val compose = "androidx.paging:paging-compose:${Versions.AndroidX.Paging.compose}"
        }
        object Navigation {
            const val uiKtx = "androidx.navigation:navigation-ui-ktx:${Versions.AndroidX.navigation}"
            const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:${Versions.AndroidX.navigation}"
            const val safeArgsGradlePlugin = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.AndroidX.navigation}"
        }
        object Work {
            const val runtimeKtx = "androidx.work:work-runtime-ktx:${Versions.AndroidX.work}"
        }
        object Hilt {
            const val compiler = "androidx.hilt:hilt-compiler:${Versions.AndroidX.hilt}"
            const val work = "androidx.hilt:hilt-work:${Versions.AndroidX.hilt}"
            object Lifecycle {
                const val viewModel = "androidx.hilt:hilt-lifecycle-viewmodel:${Versions.AndroidX.hilt}"
            }
        }
        object DataStore {
            const val preferences = "androidx.datastore:datastore-preferences:${Versions.AndroidX.dataStore}"
        }
        object Compose {
            const val animation = "androidx.compose.animation:animation:${Versions.AndroidX.compose}"
            object Runtime {
                const val runtime = "androidx.compose.runtime:runtime:${Versions.AndroidX.compose}"
                const val liveData = "androidx.compose.runtime:runtime-livedata:${Versions.AndroidX.compose}"
            }
            object Foundation {
                const val foundation = "androidx.compose.foundation:foundation:${Versions.AndroidX.compose}"
                const val layout = "androidx.compose.foundation:foundation-layout:${Versions.AndroidX.compose}"
            }
            object UI {
                const val ui = "androidx.compose.ui:ui:${Versions.AndroidX.compose}"
                const val tooling = "androidx.compose.ui:ui-tooling:${Versions.AndroidX.compose}"
            }
            object Material {
                const val material = "androidx.compose.material:material:${Versions.AndroidX.compose}"
                const val icons = "androidx.compose.material:material-icons-extended:${Versions.AndroidX.compose}"
            }
        }
        object Test {
            const val runner = "androidx.test:runner:${Versions.AndroidX.Test.runner}"
            const val instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            object Core {
                const val ktx = "androidx.test:core-ktx:${Versions.AndroidX.Test.core}"
            }
            object JUnit {
                const val ktx = "androidx.test.ext:junit-ktx:${Versions.AndroidX.Test.jUnit}"
            }
        }
    }
    object Google {
        object Material {
            const val material = "com.google.android.material:material:${Versions.Google.material}"
            const val composeThemeAdapter = "com.google.android.material:compose-theme-adapter:${Versions.AndroidX.compose}"
        }
        object Hilt {
            const val compiler = "com.google.dagger:hilt-compiler:${Versions.Google.hilt}"
            const val android = "com.google.dagger:hilt-android:${Versions.Google.hilt}"
            const val androidGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:${Versions.Google.hilt}"
        }
        object OssLicenses {
            const val ossLicenses = "com.google.android.gms:play-services-oss-licenses:${Versions.Google.OssLicenses.ossLicenses}"
            const val gradlePlugin = "com.google.android.gms:oss-licenses-plugin:${Versions.Google.OssLicenses.gradlePlugin}"
        }
    }
    object Square {
        const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.Square.retrofit}"
        object Retrofit {
            const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.Square.retrofit}"
            const val gsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.Square.retrofit}"
        }
    }
    object Accompanist {
        const val coil = "dev.chrisbanes.accompanist:accompanist-coil:${Versions.accompanist}"
        const val insets = "dev.chrisbanes.accompanist:accompanist-insets:${Versions.accompanist}"
    }
}
