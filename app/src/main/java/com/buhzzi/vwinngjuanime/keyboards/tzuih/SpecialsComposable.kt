package com.buhzzi.vwinngjuanime.keyboards.tzuih

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiSymbols
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.buhzzi.vwinngjuanime.keyboards.KeyContent
import com.buhzzi.vwinngjuanime.keyboards.OutlinedKey
import com.buhzzi.vwinngjuanime.keyboards.OutlinedSpace
import com.buhzzi.vwinngjuanime.keyboards.Plane
import com.buhzzi.vwinngjuanime.keyboards.commitText
import com.buhzzi.vwinngjuanime.keyboards.goToPlane
import com.buhzzi.vwinngjuanime.keyboards.latin.BackspaceKey
import com.buhzzi.vwinngjuanime.keyboards.planeGoBack

internal data class SpecialsCategory(val label: String, val items: List<SpecialsItem>)

internal data class SpecialsItem(val label: String, val text: String) {
	constructor(text: String) : this(text, text)
	constructor(text: Char) : this(text.toString())
	constructor(text: Int) : this(String(intArrayOf(text), 0x0, 0x1))

	companion object {
		fun combining(text: Any) = SpecialsItem("◌$text")
	}
}

@Composable
internal fun SpecialsKey(plane: Plane, modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(Icons.Filled.EmojiSymbols),
		modifier,
	) {
		goToPlane(plane)
	}
}

@Composable
internal fun SpecialsComposable(
	categories: List<SpecialsCategory>,
	modifier: Modifier = Modifier,
	titleSize: Int = 0x40,
	rowCount: Int = 0x4,
	rowSize: Int = 0x40,
) {
	Column(modifier) {
		var selectedCategory by remember { mutableStateOf(categories.first().items) }
		Row(Modifier.weight(1F)) {
			OutlinedKey(
				KeyContent(Icons.AutoMirrored.Filled.ArrowBack),
				Modifier.weight(1F),
			) {
				planeGoBack()
			}
			OutlinedSpace(Modifier.weight(5F)) {
				LazyRow {
					items(categories) { (categoryTitle, category) ->
						OutlinedKey(
							KeyContent(categoryTitle),
							Modifier.width(titleSize.dp),
							movedThreshold = 0F,
						) {
							selectedCategory = category
						}
					}
				}
			}
			BackspaceKey(Modifier.weight(1F))
		}
		OutlinedSpace(Modifier.weight(4F)) {
			LazyRow {
				items(
					selectedCategory.windowed(rowCount, rowCount, true).toList()
				) { row ->
					Column(Modifier.width(rowSize.dp)) {
						row.forEach { (label, text) ->
							OutlinedKey(
								KeyContent(label),
								Modifier.weight(1F),
								movedThreshold = 0F,
							) {
								commitText(text)
							}
						}
						(rowCount - row.size).takeIf { it > 0x0 }?.apply {
							Box(Modifier.weight(toFloat()))
						}
					}
				}
			}
		}
	}
}
