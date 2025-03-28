package com.buhzzi.vwinngjuanime

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

internal class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		setContent {
			MainComposable()
		}
	}

	companion object {
		var instance: MainActivity? = null
			private set

		val instanceMust
			get() = instance ?: error("${MainActivity::class.qualifiedName} instance is not created.")
	}
}

@Composable
internal fun MainComposable() {
	ScaffoldWrapComposable {
		val context = LocalContext.current
		val navController = rememberNavController()
		NavHost(navController, stringResource(R.string.nav_settings)) {
			composable(context.getString(R.string.nav_settings)) { SettingsComposable(navController) }
		}
	}
}

internal val LocalScaffoldState = staticCompositionLocalOf<ScaffoldState> { error("No ScaffoldState provided") }

@Composable
internal fun ScaffoldWrapComposable(content: @Composable (PaddingValues) -> Unit) {
	ThemeWrapComposable {
		val scaffoldState = rememberScaffoldState()
		CompositionLocalProvider(LocalScaffoldState provides scaffoldState) {
			Scaffold(
				scaffoldState = scaffoldState,
				content = content,
			)
		}
	}
}

internal val LocalCustomColors = compositionLocalOf { mutableMapOf<String, Color>() }

@Composable
internal fun ThemeWrapComposable(content: @Composable () -> Unit) {
	MaterialTheme(
		if (isSystemInDarkTheme()) darkColors()
		else lightColors(),
//		Typography(
//			FontFamily(
//				Font(R.font.gen_ryu_min2_tc_b),
//			),
//		),
	) {
		CompositionLocalProvider(
			LocalCustomColors provides if (isSystemInDarkTheme()) mutableMapOf(
				"red" to Color.hsl(0F, 1F, 0.375F),
				"green" to Color.hsl(120F, 1F, 0.375F),
			) else mutableMapOf(
				"red" to Color.hsl(0F, 1F, 0.625F),
				"green" to Color.hsl(120F, 1F, 0.625F),
			),
			content,
		)
	}
}

@Composable
internal fun SettingsComposable(navController: NavController) {
	val context = LocalContext.current
	Column(Modifier
		.safeDrawingPadding()
		.fillMaxSize(),
		Arrangement.Center,
		Alignment.CenterHorizontally,
	) {
		OutlinedButton({
			context.getSystemService(InputMethodManager::class.java).showInputMethodPicker()
		}, Modifier.fillMaxWidth()) {
			Text("Settings")
		}
		var text by remember { mutableStateOf("""
			|<?xml version="1.0" encoding="utf-8"?>
			|<manifest xmlns:android="http://schemas.android.com/apk/res/android">
			|
			|	<uses-permission android:name="android.permission.VIBRATE" />
			|	<application
			|		android:allowBackup="true"
			|		android:dataExtractionRules="@xml/data_extraction_rules"
			|		android:fullBackupContent="@xml/backup_rules"
			|		android:icon="@mipmap/ic_launcher"
			|		android:label="@string/app_name"
			|		android:roundIcon="@mipmap/ic_launcher_round"
			|		android:supportsRtl="true"
			|		android:theme="@style/Theme.VwinngjuanIme"
			|	>
			|		<activity android:name="com.buhzzi.vwinngjuanime.MainActivity"
			|			android:exported="true"
			|		>
			|			<intent-filter>
			|				<action android:name="android.intent.action.MAIN" />
			|				<category android:name="android.intent.category.LAUNCHER" />
			|			</intent-filter>
			|		</activity>
			|		<service android:name="com.buhzzi.vwinngjuanime.VwinngjuanIms"
			|			android:label="@string/app_name"
			|			android:permission="android.permission.BIND_INPUT_METHOD"
			|			android:exported="true"
			|		>
			|			<meta-data android:name="android.view.im"
			|				android:resource="@xml/vwinngjuan_ime"
			|			/>
			|			<intent-filter>
			|				<action android:name="android.view.InputMethod" />
			|			</intent-filter>
			|		</service>
			|	</application>
			|
			|</manifest>
			|
		""".trimMargin()) }
		val focusRequester = FocusRequester()
		val keyboardController = LocalSoftwareKeyboardController.current
		LaunchedEffect(Unit) {
			focusRequester.requestFocus()
			keyboardController?.show()
		}
		OutlinedTextField(text, { text = it }, Modifier
			.fillMaxWidth()
			.focusRequester(focusRequester),
			label = { Text("Test your input here...") },
		)
	}
}

val Context.externalFilesDir
	get() = getExternalFilesDir(null) ?: error("Cannot access external files directory.")
