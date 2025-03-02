package com.buhzzi.vwinngjuanime.keyboards.editor

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Base64
import android.view.inputmethod.InputConnection
import androidx.annotation.IntRange
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardDoubleArrowLeft
import androidx.compose.material.icons.filled.KeyboardDoubleArrowRight
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.buhzzi.vwinngjuanime.R
import com.buhzzi.vwinngjuanime.VwinngjuanIms
import com.buhzzi.vwinngjuanime.fromBytes
import com.buhzzi.vwinngjuanime.keyboards.ClipboardPlaneNavigatorKey
import com.buhzzi.vwinngjuanime.keyboards.EditorPlaneNavigatorKey
import com.buhzzi.vwinngjuanime.keyboards.KeyContent
import com.buhzzi.vwinngjuanime.keyboards.NavigatorPlaneNavigatorKey
import com.buhzzi.vwinngjuanime.keyboards.OutlinedClickable
import com.buhzzi.vwinngjuanime.keyboards.OutlinedKey
import com.buhzzi.vwinngjuanime.keyboards.OutlinedSpace
import com.buhzzi.vwinngjuanime.keyboards.Plane
import com.buhzzi.vwinngjuanime.keyboards.PlaneGoBackKey
import com.buhzzi.vwinngjuanime.keyboards.backspaceText
import com.buhzzi.vwinngjuanime.keyboards.commitText
import com.buhzzi.vwinngjuanime.keyboards.planeGoBack
import com.buhzzi.vwinngjuanime.toBytes
import java.math.BigInteger

private inline fun InputConnection.doWithCarets(block: InputConnection.(Int, Int) -> Unit) {
	getTextBeforeCursor(Int.MAX_VALUE, 0)?.length?.also { leftLength ->
		val midLength = getSelectedText(0)?.length ?: 0
		block(leftLength, leftLength + midLength)
	}
}

private fun InputConnection.applyCarets(@IntRange(-1, 1) carets: Int, block: (Pair<Int, Int>) -> Pair<Int, Int>) {
	doWithCarets { left, right ->
		val (newLeft, newRight) = block(left to right)
		when (carets) {
			-1 -> setSelection(newLeft, right)
			0 -> setSelection(newLeft, newRight)
			1 -> setSelection(left, newRight)
			else -> error("Invalid carets value: $carets.")
		}
	}
}

@Composable
private fun LeftKey(
	ims: VwinngjuanIms,
	modifier: Modifier = Modifier,
	@IntRange(-1, 1) carets: Int = 0,
) {
	OutlinedKey(
		ims,
		KeyContent(Icons.AutoMirrored.Filled.KeyboardArrowLeft),
		modifier,
	) {
		currentInputConnection?.applyCarets(carets) { (left, right) -> left - 1 to right - 1 }
	}
}

@Composable
private fun RightKey(
	ims: VwinngjuanIms,
	modifier: Modifier = Modifier,
	@IntRange(-1, 1) carets: Int = 0,
) {
	OutlinedKey(
		ims,
		KeyContent(Icons.AutoMirrored.Filled.KeyboardArrowRight),
		modifier,
	) {
		currentInputConnection?.apply {
			applyCarets(carets) { (left, right) -> left + 1 to right + 1 }
		}
	}
}

@Composable
private fun LeftMostKey(
	ims: VwinngjuanIms,
	modifier: Modifier = Modifier,
	@IntRange(-1, 1) carets: Int = 0,
) {
	OutlinedKey(
		ims,
		KeyContent(Icons.Filled.KeyboardDoubleArrowLeft),
		modifier,
	) {
		currentInputConnection?.applyCarets(carets) { 0 to 0 }
	}
}

@Composable
private fun RightMostKey(
	ims: VwinngjuanIms,
	modifier: Modifier = Modifier,
	@IntRange(-1, 1) carets: Int = 0,
) {
	OutlinedKey(
		ims,
		KeyContent(Icons.Filled.KeyboardDoubleArrowRight),
		modifier,
	) {
		currentInputConnection?.applyCarets(carets) { Int.MAX_VALUE to Int.MAX_VALUE }
	}
}

@Composable
private fun SelectAllKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent("Select All", Icons.Filled.SelectAll),
		modifier,
	) {
		currentInputConnection?.setSelection(0, Int.MAX_VALUE)
	}
}

