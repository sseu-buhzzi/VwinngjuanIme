package com.buhzzi.vwinngjuanime

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TextField
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.io.path.appendText
import kotlin.io.path.createParentDirectories
import kotlin.io.path.div
import kotlin.io.path.outputStream
import kotlin.io.path.readText

internal class MainActivity : ComponentActivity() {
	private val importFilesCoroutineScope = CoroutineScope(Dispatchers.IO)
	private var importFilesCallback: (() -> Unit)? = null
	private val importFilesLauncher = registerForActivityResult(
		ActivityResultContracts.OpenDocumentTree(),
	) { uri ->
		if (uri != null) {
			importFilesCoroutineScope.launch(Dispatchers.IO) {
				DocumentFile.fromTreeUri(this@MainActivity, uri)?.let { userDir ->
					importFilesCopyRecursively(userDir, externalFilesDir.toPath())
				}
			}
			importFilesCallback?.invoke()
			importFilesCallback = null
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		if (intent.getBooleanExtra("import_files", false)) {
			importFiles {
				LocalBroadcastManager.getInstance(this)
					.sendBroadcast(Intent("com.buhzzi.vwinngjuanime.IMPORT_FILES_END"))
				finish()
			}
			return
		}

		instance = this

		setContent {
			MainComposable()
		}
	}

	override fun onDestroy() {
		super.onDestroy()

		if (this === instance) {
			instance = null
		}
	}

	private fun importFilesCopyRecursively(srcFile: DocumentFile, dstPath: Path) {
		when {
			srcFile.isDirectory -> {
				dstPath.createParentDirectories()
				srcFile.listFiles().forEach { srcSubFile ->
					srcSubFile.name?.let { importFilesCopyRecursively(srcSubFile, dstPath / it) }
				}
			}
			srcFile.isFile -> {
				contentResolver.openInputStream(srcFile.uri)?.use { `in` ->
					dstPath.outputStream().use { out ->
						`in`.copyTo(out)
					}
				}
			}
		}
	}

	fun importFiles(callback: () -> Unit = { }) {
		importFilesCallback = callback
		importFilesLauncher.launch(null)
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
	val activity = MainActivity.instanceMust
	Column(Modifier
		.safeDrawingPadding()
		.fillMaxSize(),
		Arrangement.Center,
		Alignment.CenterHorizontally,
	) {
		OutlinedButton({
			activity.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			)
		}) {
			Text("Input Method Settings")
		}
		OutlinedButton({
			activity.startActivity(Intent(Settings.ACTION_INPUT_METHOD_SUBTYPE_SETTINGS)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			)
		}) {
			Text("Input Method Subtype Settings")
		}
		OutlinedButton({
			activity.getSystemService(InputMethodManager::class.java).showInputMethodPicker()
		}) {
			Text("Show Input Method Picker")
		}

		OutlinedButton({
			activity.importFiles()
		}) {
			Text("Import Files")
		}

		val debugTextPath = remember { activity.externalFilesDir.toPath() / "debug.txt" }
		fun readDebugTextLines() = runCatching {
			debugTextPath.readText()
		}.getOrNull()?.split('\n') ?: listOf()
		var debugTextLines by remember { mutableStateOf(readDebugTextLines()) }
		val debugLazyColumnState = rememberLazyListState()
		LaunchedEffect(debugTextLines) {
			if (debugTextLines.isNotEmpty()) {
				debugLazyColumnState.scrollToItem(debugTextLines.lastIndex)
			}
		}
		LazyColumn(Modifier.heightIn(Dp.Unspecified, 0x80.dp),
			state = debugLazyColumnState,
		) {
			items(debugTextLines) { line ->
				TextField(line, { }, Modifier.fillMaxWidth(),
					textStyle = LocalTextStyle.current.copy(
						textAlign = TextAlign.Center,
					),
				)
			}
		}

		val focusRequester = FocusRequester()
		val keyboardController = LocalSoftwareKeyboardController.current
		LaunchedEffect(Unit) {
			focusRequester.requestFocus()
			keyboardController?.show()
		}
		var testText by remember { mutableStateOf("") }
		OutlinedTextField(testText, {
			testText = it
		}, Modifier.focusRequester(focusRequester),
			label = { Text("Test") },
		)
		OutlinedButton({
			debugTextPath.appendText(testText)
			debugTextLines = readDebugTextLines()
			testText = ""
		}) {
			Text("Save Test Text")
		}
	}
}

val Context.externalFilesDir
	get() = getExternalFilesDir(null) ?: error("Cannot access external files directory.")
