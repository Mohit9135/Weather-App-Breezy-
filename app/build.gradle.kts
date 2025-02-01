import java.util.Properties
import java.io.FileInputStream
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.weatherapp"
    compileSdk = 35
    buildFeatures{
        viewBinding=true

        buildConfig=true
    }

    defaultConfig {
        applicationId = "com.example.weatherapp"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // Load API key from local.properties

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "WEATHER_API_KEY", "\"${getWeatherApiKey()}\"")
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
}

dependencies {
//    Lottie animations
    implementation ("com.airbnb.android:lottie:6.6.2")

//    Gson Converter
    implementation ("com.squareup.retrofit2:converter-gson:2.11.0")

    //    Koral dependency to show gif images
    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.29")
//    retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("com.android.car.ui:car-ui-lib:2.6.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
fun getWeatherApiKey(): String {
    val localProperties = Properties()
    val localPropertiesFile = File(rootProject.projectDir, "local.properties")

    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }

    return localProperties.getProperty("WEATHER_API_KEY", "")
}