package com.buhzzi.vwinngjuanime.keyboards.latin

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.automirrored.filled.KeyboardTab
import androidx.compose.material.icons.filled.KeyboardCapslock
import androidx.compose.material.icons.filled.KeyboardCommandKey
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.buhzzi.vwinngjuanime.R
import com.buhzzi.vwinngjuanime.keyboards.KeyContent
import com.buhzzi.vwinngjuanime.keyboards.OutlinedKey
import com.buhzzi.vwinngjuanime.keyboards.Plane
import com.buhzzi.vwinngjuanime.keyboards.backspaceText
import com.buhzzi.vwinngjuanime.keyboards.commitText
import com.buhzzi.vwinngjuanime.keyboards.editor.FunctionalKeysRow
import com.buhzzi.vwinngjuanime.keyboards.goToPlane
import com.buhzzi.vwinngjuanime.keyboards.navigatorPlane
import com.buhzzi.vwinngjuanime.keyboards.tzuih.SpecialsCategory
import com.buhzzi.vwinngjuanime.keyboards.tzuih.SpecialsComposable
import com.buhzzi.vwinngjuanime.keyboards.tzuih.SpecialsItem
import com.buhzzi.vwinngjuanime.keyboards.tzuih.SpecialsKey

private enum class LatinKeySet {
	LOWERCASE,
	UPPERCASE,
}

private var keySet by mutableStateOf(LatinKeySet.LOWERCASE)
private var nextKeySet by mutableStateOf(LatinKeySet.LOWERCASE)

@Composable
private fun CaseLatterKey(
	lowerDesc: String,
	upperDesc: String,
	modifier: Modifier = Modifier,
) {
	val desc = when (keySet) {
		LatinKeySet.LOWERCASE -> lowerDesc
		LatinKeySet.UPPERCASE -> upperDesc
	}
	OutlinedKey(
		KeyContent(desc),
		modifier,
		arrayOf(keySet),
	) {
		commitText(desc)
		keySet = nextKeySet
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
		modifier.run {
			if (nextKeySet == LatinKeySet.UPPERCASE) border(0x1.dp, Color.hsl(0F, 0F, 0.5F))
			else this
		},
	) {
		when (keySet) {
			LatinKeySet.LOWERCASE -> keySet = LatinKeySet.UPPERCASE
			LatinKeySet.UPPERCASE -> when (nextKeySet) {
				LatinKeySet.LOWERCASE -> nextKeySet = LatinKeySet.UPPERCASE
				LatinKeySet.UPPERCASE -> {
					keySet = LatinKeySet.LOWERCASE
					nextKeySet = LatinKeySet.LOWERCASE
				}
			}
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
		FunctionalKeysRow(Modifier.weight(1F))
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
			SpecialsKey(latinSpecialsPlane, Modifier.weight(1F))
			EnterKey(Modifier.weight(2F))
		}
	}
}

@Composable
internal fun FullwidthSpaceKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(Icons.Filled.SpaceBar),
		modifier,
	) {
		commitText("　")
	}
}

