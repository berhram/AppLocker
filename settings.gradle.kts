pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            //Core
            library("core-ktx", "androidx.core", "core-ktx").version("1.7.0")
            library("appcompat", "androidx.appcompat", "appcompat").version("1.4.1")
            library("material", "com.google.android.material", "material").version("1.6.0")
            //Coroutines
            version("coroutines", "1.6.1")
            library("kotlinx-coroutines-core", "org.jetbrains.kotlinx" , "kotlinx-coroutines-core").versionRef("coroutines")
            library("kotlinx-coroutines-android", "org.jetbrains.kotlinx" , "kotlinx-coroutines-android").versionRef("coroutines")
            bundle("coroutines", listOf("kotlinx-coroutines-core", "kotlinx-coroutines-android"))
            //Orbit
            version("orbit", "4.3.2")
            library("orbit-core", "org.orbit-mvi", "orbit-core").versionRef("orbit")
            library("orbit-viewmodel", "org.orbit-mvi", "orbit-viewmodel").versionRef("orbit")
            bundle("orbit", listOf("orbit-core", "orbit-viewmodel"))
            //Room
            version("room", "2.4.2")
            library("room-ktx", "androidx.room", "room-ktx").versionRef("room")
            library("room-runtime", "androidx.room", "room-runtime").versionRef("room")
            library("room-compiler", "androidx.room", "room-compiler").versionRef("room")
            library("room-testing", "androidx.room", "room-testing").versionRef("room")
            //Activity
            version("activity", "1.4.0")
            library("activity-ktx", "androidx.activity", "activity-ktx").versionRef("activity")
            library("activity-compose", "androidx.activity", "activity-compose").versionRef("activity")
            bundle("activity", listOf("activity-ktx", "activity-compose"))
            //Compose
            version("compose", "1.1.1")
            library("ui", "androidx.compose.ui", "ui").versionRef("compose")
            library("ui-util", "androidx.compose.ui", "ui-util").versionRef("compose")
            library("ui-tooling", "androidx.compose.ui", "ui-tooling").versionRef("compose")
            library("foundation", "androidx.compose.foundation", "foundation").versionRef("compose")
            library("material", "androidx.compose.material", "material").versionRef("compose")
            library("material-icons-core", "androidx.compose.material", "material-icons-core").versionRef("compose")
            library("material-icons-extended", "androidx.compose.material", "material-icons-extended").versionRef("compose")
            bundle("compose", listOf("ui", "ui-util", "ui-tooling", "foundation", "material", "material-icons-core", "material-icons-extended"))
            //Koin
            version("koin", "3.2.0")
            library("koin-android", "io.insert-koin", "koin-android").versionRef("koin")
            library("koin-androidx-compose", "io.insert-koin", "koin-androidx-compose").versionRef("koin")
            bundle("koin", listOf("koin-android", "koin-androidx-compose"))
            //Accompanist
            version("accompanist", "0.23.1")
            library("accompanist-systemuicontroller", "com.google.accompanist", "accompanist-systemuicontroller").versionRef("accompanist")
            library("accompanist-pager", "com.google.accompanist", "accompanist-pager").versionRef("accompanist")
            library("accompanist-swiperefresh", "com.google.accompanist", "accompanist-swiperefresh").versionRef("accompanist")
            library("accompanist-pager-indicators", "com.google.accompanist", "accompanist-pager-indicators").versionRef("accompanist")
            bundle("accompanist", listOf("accompanist-systemuicontroller", "accompanist-pager", "accompanist-swiperefresh", "accompanist-pager-indicators"))
            //Testing
            library("orbit-test", "org.orbit-mvi", "orbit-test").version("4.3.2")
            library("junit", "junit", "junit").version("4.13.2")
            library("junit-android", "androidx.test.ext", "junit").version("1.1.3")
            library("ui-test-junit4", "androidx.compose.ui", "ui-test-junit4").version("1.1.1")
        }
    }
}

rootProject.name = "AppLocker"
include(":app")
