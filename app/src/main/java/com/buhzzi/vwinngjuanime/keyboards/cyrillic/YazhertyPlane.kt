package com.buhzzi.vwinngjuanime.keyboards.cyrillic

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardCapslock
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
import com.buhzzi.vwinngjuanime.keyboards.commitText
import com.buhzzi.vwinngjuanime.keyboards.editor.FunctionalKeysRow
import com.buhzzi.vwinngjuanime.keyboards.latin.BackspaceKey
import com.buhzzi.vwinngjuanime.keyboards.latin.EnterKey
import com.buhzzi.vwinngjuanime.keyboards.latin.MetaKey
import com.buhzzi.vwinngjuanime.keyboards.latin.SpaceKey
import com.buhzzi.vwinngjuanime.keyboards.latin.TabKey
import com.buhzzi.vwinngjuanime.keyboards.tzuih.SpecialsCategory
import com.buhzzi.vwinngjuanime.keyboards.tzuih.SpecialsComposable
import com.buhzzi.vwinngjuanime.keyboards.tzuih.SpecialsItem
import com.buhzzi.vwinngjuanime.keyboards.tzuih.SpecialsKey
import com.buhzzi.vwinngjuanime.keyboards.tzuih.usingSpecials

private enum class CyrilKeySet {
	LOWERCASE,
	UPPERCASE,
}

private var keySet by mutableStateOf(CyrilKeySet.LOWERCASE)
private var nextKeySet by mutableStateOf(CyrilKeySet.LOWERCASE)

@Composable
private fun CaseLatterKey(
	lowerDesc: String,
	upperDesc: String,
	modifier: Modifier = Modifier,
) {
	val desc = when (keySet) {
		CyrilKeySet.LOWERCASE -> lowerDesc
		CyrilKeySet.UPPERCASE -> upperDesc
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
private fun ShiftKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(Icons.Filled.KeyboardCapslock),
		modifier.run {
			if (nextKeySet == CyrilKeySet.UPPERCASE) border(0x1.dp, Color.hsl(0F, 0F, 0.5F))
			else this
		},
	) {
		when (keySet) {
			CyrilKeySet.LOWERCASE -> keySet = CyrilKeySet.UPPERCASE
			CyrilKeySet.UPPERCASE -> when (nextKeySet) {
				CyrilKeySet.LOWERCASE -> nextKeySet = CyrilKeySet.UPPERCASE
				CyrilKeySet.UPPERCASE -> {
					keySet = CyrilKeySet.LOWERCASE
					nextKeySet = CyrilKeySet.LOWERCASE
				}
			}
		}
	}
}

@Composable
private fun CyrilSpecialsComposable() {
	SpecialsComposable(remember {
		listOf(
			SpecialsCategory("Supplement", buildList {
				addAll(('Ԁ' .. 'ԯ').map { SpecialsItem(it) })
			}),
			SpecialsCategory("Ext-A", buildList {
				addAll(('ⷠ' .. 'ⷿ').map { SpecialsItem.combining(it) })
			}),
			SpecialsCategory("Ext-B", buildList {
				addAll(('Ꙁ' .. 'ꙮ').map { SpecialsItem(it) })
				addAll(('꙯' .. 'ꙿ').map { SpecialsItem.combining(it) })
				addAll(('Ꚁ' .. 'ꚝ').map { SpecialsItem(it) })
				addAll(('ꚞ' .. 'ꚟ').map { SpecialsItem.combining(it) })
			}),
			SpecialsCategory("Ext-C", buildList {
				addAll(('ᲀ' .. 'ᲊ').map { SpecialsItem.combining(it) })
			}),
			SpecialsCategory("Ext-D", buildList {
				addAll(("𞀰".codePointAt(0x0) .. "𞁭".codePointAt(0x0)).map { SpecialsItem(it) })
				add(SpecialsItem.combining("𞂏"))
			}),
		)
	})
}

