plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.buhzzi.vwinngjuanime"
	compileSdk = 35

	defaultConfig {
		applicationId = "com.buhzzi.vwinngjuanime"
		minSdk = 34
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
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}

	buildFeatures {
		compose = true
	}
	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.14"
	}
}

dependencies {

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.material)

	implementation(libs.androidx.activity.compose)
	implementation(libs.androidx.material)
	implementation(libs.androidx.material.icons.extended)
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.tooling)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.navigation.compose)
	implementation(libs.androidx.lifecycle.common.java8)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.lifecycle.lifecycle.service)
	implementation(libs.androidx.lifecycle.viewmodel.ktx)
	implementation(libs.androidx.savedstate)

	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
}
