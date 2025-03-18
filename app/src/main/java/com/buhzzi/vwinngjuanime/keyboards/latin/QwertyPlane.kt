package com.buhzzi.vwinngjuanime.keyboards.latin

import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.automirrored.filled.KeyboardTab
import androidx.compose.material.icons.filled.KeyboardCapslock
import androidx.compose.material.icons.filled.KeyboardCommandKey
import androidx.compose.material.icons.filled.KeyboardControlKey
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.buhzzi.vwinngjuanime.LocalVwinngjuanIms
import com.buhzzi.vwinngjuanime.R
import com.buhzzi.vwinngjuanime.keyboards.ActionDoneKey
import com.buhzzi.vwinngjuanime.keyboards.ActionGoKey
import com.buhzzi.vwinngjuanime.keyboards.ActionNextKey
import com.buhzzi.vwinngjuanime.keyboards.ActionPreviousKey
import com.buhzzi.vwinngjuanime.keyboards.ActionSearchKey
import com.buhzzi.vwinngjuanime.keyboards.ActionSendKey
import com.buhzzi.vwinngjuanime.keyboards.KeyContent
import com.buhzzi.vwinngjuanime.keyboards.OutlinedKey
import com.buhzzi.vwinngjuanime.keyboards.Plane
import com.buhzzi.vwinngjuanime.keyboards.backspaceText
import com.buhzzi.vwinngjuanime.keyboards.commitText
import com.buhzzi.vwinngjuanime.keyboards.editor.editorPlane
import com.buhzzi.vwinngjuanime.keyboards.goToPlane
import com.buhzzi.vwinngjuanime.keyboards.navigatorPlane

private enum class LatinKeySet {
	LOWERCASE,
	UPPERCASE,
}

private var latinKeySet by mutableStateOf(LatinKeySet.LOWERCASE)

@Composable
private fun CaseLatterKey(
	lowerDesc: String,
	upperDesc: String,
	modifier: Modifier = Modifier,
) {
	val desc = when (latinKeySet) {
		LatinKeySet.LOWERCASE -> lowerDesc
		LatinKeySet.UPPERCASE -> upperDesc
	}
	OutlinedKey(
		KeyContent(desc),
		modifier,
		arrayOf(latinKeySet),
	) {
		commitText(desc)
		latinKeySet = LatinKeySet.LOWERCASE
	}
}

@Composable
internal fun TabKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(Icons.AutoMirrored.Filled.KeyboardTab),
		modifier,
	) {
		commitText("\t")
	}
}

@Composable
internal fun BackspaceKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(Icons.AutoMirrored.Filled.Backspace),
		modifier,
	) {
		backspaceText()
	}
}

@Composable
private fun ShiftKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(Icons.Filled.KeyboardCapslock),
		modifier,
	) {
		latinKeySet = when (latinKeySet) {
			LatinKeySet.LOWERCASE -> LatinKeySet.UPPERCASE
			LatinKeySet.UPPERCASE -> LatinKeySet.LOWERCASE
		}
	}
}

@Composable
internal fun MetaKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(Icons.Filled.KeyboardCommandKey),
		modifier,
	) {
		goToPlane(navigatorPlane)
	}
}

@Composable
internal fun SpaceKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(Icons.Filled.SpaceBar),
		modifier,
	) {
		commitText(" ")
	}
}

@Composable
internal fun CtrlKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(Icons.Filled.KeyboardControlKey),
		modifier,
	) {
		goToPlane(editorPlane)
	}
}

@Composable
internal fun EnterKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(Icons.AutoMirrored.Filled.KeyboardReturn),
		modifier,
	) {
		commitText("\n")
	}
}

