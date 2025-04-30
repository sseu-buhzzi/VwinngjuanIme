package com.buhzzi.vwinngjuanime.keyboards.tzuih

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.format.Formatter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.buhzzi.util.bigIntegerToString
import com.buhzzi.util.getSha256Sum
import com.buhzzi.vwinngjuanime.MainActivity
import com.buhzzi.vwinngjuanime.R
import com.buhzzi.vwinngjuanime.VwinngjuanIms
import com.buhzzi.vwinngjuanime.externalFilesDir
import com.buhzzi.vwinngjuanime.keyboards.KeyContent
import com.buhzzi.vwinngjuanime.keyboards.OutlinedKey
import com.buhzzi.vwinngjuanime.keyboards.OutlinedSpace
import com.buhzzi.vwinngjuanime.keyboards.Plane
import com.buhzzi.vwinngjuanime.keyboards.backspaceText
import com.buhzzi.vwinngjuanime.keyboards.commitText
import com.buhzzi.vwinngjuanime.keyboards.editor.FunctionalKeysRow
import com.buhzzi.vwinngjuanime.keyboards.latin.FullwidthSpaceKey
import com.buhzzi.vwinngjuanime.keyboards.latin.MetaKey
import com.buhzzi.vwinngjuanime.keyboards.latin.TabKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.name
import kotlin.io.path.outputStream
import kotlin.io.path.readBytes
import kotlin.math.max
import kotlin.system.exitProcess

internal class LejKeyContentAction(
	val content: KeyContent,
	val action: VwinngjuanIms.() -> Unit,
) {
	override fun toString() = "LejKeyAction($content)"
}

internal var keyTable by mutableStateOf<Map<Char, LejKeyContentAction>?>(null)


@Composable
private fun LejKey(
	keyChar: Char,
	modifier: Modifier = Modifier,
) {
	val vwinStack = savedTzhuComposer?.vwinStack
	(keyTable?.get(keyChar) ?: "$keyChar".let { keyString ->
		LejKeyContentAction(KeyContent(keyString)) {
			vwinStack?.takeIf { it.notEmpty }?.apply {
				commitText(buildTzuih())
				clear()
			}
			commitText(keyString)
		}
	}).apply {
		OutlinedKey(
			content,
			modifier,
			arrayOf(keyTable),
		) { action() }
	}
}

@Composable
internal fun BackspaceKey(modifier: Modifier = Modifier) {
	val tzhuComposer = savedTzhuComposer
	OutlinedKey(
		KeyContent(Icons.AutoMirrored.Filled.Backspace),
		modifier,
	) {
		when {
			tzhuComposer == null -> backspaceText()
			keyTable !== tzhuComposer.lejKeyTable -> keyTable = tzhuComposer.lejKeyTable
			tzhuComposer.vwinStack.notEmpty -> tzhuComposer.vwinStack.pop()
			else -> backspaceText()
		}
	}
}

@Composable
internal fun EnterKey(
	modifier: Modifier = Modifier,
) {
	val vwinStack = savedTzhuComposer?.vwinStack
	OutlinedKey(
		KeyContent(Icons.AutoMirrored.Filled.KeyboardReturn),
		modifier,
	) {
		when {
			vwinStack != null && vwinStack.notEmpty -> {
				commitText(vwinStack.buildTzuih())
				vwinStack.clear()
			}
			else -> commitText("\n")
		}
	}
}


private var usingTzuihSelector by mutableStateOf(false)

