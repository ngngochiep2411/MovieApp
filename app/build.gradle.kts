plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.dagger.hilt.android")
    id ("kotlin-parcelize")
    alias(libs.plugins.google.gms.google.services)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.movieapp"
    compileSdk =35

    defaultConfig {
        applicationId = "com.example.movieapp"
        minSdk = 24
        targetSdk = 35
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
        viewBinding = true
    }
    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.media3.exoplayer.hls)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.dagger:hilt-android:2.56.2")
    ksp("com.google.dagger:hilt-android-compiler:2.56.2")
    implementation(libs.retrofit)
    implementation (libs.gson)
    implementation (libs.retrofit2.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    //Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // Views/Fragments integration
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    implementation (libs.androidx.swiperefreshlayout)
    implementation (libs.glide)

    implementation ("me.relex:circleindicator:2.1.6")
    implementation("androidx.media3:media3-exoplayer:1.5.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.5.0")
    implementation("androidx.media3:media3-ui:1.5.0")
    implementation("androidx.media3:media3-exoplayer-hls:1.5.0")
    implementation ("com.airbnb.android:lottie:6.6.2")

    implementation (platform("com.google.firebase:firebase-bom:31.5.0"))
//    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation ("com.google.android.gms:play-services-auth:20.5.0")


    implementation ("com.github.zhpanvip:viewpagerindicator:1.2.3")

    implementation ("com.google.android.flexbox:flexbox:3.0.0")
    implementation("com.github.marlonlom:timeago:4.1.0")
    implementation ("androidx.datastore:datastore-preferences:1.1.2")
    implementation ("androidx.datastore:datastore:1.1.2")
    implementation ("io.github.glailton.expandabletextview:expandabletextview:1.0.4")
    implementation ("com.android.support:multidex:1.0.3")
    //完整版引入
    implementation ("com.github.CarGuo.GSYVideoPlayer:gsyvideoplayer:v10.2.0")
    //是否需要AliPlayer模式
    implementation ("com.github.CarGuo.GSYVideoPlayer:gsyvideoplayer-aliplay:v10.2.0")
    // Room
    implementation ("androidx.room:room-runtime:2.7.1")
    implementation ("androidx.room:room-ktx:2.7.1")
    ksp ("androidx.room:room-compiler:2.7.1")
    implementation ("com.mikhaellopez:circularprogressbar:3.1.0")
    implementation ("com.github.animsh:AnimatedCheckBox:1.0.0")
    implementation ("org.ocpsoft.prettytime:prettytime:5.0.8.Final")

    implementation("io.github.ParkSangGwon:tedimagepicker:1.6.1")
    implementation("com.arthenica:ffmpeg-kit-full:6.0-2")
    implementation ("io.github.amrdeveloper:reactbutton:2.1.0")
}