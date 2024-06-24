plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.wonchihyeon.seoultoilet"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.wonchihyeon.seoultoilet"
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        dataBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // 구글 맵 관련 라이브러리
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // 구글 맵 클러스터 사용을 위한 라이브러리
    implementation("com.google.maps.android:android-maps-utils:0.5+")

    // 맵 서치 바 카드를 만들기 위한 CardView 라이브러리
    implementation("com.android.support:cardview-v7:28.0.0")

    // 현재위치 버튼에서 사용하는 FloatingActionButton 사용을 위한 라이브러리
    implementation("com.android.support:design:28.0.0")

}