@Composable
private fun CutKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent("Cut", Icons.Filled.ContentCut),
		modifier,
	) {
		currentInputConnection?.apply {
			addPlainTextClipData(getSelectedText(0))
			commitText("", 0)
		}
	}
}

@Composable
private fun CopyKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent("Copy", Icons.Filled.ContentCopy),
		modifier,
	) {
		currentInputConnection?.apply {
			addPlainTextClipData(getSelectedText(0))
		}
	}
}

@Composable
private fun PasteKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent("Paste", Icons.Filled.ContentPaste),
		modifier,
	) {
		getSystemService(ClipboardManager::class.java).primaryClip?.run {
			List(itemCount) { i -> getItemAt(i).text.takeIf { it.isNotEmpty() } }
				.asSequence()
				.filterNotNull()
				.joinToString("")
				.also { commitText(it) }
		}
	}
}

@Composable
private fun BackspaceKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent("Backspace", Icons.AutoMirrored.Filled.Backspace),
		modifier,
	) {
		backspaceText()
	}
}

internal val editorPlane: Plane = Plane({ stringResource(R.string.editor_plane) }) { ims ->
	Column {
		Row(Modifier.weight(1F)) {
			PlaneGoBackKey(ims, Modifier.weight(1F))
			ClipboardPlaneNavigatorKey(ims, Modifier.weight(1F))
			NavigatorPlaneNavigatorKey(ims, Modifier.weight(1F))
		}
		Row(Modifier.weight(2F)) {
			Column(Modifier.weight(2F)) {
				LeftKey(ims, Modifier.weight(2F))
				LeftMostKey(ims, Modifier.weight(1F))
			}
			Column(Modifier.weight(1F)) {
				LeftKey(ims, Modifier.weight(1F), -1)
				LeftMostKey(ims, Modifier.weight(1F), -1)
			}
			Column(Modifier.weight(1F)) {
				RightKey(ims, Modifier.weight(1F), -1)
				RightMostKey(ims, Modifier.weight(1F), -1)
			}
			Column(Modifier.weight(1F)) {
				LeftKey(ims, Modifier.weight(1F), 1)
				LeftMostKey(ims, Modifier.weight(1F), 1)
			}
			Column(Modifier.weight(1F)) {
				RightKey(ims, Modifier.weight(1F), 1)
				RightMostKey(ims, Modifier.weight(1F), 1)
			}
			Column(Modifier.weight(2F)) {
				RightKey(ims, Modifier.weight(2F))
				RightMostKey(ims, Modifier.weight(1F))
			}
		}
		Row(Modifier.weight(1F)) {
			SelectAllKey(ims, Modifier.weight(1F))
			CutKey(ims, Modifier.weight(1F))
			CopyKey(ims, Modifier.weight(1F))
			PasteKey(ims, Modifier.weight(1F))
			BackspaceKey(ims, Modifier.weight(1F))
		}
	}
}

const val CLIP_DATA_ROW_HEIGHT = 0x40

internal fun VwinngjuanIms.addPlainTextClipData(text: CharSequence?) {
	text.isNullOrEmpty() && return
	getSystemService(ClipboardManager::class.java)
		.setPrimaryClip(ClipData.newPlainText("plain text", text))
}

internal fun VwinngjuanIms.writeClipPreferences(clips: List<ClipData?>) {
	getSharedPreferences("clip", Context.MODE_PRIVATE).edit {
		putInt("size", clips.size)
		clips.forEachIndexed { i, clip ->
			clip?.toBytes()
				?.let { Base64.encodeToString(it, 0) }
				?.let { putString("$i", it) }
		}
	}
}

internal fun VwinngjuanIms.readClipPreferences() =
	getSharedPreferences("clip", Context.MODE_PRIVATE).run {
		List(getInt("size", 0)) { i ->
			getString("$i", null)
				?.let { Base64.decode(it, Base64.DEFAULT) }
				?.let { ClipData.CREATOR.fromBytes(it) }
		}
	}

internal fun ClipData.hashParcelled() =
	BigInteger(toBytes()).hashCode()