internal val qwertyPlane: Plane = Plane({ stringResource(R.string.qwerty_plane) }) {
	Column {
		Row(Modifier.weight(1F)) {
			TabKey(Modifier.weight(1F))
			CaseLatterKey("`", "~", Modifier.weight(1F))
			CaseLatterKey("'", "\"", Modifier.weight(1F))
			CaseLatterKey("[", "{", Modifier.weight(1F))
			CaseLatterKey("]", "}", Modifier.weight(1F))
			CaseLatterKey("\\", "|", Modifier.weight(1F))
			CaseLatterKey("-", "_", Modifier.weight(1F))
			CaseLatterKey("=", "+", Modifier.weight(1F))
			BackspaceKey(Modifier.weight(2F))
		}
		Row(Modifier.weight(1F)) {
			CaseLatterKey("1", "!", Modifier.weight(1F))
			CaseLatterKey("2", "@", Modifier.weight(1F))
			CaseLatterKey("3", "#", Modifier.weight(1F))
			CaseLatterKey("4", "$", Modifier.weight(1F))
			CaseLatterKey("5", "%", Modifier.weight(1F))
			CaseLatterKey("6", "^", Modifier.weight(1F))
			CaseLatterKey("7", "&", Modifier.weight(1F))
			CaseLatterKey("8", "*", Modifier.weight(1F))
			CaseLatterKey("9", "(", Modifier.weight(1F))
			CaseLatterKey("0", ")", Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			CaseLatterKey("q", "Q", Modifier.weight(1F))
			CaseLatterKey("w", "W", Modifier.weight(1F))
			CaseLatterKey("e", "E", Modifier.weight(1F))
			CaseLatterKey("r", "R", Modifier.weight(1F))
			CaseLatterKey("t", "T", Modifier.weight(1F))
			CaseLatterKey("y", "Y", Modifier.weight(1F))
			CaseLatterKey("u", "U", Modifier.weight(1F))
			CaseLatterKey("i", "I", Modifier.weight(1F))
			CaseLatterKey("o", "O", Modifier.weight(1F))
			CaseLatterKey("p", "P", Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			CaseLatterKey("a", "A", Modifier.weight(1F))
			CaseLatterKey("s", "S", Modifier.weight(1F))
			CaseLatterKey("d", "D", Modifier.weight(1F))
			CaseLatterKey("f", "F", Modifier.weight(1F))
			CaseLatterKey("g", "G", Modifier.weight(1F))
			CaseLatterKey("h", "H", Modifier.weight(1F))
			CaseLatterKey("j", "J", Modifier.weight(1F))
			CaseLatterKey("k", "K", Modifier.weight(1F))
			CaseLatterKey("l", "L", Modifier.weight(1F))
			CaseLatterKey(";", ":", Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			CaseLatterKey("/", "?", Modifier.weight(1F))
			CaseLatterKey("z", "Z", Modifier.weight(1F))
			CaseLatterKey("x", "X", Modifier.weight(1F))
			CaseLatterKey("c", "C", Modifier.weight(1F))
			CaseLatterKey("v", "V", Modifier.weight(1F))
			CaseLatterKey("b", "B", Modifier.weight(1F))
			CaseLatterKey("n", "N", Modifier.weight(1F))
			CaseLatterKey("m", "M", Modifier.weight(1F))
			CaseLatterKey(",", "<", Modifier.weight(1F))
			CaseLatterKey(".", ">", Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			ShiftKey(Modifier.weight(2F))
			MetaKey(Modifier.weight(1F))
			SpaceKey(Modifier.weight(4F))
			CtrlKey(Modifier.weight(1F))
			val ims = LocalVwinngjuanIms.current
			(ims.imeOptions and EditorInfo.IME_MASK_ACTION).also { println("${ims.imeOptions.toString(0x10)} after masked: ${it.toString(0x10)}") }
			when (ims.imeOptions and EditorInfo.IME_MASK_ACTION) {
				EditorInfo.IME_ACTION_GO -> {
					println("ime action go")
					EnterKey(Modifier.weight(1F))
					ActionGoKey(Modifier.weight(1F))
				}
				EditorInfo.IME_ACTION_SEARCH -> {
					println("ime action search")
					EnterKey(Modifier.weight(1F))
					ActionSearchKey(Modifier.weight(1F))
				}
				EditorInfo.IME_ACTION_SEND -> {
					println("ime action send")
					EnterKey(Modifier.weight(1F))
					ActionSendKey(Modifier.weight(1F))
				}
				EditorInfo.IME_ACTION_NEXT -> {
					println("ime action next")
					EnterKey(Modifier.weight(1F))
					ActionNextKey(Modifier.weight(1F))
				}
				EditorInfo.IME_ACTION_DONE -> {
					println("ime action done")
					EnterKey(Modifier.weight(1F))
					ActionDoneKey(Modifier.weight(1F))
				}
				EditorInfo.IME_ACTION_PREVIOUS -> {
					println("ime action previous")
					EnterKey(Modifier.weight(1F))
					ActionPreviousKey(Modifier.weight(1F))
				}
				else -> {
					println("no ime action")
					EnterKey(Modifier.weight(2F))
				}
			}
		}
	}
}