internal val yazhertyPlane: Plane = Plane({ stringResource(R.string.yazherty_plane) }) {
	if (usingSpecials) {
		CyrilSpecialsComposable()
		return@Plane
	}

	Column {
		FunctionalKeysRow(Modifier.weight(1F))
		Row(Modifier.weight(1F)) {
			TabKey(Modifier.weight(1F))
			CaseLatterKey("ю", "Ю", Modifier.weight(1F))
			CaseLatterKey("'", "\"", Modifier.weight(1F))
			CaseLatterKey("ш", "Ш", Modifier.weight(1F))
			CaseLatterKey("щ", "Щ", Modifier.weight(1F))
			CaseLatterKey("э", "Э", Modifier.weight(1F))
			CaseLatterKey("-", "_", Modifier.weight(1F))
			CaseLatterKey("ь", "Ь", Modifier.weight(1F))
			BackspaceKey(Modifier.weight(2F))
		}
		Row(Modifier.weight(1F)) {
			CaseLatterKey("1", "!", Modifier.weight(1F))
			CaseLatterKey("2", "@", Modifier.weight(1F))
			CaseLatterKey("3", "ё", Modifier.weight(1F))
			CaseLatterKey("4", "Ё", Modifier.weight(1F))
			CaseLatterKey("5", "ъ", Modifier.weight(1F))
			CaseLatterKey("6", "Ъ", Modifier.weight(1F))
			CaseLatterKey("7", "&", Modifier.weight(1F))
			CaseLatterKey("8", "*", Modifier.weight(1F))
			CaseLatterKey("9", "(", Modifier.weight(1F))
			CaseLatterKey("0", ")", Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			CaseLatterKey("я", "Я", Modifier.weight(1F))
			CaseLatterKey("ж", "Ж", Modifier.weight(1F))
			CaseLatterKey("е", "Е", Modifier.weight(1F))
			CaseLatterKey("р", "Р", Modifier.weight(1F))
			CaseLatterKey("т", "Т", Modifier.weight(1F))
			CaseLatterKey("ы", "Ы", Modifier.weight(1F))
			CaseLatterKey("у", "У", Modifier.weight(1F))
			CaseLatterKey("и", "И", Modifier.weight(1F))
			CaseLatterKey("о", "О", Modifier.weight(1F))
			CaseLatterKey("п", "П", Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			CaseLatterKey("а", "А", Modifier.weight(1F))
			CaseLatterKey("с", "С", Modifier.weight(1F))
			CaseLatterKey("д", "Д", Modifier.weight(1F))
			CaseLatterKey("ф", "Ф", Modifier.weight(1F))
			CaseLatterKey("г", "Г", Modifier.weight(1F))
			CaseLatterKey("ч", "Ч", Modifier.weight(1F))
			CaseLatterKey("й", "Й", Modifier.weight(1F))
			CaseLatterKey("к", "К", Modifier.weight(1F))
			CaseLatterKey("л", "Л", Modifier.weight(1F))
			CaseLatterKey(";", ":", Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			CaseLatterKey("/", "?", Modifier.weight(1F))
			CaseLatterKey("з", "З", Modifier.weight(1F))
			CaseLatterKey("х", "Х", Modifier.weight(1F))
			CaseLatterKey("ц", "Ц", Modifier.weight(1F))
			CaseLatterKey("в", "В", Modifier.weight(1F))
			CaseLatterKey("б", "Б", Modifier.weight(1F))
			CaseLatterKey("н", "Н", Modifier.weight(1F))
			CaseLatterKey("м", "М", Modifier.weight(1F))
			CaseLatterKey(",", "<", Modifier.weight(1F))
			CaseLatterKey(".", ">", Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			ShiftKey(Modifier.weight(2F))
			MetaKey(Modifier.weight(1F))
			SpaceKey(Modifier.weight(4F))
			SpecialsKey(Modifier.weight(1F)) {
				usingSpecials = true
			}
			EnterKey(Modifier.weight(2F))
		}
	}
}