@Composable
internal inline fun ClipDataRow(
	ims: VwinngjuanIms,
	text: String,
	crossinline select: () -> Unit,
) {
	Row(Modifier.height(CLIP_DATA_ROW_HEIGHT.dp)) {
		val optionModifier = Modifier
			.width(CLIP_DATA_ROW_HEIGHT.dp)

		OutlinedKey(
			ims,
			KeyContent(Icons.Filled.Info),
			optionModifier,
		) {
			select()
		}
		OutlinedClickable(ims, { commitText(text) }, Modifier.weight(1F),
			movedThreshold = 0F
		) {
			Box(Modifier.fillMaxSize(), Alignment.CenterStart) {
				Text(text,
					overflow = TextOverflow.Ellipsis,
				)
			}
		}
	}
}

@Composable
internal inline fun SelectedClipDataRow(
	ims: VwinngjuanIms,
	clip: ClipData,
	clipIndex: Int,
	itemIndex: Int,
	crossinline deleteItem: () -> Unit,
	crossinline goBack: () -> Unit,
) {
	Row(Modifier.height(CLIP_DATA_ROW_HEIGHT.dp)) {
		val optionModifier = Modifier
			.width(CLIP_DATA_ROW_HEIGHT.dp)
		OutlinedKey(
			ims,
			KeyContent(Icons.Filled.ArrowBackIosNew),
			optionModifier,
		) {
			goBack()
		}
		OutlinedKey(
			ims,
			KeyContent(Icons.Filled.DeleteForever),
			optionModifier,
		) {
			deleteItem()
		}
		OutlinedSpace(Modifier.weight(1F)) {
			Box(Modifier.fillMaxSize(), Alignment.Center) {
				Text("$clipIndex $clip\n$itemIndex ${clip.getItemAt(itemIndex)}", Modifier
					.horizontalScroll(rememberScrollState()),
				)
			}
		}
	}
}

internal val clipboardPlane = Plane({ stringResource(R.string.clipboard_plane) }) { ims ->
	Column {
		Row(Modifier.weight(1F)) {
			PlaneGoBackKey(ims, Modifier.weight(1F))
			EditorPlaneNavigatorKey(ims, Modifier.weight(1F))
			NavigatorPlaneNavigatorKey(ims, Modifier.weight(1F))
		}

		val clipboardManager = remember { ims.getSystemService(ClipboardManager::class.java) }
		val clips = remember { ims.readClipPreferences().toMutableStateList() }
		val clipHashes = remember { clips.asSequence().map { it?.hashParcelled() }.toMutableSet() }
		val selectedClipOrders = remember { mutableStateMapOf<Int, Unit>() }
		fun updateClips() {
			selectedClipOrders.clear()
			ims.writeClipPreferences(clips)
		}
		LaunchedEffect(Unit) {
			clipboardManager.addPrimaryClipChangedListener {
				clipboardManager.primaryClip?.apply {
					clipHashes.add(hashParcelled()) && clips.add(this)
					updateClips()
				}
			}
		}

		OutlinedSpace(Modifier.weight(3F)) {
			val clipTextItems = clips.asSequence().withIndex().flatMap { (clipIndex, clip) ->
				sequence {
					clip?.run {
						for (i in 0 ..< itemCount) {
							getItemAt(i).text?.let { yield(Triple(this, clipIndex, i)) }
						}
					}
				}
			}.withIndex().toList().asReversed()
			LazyColumn {
				items(clipTextItems) { (itemOrder, itemTriple) ->
					val (clip, clipIndex, itemIndex) = itemTriple
					if (itemOrder in selectedClipOrders) {
						SelectedClipDataRow(ims, clip, clipIndex, itemIndex, {
							clip.run { List(itemCount) { i ->
								takeIf { i != itemIndex }?.getItemAt(i)
							} }
								.asSequence().filterNotNull()
								.iterator()
								.apply {
									if (hasNext()) {
										clips[clipIndex] = ClipData(clip.description, next()).apply {
											forEachRemaining { addItem(it) }
										}
									} else {
										clipHashes.remove(clip.hashParcelled())
										clips.removeAt(clipIndex)
									}
								}
							updateClips()
						}) { selectedClipOrders.remove(itemOrder) }
					} else {
						ClipDataRow(ims, clip.getItemAt(itemIndex).text.toString()) { selectedClipOrders[itemOrder] = Unit }
					}
				}
			}
			if (clipTextItems.isEmpty()) OutlinedKey(
				ims,
				KeyContent("No clip", Icons.Filled.Close),
			) {
				planeGoBack()
			}
		}
	}
}
