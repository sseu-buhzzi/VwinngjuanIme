package com.buhzzi.vwinngjuanime.keyboards.tzuih

import android.text.format.Formatter
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.buhzzi.vwinngjuanime.R
import com.buhzzi.vwinngjuanime.VwinngjuanIms
import com.buhzzi.vwinngjuanime.externalFilesDir
import com.buhzzi.vwinngjuanime.keyboards.KeyContent
import com.buhzzi.vwinngjuanime.keyboards.OutlinedKey
import com.buhzzi.vwinngjuanime.keyboards.OutlinedSpace
import com.buhzzi.vwinngjuanime.keyboards.Plane
import com.buhzzi.vwinngjuanime.keyboards.backspaceText
import com.buhzzi.vwinngjuanime.keyboards.commitText
import com.buhzzi.vwinngjuanime.keyboards.latin.CtrlKey
import com.buhzzi.vwinngjuanime.keyboards.latin.MetaKey
import com.buhzzi.vwinngjuanime.keyboards.latin.SpaceKey
import com.buhzzi.vwinngjuanime.keyboards.latin.TabKey
import com.buhzzi.vwinngjuanime.keyboards.latin.WithActionKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.io.path.deleteIfExists
import kotlin.io.path.div

internal class LejKeyAction(
	val content: KeyContent,
	val action: VwinngjuanIms.() -> Unit,
) {
	override fun toString() = "LejKeyAction($content)"
}

internal var keyMap by mutableStateOf<Map<Char, LejKeyAction>?>(null)


@Composable
private fun LejKey(
	keyChar: Char,
	modifier: Modifier = Modifier,
) {
	val vwinStack = savedTzhuComposer?.vwinStack
	(keyMap?.get(keyChar) ?: "$keyChar".let { keyString ->
		LejKeyAction(KeyContent(keyString)) {
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
			arrayOf(keyMap),
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
			keyMap !== tzhuComposer.lejKeyMap -> keyMap = tzhuComposer.lejKeyMap
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
							OutlinedKey(KeyContent(tzuih), Modifier.weight(2F)) {
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

private fun quitOptionsComposable() {
	usingOptions = false
}

private fun deleteTzhuComposer() {
	++updatingTzhuComposerTrigger
	savedTzhuComposer = null
}

private fun deleteVwinngjuanFiles() {
	VwinngjuanIms.instance?.externalFilesDir?.toPath()?.let { it / "vwinngjuan" }?.also { vwinngjuanDir ->
		sequenceOf("lej.tsv", "tzhu.tsv").forEach {
			(vwinngjuanDir / it).deleteIfExists()
		}
	}
}

@Composable
private fun OptionsComposable() {
	CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
		Column {
			Row {
				OutlinedKey(KeyContent(Icons.AutoMirrored.Filled.ArrowBack), Modifier.weight(1F)) {
					quitOptionsComposable()
				}
				OutlinedKey(KeyContent("建\n樹"), Modifier.weight(1F)) {
					deleteTzhuComposer()
					quitOptionsComposable()
				}
				OutlinedKey(KeyContent("下\n載"), Modifier.weight(1F)) {
					deleteVwinngjuanFiles()
					deleteTzhuComposer()
					quitOptionsComposable()
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
		Row(Modifier.weight(1F)) {
			TabKey(Modifier.weight(1F))
			LejKey('`', Modifier.weight(1F))
			LejKey('\'', Modifier.weight(1F))
			LejKey('「', Modifier.weight(1F))
			LejKey('」', Modifier.weight(1F))
			LejKey('・', Modifier.weight(1F))
			LejKey('-', Modifier.weight(1F))
			LejKey('=', Modifier.weight(1F))
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
		val tzhu = TzhuComposer.lastCreatedTzhu
		when {
			vwinngjuanResourceName != null -> ExceptionContent("""
				下載$vwinngjuanResourceName
				${Formatter.formatFileSize(ims, vwinngjuanResourceDownloaded)}
			""".trimIndent())
			tzhu != null -> ExceptionContent("""
				建樹
				${tzhu.first}
				${tzhu.second.pathString}
			""".trimIndent())
			e != null -> ExceptionContent(e.stackTraceToString())
			tzhuComposer == null -> ExceptionContent("組樹: ${null}.")
			else -> {
				Row(Modifier.weight(1F)) {
					LejKey('1', Modifier.weight(1F))
					LejKey('2', Modifier.weight(1F))
					LejKey('3', Modifier.weight(1F))
					LejKey('4', Modifier.weight(1F))
					LejKey('5', Modifier.weight(1F))
					LejKey('6', Modifier.weight(1F))
					LejKey('7', Modifier.weight(1F))
					LejKey('8', Modifier.weight(1F))
					LejKey('9', Modifier.weight(1F))
					LejKey('0', Modifier.weight(1F))
				}
				Row(Modifier.weight(1F)) {
					LejKey('q', Modifier.weight(1F))
					LejKey('w', Modifier.weight(1F))
					LejKey('e', Modifier.weight(1F))
					LejKey('r', Modifier.weight(1F))
					LejKey('t', Modifier.weight(1F))
					LejKey('y', Modifier.weight(1F))
					LejKey('u', Modifier.weight(1F))
					LejKey('i', Modifier.weight(1F))
					LejKey('o', Modifier.weight(1F))
					LejKey('p', Modifier.weight(1F))
				}
				Row(Modifier.weight(1F)) {
					LejKey('a', Modifier.weight(1F))
					LejKey('s', Modifier.weight(1F))
					LejKey('d', Modifier.weight(1F))
					LejKey('f', Modifier.weight(1F))
					LejKey('g', Modifier.weight(1F))
					LejKey('h', Modifier.weight(1F))
					LejKey('j', Modifier.weight(1F))
					LejKey('k', Modifier.weight(1F))
					LejKey('l', Modifier.weight(1F))
					LejKey(';', Modifier.weight(1F))
				}
				Row(Modifier.weight(1F)) {
					LejKey('/', Modifier.weight(1F))
					LejKey('z', Modifier.weight(1F))
					LejKey('x', Modifier.weight(1F))
					LejKey('c', Modifier.weight(1F))
					LejKey('v', Modifier.weight(1F))
					LejKey('b', Modifier.weight(1F))
					LejKey('n', Modifier.weight(1F))
					LejKey('m', Modifier.weight(1F))
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
			SpaceKey(Modifier.weight(4F))
			CtrlKey(Modifier.weight(1F))
			WithActionKey(Modifier.weight(2F)) {
				EnterKey(Modifier.weight(1F))
			}
		}
	}
}
