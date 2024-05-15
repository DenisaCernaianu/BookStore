import java.util.regex.Pattern.compile

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.chaquo.python")
}

android {
    namespace = "com.example.bookstore"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bookstore"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            // On Apple silicon, you can omit x86_64.
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    chaquopy {
        defaultConfig {
            buildPython ("C:/Users/denis/AppData/Local/Programs/Python/Python38/python.exe")
            version = "3.8"
            pip {
                // A requirement specifier, with or without a version number:
                install("scipy")
                install("requests==2.24.0")
                install("scikit-learn")
                install("pandas")

            }


        }
        sourceSets {
            getByName("main") {
                srcDir("src/main/python")
            }
        }

    }

    buildFeatures{
        viewBinding = true
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth-ktx:22.1.2")
    implementation("com.google.firebase:firebase-database:20.2.2")
    implementation("com.google.firebase:firebase-storage:20.2.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("com.google.firebase:firebase-bom:32.5.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation ("com.squareup.picasso:picasso:2.8")

    implementation ("com.android.volley:volley:1.2.1")

    implementation ("com.google.mlkit:barcode-scanning:17.2.0")

}