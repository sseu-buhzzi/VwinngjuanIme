package com.buhzzi.vwinngjuanime.keyboards.tzuih

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.buhzzi.vwinngjuanime.LocalVwinngjuanIms
import com.buhzzi.vwinngjuanime.R
import com.buhzzi.vwinngjuanime.VwinngjuanIms
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
import kotlin.concurrent.thread

private val LocalTzhuComposer = compositionLocalOf<TzhuComposer?> { null }

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
	val vwinStack = LocalTzhuComposer.current?.vwinStack
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
	val tzhuComposer = LocalTzhuComposer.current
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
	val vwinStack = LocalTzhuComposer.current?.vwinStack
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


internal val vwinngjuanPlane = Plane({ stringResource(R.string.vwinngjuan_plane) }) {
	var tzhuComposer by remember { mutableStateOf<TzhuComposer?>(null) }

	var exceptionNullable by remember { mutableStateOf<Exception?>(null) }

	val ims = LocalVwinngjuanIms.current
	fun tryCreateTzhuComposer() {
		thread {
			try {
				tzhuComposer = TzhuComposer(ims)
				exceptionNullable = null
			} catch (e: Exception) {
				exceptionNullable = e
			}
		}
	}

	CompositionLocalProvider(LocalTzhuComposer provides tzhuComposer) {
		println("tzhuComposer: $tzhuComposer")
		if (tzhuComposer == null) {
			exceptionNullable = Exception("No TzhuComposer provided.")
			tryCreateTzhuComposer()
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
			exceptionNullable?.also { exception ->
				OutlinedSpace(
					Modifier
						.weight(4F)
						.verticalScroll(rememberScrollState())
				) {
					Text(exception.stackTraceToString().also { println(it) })
				}
			} ?: run {
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
			Row(Modifier.weight(1F)) {
				OutlinedKey(KeyContent(Icons.Filled.Refresh), Modifier.weight(2F)) {
					tryCreateTzhuComposer()
				}
				MetaKey(Modifier.weight(1F))
				SpaceKey(Modifier.weight(4F))
				CtrlKey(Modifier.weight(1F))
				EnterKey(Modifier.weight(2F))
			}
		}
	}
}