@Composable
private fun SelectTzuihComposable(tzhuComposer: TzhuComposer) {
	CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
		val tzhuList = tzhuComposer.vwinStack.tzhuList
		if (tzhuList.isEmpty()) {
			OutlinedKey(KeyContent("無\n文", Icons.Filled.Close)) {
				usingTzuihSelector = false
			}
			return@CompositionLocalProvider
		}
		Column {
			var tzhuIndex by remember { mutableIntStateOf(0x0) }
			Row(Modifier.weight(1F)) {
				if (tzhuIndex <= 0x0) OutlinedSpace(Modifier.weight(1F)) { }
				else OutlinedKey(KeyContent(Icons.AutoMirrored.Filled.NavigateBefore), Modifier.weight(1F)) {
					--tzhuIndex
				}
				OutlinedKey(KeyContent(tzhuList[tzhuIndex].first.pathString), Modifier.weight(4F)
					.horizontalScroll(rememberScrollState()),
					movedThreshold = 0F,
				) {
					usingTzuihSelector = false
				}
				if (tzhuIndex >= tzhuList.lastIndex) OutlinedSpace(Modifier.weight(1F)) { }
				else OutlinedKey(KeyContent(Icons.AutoMirrored.Filled.NavigateNext), Modifier.weight(1F)) {
					++tzhuIndex
				}
			}
			OutlinedSpace(Modifier.weight(4F)) {
				LazyRow {
					items(tzhuList[tzhuIndex].first.tzuihList.withIndex().toList()) { (tzuihIndex, tzuih) ->
						Column(Modifier.width(0x40.dp)) {
							OutlinedSpace(Modifier.weight(1F)) {
								Box {
									Text(tzuih.codePointAt(0x0).toString(0x10).uppercase(), Modifier
										.align(Alignment.Center)
										.padding(0x4.dp),
									)
								}
							}
							OutlinedKey(KeyContent(tzuih), Modifier.weight(2F),
								movedThreshold = 0F,
							) {
								tzhuList[tzhuIndex] = tzhuList[tzhuIndex].first to tzuihIndex
								tzhuComposer.vwinStack.dumpTzuih()
							}
						}
					}
				}
			}
		}
	}
}

private var usingOptions by mutableStateOf(false)

@Composable
private fun OptionsComposable() {
	val ims = VwinngjuanIms.instanceMust

	fun quitOptionsComposable() {
		usingOptions = false
	}

	fun deleteTzhuComposer() {
		savedTzhuComposer = null
		++updatingTzhuComposerTrigger
	}

	fun deleteVwinngjuanFiles() {
		VwinngjuanIms.instance?.externalFilesDir?.toPath()?.let { it / "vwinngjuan" }?.also { vwinngjuanDirPath ->
			sequenceOf("lej.tsv", "tzhu-tree.bin").forEach {
				(vwinngjuanDirPath / it).deleteIfExists()
			}
		}
	}

	val importFilesEndReceiver = remember { object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			deleteTzhuComposer()
		}
	} }
	LaunchedEffect(Unit) {
		ims.registerContextBroadcastReceiver(
			importFilesEndReceiver,
			IntentFilter("com.buhzzi.vwinngjuanime.IMPORT_FILES_END"),
		)
	}
	fun importFiles() {
		ims.startActivity(Intent(ims, MainActivity::class.java)
			.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			.putExtra("import_files", true)
		)
	}

	CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
		Column {
			OutlinedSpace(Modifier
				.weight(2F)
				.verticalScroll(rememberScrollState()),
			) {
				Text(remember { buildString {
					VwinngjuanIms.instance?.externalFilesDir?.toPath()?.let { it / "vwinngjuan" }?.also { vwinngjuanDirPath ->
						fun ByteArray.toHumanFriendlyString() = bigIntegerToString()
							.windowedSequence(0x10, 0x10)
							.joinToString("\n")
						(vwinngjuanDirPath / "lej.tsv").let { path ->
							appendLine("sha256sum ${path.name}:\n${path.takeIf { it.exists() }?.getSha256Sum()?.toHumanFriendlyString()}")
						}
						(vwinngjuanDirPath / "lej.tsv.sha256").let { path ->
							appendLine("${path.name}:\n${path.takeIf { it.exists() }?.readBytes()?.toHumanFriendlyString()}")
						}
						(vwinngjuanDirPath / "tzhu-tree.bin").let { path ->
							appendLine("sha256sum ${path.name}:\n${path.takeIf { it.exists() }?.getSha256Sum()?.toHumanFriendlyString()}")
						}
						(vwinngjuanDirPath / "tzhu-tree.bin.sha256").let { path ->
							appendLine("${path.name}:\n${path.takeIf { it.exists() }?.readBytes()?.toHumanFriendlyString()}")
						}
					}
				} }, textAlign = TextAlign.Center)
			}
			Row(Modifier.weight(1F)) {
				OutlinedKey(KeyContent(Icons.AutoMirrored.Filled.ArrowBack), Modifier.weight(1F)) {
					quitOptionsComposable()
				}
				OutlinedKey(KeyContent("建\n樹"), Modifier.weight(1F)) {
					deleteTzhuComposer()
					quitOptionsComposable()
				}
				OutlinedKey(KeyContent("引\n入"), Modifier.weight(1F)) {
					importFiles()
					quitOptionsComposable()
				}
				OutlinedKey(KeyContent("下\n載"), Modifier.weight(1F)) {
					deleteVwinngjuanFiles()
					deleteTzhuComposer()
					quitOptionsComposable()
				}
				OutlinedKey(KeyContent("終\n止"), Modifier.weight(1F)) {
					exitProcess(0x0)
				}
			}
		}
	}
}

