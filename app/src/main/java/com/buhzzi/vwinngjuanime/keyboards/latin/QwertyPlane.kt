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
import com.buhzzi.vwinngjuanime.R
import com.buhzzi.vwinngjuanime.VwinngjuanIms
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
	ims: VwinngjuanIms,
	lowerDesc: String,
	upperDesc: String,
	modifier: Modifier = Modifier,
) {
	val desc = when (latinKeySet) {
		LatinKeySet.LOWERCASE -> lowerDesc
		LatinKeySet.UPPERCASE -> upperDesc
	}
	OutlinedKey(
		ims,
		KeyContent(desc),
		modifier,
		arrayOf(latinKeySet),
	) {
		commitText(desc)
	}
}

@Composable
internal fun TabKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent(Icons.AutoMirrored.Filled.KeyboardTab),
		modifier,
	) {
		commitText("\t")
	}
}

@Composable
internal fun BackspaceKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent(Icons.AutoMirrored.Filled.Backspace),
		modifier,
	) {
		backspaceText()
	}
}

@Composable
private fun ShiftKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
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
internal fun MetaKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent(Icons.Filled.KeyboardCommandKey),
		modifier,
	) {
		goToPlane(navigatorPlane)
	}
}

@Composable
internal fun SpaceKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent(Icons.Filled.SpaceBar),
		modifier,
	) {
		commitText(" ")
	}
}

@Composable
internal fun CtrlKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent(Icons.Filled.KeyboardControlKey),
		modifier,
	) {
		goToPlane(editorPlane)
	}
}

@Composable
internal fun EnterKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent(Icons.AutoMirrored.Filled.KeyboardReturn),
		modifier,
	) {
		commitText("\n")
	}
}

internal val qwertyPlane: Plane = Plane({ stringResource(R.string.qwerty_plane) }) { ims ->
	Column {
		Row(Modifier.weight(1F)) {
			TabKey(ims, Modifier.weight(1F))
			CaseLatterKey(ims, "`", "~", Modifier.weight(1F))
			CaseLatterKey(ims, "'", "\"", Modifier.weight(1F))
			CaseLatterKey(ims, "[", "{", Modifier.weight(1F))
			CaseLatterKey(ims, "]", "}", Modifier.weight(1F))
			CaseLatterKey(ims, "\\", "|", Modifier.weight(1F))
			CaseLatterKey(ims, "-", "_", Modifier.weight(1F))
			CaseLatterKey(ims, "=", "+", Modifier.weight(1F))
			BackspaceKey(ims, Modifier.weight(2F))
		}
		Row(Modifier.weight(1F)) {
			CaseLatterKey(ims, "1", "!", Modifier.weight(1F))
			CaseLatterKey(ims, "2", "@", Modifier.weight(1F))
			CaseLatterKey(ims, "3", "#", Modifier.weight(1F))
			CaseLatterKey(ims, "4", "$", Modifier.weight(1F))
			CaseLatterKey(ims, "5", "%", Modifier.weight(1F))
			CaseLatterKey(ims, "6", "^", Modifier.weight(1F))
			CaseLatterKey(ims, "7", "&", Modifier.weight(1F))
			CaseLatterKey(ims, "8", "*", Modifier.weight(1F))
			CaseLatterKey(ims, "9", "(", Modifier.weight(1F))
			CaseLatterKey(ims, "0", ")", Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			CaseLatterKey(ims, "q", "Q", Modifier.weight(1F))
			CaseLatterKey(ims, "w", "W", Modifier.weight(1F))
			CaseLatterKey(ims, "e", "E", Modifier.weight(1F))
			CaseLatterKey(ims, "r", "R", Modifier.weight(1F))
			CaseLatterKey(ims, "t", "T", Modifier.weight(1F))
			CaseLatterKey(ims, "y", "Y", Modifier.weight(1F))
			CaseLatterKey(ims, "u", "U", Modifier.weight(1F))
			CaseLatterKey(ims, "i", "I", Modifier.weight(1F))
			CaseLatterKey(ims, "o", "O", Modifier.weight(1F))
			CaseLatterKey(ims, "p", "P", Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			CaseLatterKey(ims, "a", "A", Modifier.weight(1F))
			CaseLatterKey(ims, "s", "S", Modifier.weight(1F))
			CaseLatterKey(ims, "d", "D", Modifier.weight(1F))
			CaseLatterKey(ims, "f", "F", Modifier.weight(1F))
			CaseLatterKey(ims, "g", "G", Modifier.weight(1F))
			CaseLatterKey(ims, "h", "H", Modifier.weight(1F))
			CaseLatterKey(ims, "j", "J", Modifier.weight(1F))
			CaseLatterKey(ims, "k", "K", Modifier.weight(1F))
			CaseLatterKey(ims, "l", "L", Modifier.weight(1F))
			CaseLatterKey(ims, ";", ":", Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			CaseLatterKey(ims, "/", "?", Modifier.weight(1F))
			CaseLatterKey(ims, "z", "Z", Modifier.weight(1F))
			CaseLatterKey(ims, "x", "X", Modifier.weight(1F))
			CaseLatterKey(ims, "c", "C", Modifier.weight(1F))
			CaseLatterKey(ims, "v", "V", Modifier.weight(1F))
			CaseLatterKey(ims, "b", "B", Modifier.weight(1F))
			CaseLatterKey(ims, "n", "N", Modifier.weight(1F))
			CaseLatterKey(ims, "m", "M", Modifier.weight(1F))
			CaseLatterKey(ims, ",", "<", Modifier.weight(1F))
			CaseLatterKey(ims, ".", ">", Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			ShiftKey(ims, Modifier.weight(2F))
			MetaKey(ims, Modifier.weight(1F))
			SpaceKey(ims, Modifier.weight(4F))
			CtrlKey(ims, Modifier.weight(1F))
			(ims.imeOptions and EditorInfo.IME_MASK_ACTION).also { println("${ims.imeOptions.toString(0x10)} after masked: ${it.toString(0x10)}") }
			when (ims.imeOptions and EditorInfo.IME_MASK_ACTION) {
				EditorInfo.IME_ACTION_GO -> {
					println("ime action go")
					EnterKey(ims, Modifier.weight(1F))
					ActionGoKey(ims, Modifier.weight(1F))
				}
				EditorInfo.IME_ACTION_SEARCH -> {
					println("ime action search")
					EnterKey(ims, Modifier.weight(1F))
					ActionSearchKey(ims, Modifier.weight(1F))
				}
				EditorInfo.IME_ACTION_SEND -> {
					println("ime action send")
					EnterKey(ims, Modifier.weight(1F))
					ActionSendKey(ims, Modifier.weight(1F))
				}
				EditorInfo.IME_ACTION_NEXT -> {
					println("ime action next")
					EnterKey(ims, Modifier.weight(1F))
					ActionNextKey(ims, Modifier.weight(1F))
				}
				EditorInfo.IME_ACTION_DONE -> {
					println("ime action done")
					EnterKey(ims, Modifier.weight(1F))
					ActionDoneKey(ims, Modifier.weight(1F))
				}
				EditorInfo.IME_ACTION_PREVIOUS -> {
					println("ime action previous")
					EnterKey(ims, Modifier.weight(1F))
					ActionPreviousKey(ims, Modifier.weight(1F))
				}
				else -> {
					println("no ime action")
					EnterKey(ims, Modifier.weight(2F))
				}
			}
		}
	}
}