internal val qwertyFullwidthPlane: Plane = Plane({ stringResource(R.string.qwerty_fullwidth_plane) }) {
	Column {
		FunctionalKeysRow(Modifier.weight(1F))
		Row(Modifier.weight(1F)) {
			TabKey(Modifier.weight(1F))
			CaseLatterKey("｀", "～", Modifier.weight(1F))
			CaseLatterKey("＇", "＂", Modifier.weight(1F))
			CaseLatterKey("［", "｛", Modifier.weight(1F))
			CaseLatterKey("］", "｝", Modifier.weight(1F))
			CaseLatterKey("＼", "｜", Modifier.weight(1F))
			CaseLatterKey("－", "＿", Modifier.weight(1F))
			CaseLatterKey("＝", "＋", Modifier.weight(1F))
			BackspaceKey(Modifier.weight(2F))
		}
		Row(Modifier.weight(1F)) {
			CaseLatterKey("０", "！", Modifier.weight(1F))
			CaseLatterKey("１", "@", Modifier.weight(1F))
			CaseLatterKey("２", "＃", Modifier.weight(1F))
			CaseLatterKey("３", "＄", Modifier.weight(1F))
			CaseLatterKey("４", "％", Modifier.weight(1F))
			CaseLatterKey("５", "＾", Modifier.weight(1F))
			CaseLatterKey("６", "＆", Modifier.weight(1F))
			CaseLatterKey("７", "＊", Modifier.weight(1F))
			CaseLatterKey("８", "（", Modifier.weight(1F))
			CaseLatterKey("９", "）", Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			CaseLatterKey("ｑ", "Ｑ", Modifier.weight(1F))
			CaseLatterKey("ｗ", "Ｗ", Modifier.weight(1F))
			CaseLatterKey("ｅ", "Ｅ", Modifier.weight(1F))
			CaseLatterKey("ｒ", "Ｒ", Modifier.weight(1F))
			CaseLatterKey("ｔ", "Ｔ", Modifier.weight(1F))
			CaseLatterKey("ｙ", "Ｙ", Modifier.weight(1F))
			CaseLatterKey("ｕ", "Ｕ", Modifier.weight(1F))
			CaseLatterKey("ｉ", "Ｉ", Modifier.weight(1F))
			CaseLatterKey("ｏ", "Ｏ", Modifier.weight(1F))
			CaseLatterKey("ｐ", "Ｐ", Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			CaseLatterKey("ａ", "Ａ", Modifier.weight(1F))
			CaseLatterKey("ｓ", "Ｓ", Modifier.weight(1F))
			CaseLatterKey("ｄ", "Ｄ", Modifier.weight(1F))
			CaseLatterKey("ｆ", "Ｆ", Modifier.weight(1F))
			CaseLatterKey("ｇ", "Ｇ", Modifier.weight(1F))
			CaseLatterKey("ｈ", "Ｈ", Modifier.weight(1F))
			CaseLatterKey("ｊ", "Ｊ", Modifier.weight(1F))
			CaseLatterKey("ｋ", "Ｋ", Modifier.weight(1F))
			CaseLatterKey("ｌ", "Ｌ", Modifier.weight(1F))
			CaseLatterKey("；", "：", Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			CaseLatterKey("／", "？", Modifier.weight(1F))
			CaseLatterKey("ｚ", "Ｚ", Modifier.weight(1F))
			CaseLatterKey("ｘ", "Ｘ", Modifier.weight(1F))
			CaseLatterKey("ｃ", "Ｃ", Modifier.weight(1F))
			CaseLatterKey("ｖ", "Ｖ", Modifier.weight(1F))
			CaseLatterKey("ｂ", "Ｂ", Modifier.weight(1F))
			CaseLatterKey("ｎ", "Ｎ", Modifier.weight(1F))
			CaseLatterKey("ｍ", "Ｍ", Modifier.weight(1F))
			CaseLatterKey("，", "＜", Modifier.weight(1F))
			CaseLatterKey("．", "＞", Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			ShiftKey(Modifier.weight(2F))
			MetaKey(Modifier.weight(1F))
			FullwidthSpaceKey(Modifier.weight(4F))
			SpecialsKey(latinSpecialsPlane, Modifier.weight(1F))
			EnterKey(Modifier.weight(2F))
		}
	}
}

private val latinSpecialsPlane = Plane({ stringResource(R.string.latin_specials_plane) }) {
	SpecialsComposable(remember { listOf(
		SpecialsCategory("Small Form", buildList {
			addAll(('﹐' .. '﹫').map { SpecialsItem(it) })
		}),
		SpecialsCategory("Letterlike", buildList {
			addAll(('℀' .. '⅏').map { SpecialsItem(it) })
		})
	) })
}