private var updatingTzhuComposerTrigger by mutableIntStateOf(0x0)

internal val vwinngjuanPlane = Plane({ stringResource(R.string.vwinngjuan_plane) },
	onFinishInput = { savedTzhuComposer?.vwinStack?.clear() },
	onWindowHidden = { savedTzhuComposer?.vwinStack?.clear() },
) {
	val ims = VwinngjuanIms.instanceMust

	var creatingTzhuComposerException by remember { mutableStateOf<Exception?>(null) }

	LaunchedEffect(ims, updatingTzhuComposerTrigger) {
		println("Launched effect with $ims, $savedTzhuComposer")
		if (savedTzhuComposer == null) {
			withContext(Dispatchers.IO) {
				try {
					creatingTzhuComposerException = null
					savedTzhuComposer = TzhuComposer()
				} catch (e: Exception) {
					e.printStackTrace()
					creatingTzhuComposerException = e
				}
			}
		}
	}
	val tzhuComposer = savedTzhuComposer

	if (usingTzuihSelector) {
		if (tzhuComposer != null) SelectTzuihComposable(tzhuComposer)
		else usingTzuihSelector = false
		return@Plane
	}

	if (usingOptions) {
		OptionsComposable()
		return@Plane
	}

	Column {
		FunctionalKeysRow(Modifier.weight(1F))
		Row(Modifier.weight(1F)) {
			TabKey(Modifier.weight(1F))
			LejKey('〜', Modifier.weight(1F))
			LejKey('〇', Modifier.weight(1F))
			LejKey('「', Modifier.weight(1F))
			LejKey('」', Modifier.weight(1F))
			LejKey('・', Modifier.weight(1F))
			LejKey('々', Modifier.weight(1F))
			LejKey('〻', Modifier.weight(1F))
			BackspaceKey(Modifier.weight(2F))
		}
		val e = creatingTzhuComposerException
		@Composable
		fun ExceptionContent(text: String) {
			OutlinedSpace(Modifier
				.weight(4F)
				.verticalScroll(rememberScrollState()),
			) {
				Text(text, textAlign = TextAlign.Center)
			}
		}
		when {
			e != null -> ExceptionContent(e.stackTraceToString())
			vwinngjuanResourceName != null -> ExceptionContent("""
				下載$vwinngjuanResourceName
				${Formatter.formatFileSize(ims, vwinngjuanResourceDownloaded)}
			""".trimIndent())
			tzhuComposer == null -> ExceptionContent("組樹: ${null}.")
			else -> {
				Row(Modifier.weight(1F)) {
					val vwinStack = tzhuComposer.vwinStack
					val tzhuList = vwinStack.tzhuList
					if (tzhuList.isEmpty()) {
						LejKey('１', Modifier.weight(1F))
						LejKey('２', Modifier.weight(1F))
						LejKey('３', Modifier.weight(1F))
						LejKey('４', Modifier.weight(1F))
						LejKey('５', Modifier.weight(1F))
						LejKey('６', Modifier.weight(1F))
						LejKey('７', Modifier.weight(1F))
						LejKey('８', Modifier.weight(1F))
						LejKey('９', Modifier.weight(1F))
						LejKey('０', Modifier.weight(1F))
					} else {
						val tzuihList = tzhuList.flatMap { tzhu -> tzhu.first.tzuihList }
						val candidateNumber = 0xa
						val leadingSpacesNumber = max(candidateNumber - tzuihList.size, 0x0) / 0x2
						for (tzuihIndex in 0x0 ..< candidateNumber) {
							tzuihList.getOrNull(tzuihIndex - leadingSpacesNumber)?.let { tzuih ->
								OutlinedKey(KeyContent(tzuih), Modifier.weight(1F)) {
									commitText(tzuih)
									vwinStack.clear()
								}
							} ?: OutlinedSpace(Modifier.weight(1F)) { }
						}
					}
				}
				Row(Modifier.weight(1F)) {
					LejKey('ｑ', Modifier.weight(1F))
					LejKey('ｗ', Modifier.weight(1F))
					LejKey('ｅ', Modifier.weight(1F))
					LejKey('ｒ', Modifier.weight(1F))
					LejKey('ｔ', Modifier.weight(1F))
					LejKey('ｙ', Modifier.weight(1F))
					LejKey('ｕ', Modifier.weight(1F))
					LejKey('ｉ', Modifier.weight(1F))
					LejKey('ｏ', Modifier.weight(1F))
					LejKey('ｐ', Modifier.weight(1F))
				}
				Row(Modifier.weight(1F)) {
					LejKey('ａ', Modifier.weight(1F))
					LejKey('ｓ', Modifier.weight(1F))
					LejKey('ｄ', Modifier.weight(1F))
					LejKey('ｆ', Modifier.weight(1F))
					LejKey('ｇ', Modifier.weight(1F))
					LejKey('ｈ', Modifier.weight(1F))
					LejKey('ｊ', Modifier.weight(1F))
					LejKey('ｋ', Modifier.weight(1F))
					LejKey('ｌ', Modifier.weight(1F))
					LejKey('〃', Modifier.weight(1F))
				}
				Row(Modifier.weight(1F)) {
					LejKey('〾', Modifier.weight(1F))
					LejKey('ｚ', Modifier.weight(1F))
					LejKey('ｘ', Modifier.weight(1F))
					LejKey('ｃ', Modifier.weight(1F))
					LejKey('ｖ', Modifier.weight(1F))
					LejKey('ｂ', Modifier.weight(1F))
					LejKey('ｎ', Modifier.weight(1F))
					LejKey('ｍ', Modifier.weight(1F))
					LejKey('、', Modifier.weight(1F))
					LejKey('。', Modifier.weight(1F))
				}
			}
		}
		Row(Modifier.weight(1F)) {
			OutlinedKey(KeyContent(Icons.Filled.MoreVert), Modifier.weight(1F)) {
				usingTzuihSelector = true
			}
			OutlinedKey(KeyContent(Icons.Filled.Settings), Modifier.weight(1F)) {
				usingOptions = true
			}
			MetaKey(Modifier.weight(1F))
			FullwidthSpaceKey(Modifier.weight(4F))
			SpecialsKey(tzuihSpecialsPlane, Modifier.weight(1F))
			EnterKey(Modifier.weight(2F))
		}
	}
}

private val tzuihSpecialsPlane = Plane({ stringResource(R.string.tzuih_specials_plane) }) {
	CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
		SpecialsComposable(remember { listOf(
			SpecialsCategory("注\n音", buildList {
				addAll(('ㄅ' .. 'ㄯ').map { SpecialsItem(it) })
				addAll(('ㆠ' .. 'ㆿ').map { SpecialsItem(it) })
			}),
			SpecialsCategory("僞\n康", buildList {
				addAll(('⼀' .. '⿕').map { SpecialsItem(it) })
			}),
			SpecialsCategory("述\n字", buildList {
				addAll(('⿰' .. '⿿').map { SpecialsItem(it) })
				addAll(('〾' .. '〿').map { SpecialsItem(it) })
			}),
			SpecialsCategory("文\n法", buildList {
				addAll(('　' .. '】').map { SpecialsItem(it) })
				addAll(('〔' .. '〟').map { SpecialsItem(it) })
				add(SpecialsItem('〰'))
				addAll(('︰' .. '﹏').map { SpecialsItem(it) })
			}),
			SpecialsCategory("直\n書", buildList {
				addAll(('︐' .. '︙').map { SpecialsItem(it) })
			}),
			SpecialsCategory("序\n號", buildList {
				addAll(('㈠' .. '㍰').map { SpecialsItem(it) })
				addAll(('㍺' .. '㍿').map { SpecialsItem(it) })
				addAll(('㏠' .. '㏾').map { SpecialsItem(it) })
			}),
			SpecialsCategory("單\n位", buildList {
				addAll(('㍱' .. '㍹').map { SpecialsItem(it) })
				addAll(('㎀' .. '㏟').map { SpecialsItem(it) })
				addAll(('㏿' .. '㏿').map { SpecialsItem(it) })
			}),
		) })
	}
}
