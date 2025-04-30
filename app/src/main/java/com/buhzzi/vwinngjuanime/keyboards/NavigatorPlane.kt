package com.buhzzi.vwinngjuanime.keyboards

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AppShortcut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ControlCamera
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.buhzzi.vwinngjuanime.R
import com.buhzzi.vwinngjuanime.VwinngjuanIms
import com.buhzzi.vwinngjuanime.keyboards.cyrillic.yazhertyPlane
import com.buhzzi.vwinngjuanime.keyboards.editor.clipboardPlane
import com.buhzzi.vwinngjuanime.keyboards.editor.editorPlane
import com.buhzzi.vwinngjuanime.keyboards.kana.kanaPlane
import com.buhzzi.vwinngjuanime.keyboards.latin.qwertyFullwidthPlane
import com.buhzzi.vwinngjuanime.keyboards.latin.qwertyPlane
import com.buhzzi.vwinngjuanime.keyboards.tzuih.vwinngjuanPlane

internal fun VwinngjuanIms.planeGoBack() {
	planeStack.apply {
		if (size > 0x1) {
			removeLastOrNull()!!.onFinishInput(this@planeGoBack)
		}
	}
}

internal fun VwinngjuanIms.goToPlane(plane: Plane) {
	planeStack.apply {
		if (size > 0x1) {
			remove(plane)
			add(plane)
		}
	}
}

internal fun VwinngjuanIms.showImPicker() {
	getSystemService(InputMethodManager::class.java).showInputMethodPicker()
}

internal fun VwinngjuanIms.commitText(text: CharSequence) {
	currentInputConnection?.commitText(text, 0x1)
}

internal fun VwinngjuanIms.backspaceText() {
	sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL)
}

@Composable
internal fun PlaneGoBackKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(stringResource(R.string.navigator_go_back), Icons.AutoMirrored.Filled.ArrowBack),
		modifier,
	) {
		planeGoBack()
	}
}

@Composable
internal fun ActionGoKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(Icons.AutoMirrored.Filled.Send),
		modifier,
	) {
		currentInputConnection.performEditorAction(EditorInfo.IME_ACTION_GO)
	}
}

@Composable
internal fun ActionSearchKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(Icons.Filled.Search),
		modifier,
	) {
		currentInputConnection.performEditorAction(EditorInfo.IME_ACTION_SEARCH)
	}
}

@Composable
internal fun ActionSendKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(Icons.AutoMirrored.Filled.Send),
		modifier,
	) {
		currentInputConnection.performEditorAction(EditorInfo.IME_ACTION_SEND)
	}
}

@Composable
internal fun ActionNextKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(Icons.AutoMirrored.Filled.ArrowForward),
		modifier,
	) {
		currentInputConnection.performEditorAction(EditorInfo.IME_ACTION_NEXT)
	}
}

@Composable
internal fun ActionDoneKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(Icons.Filled.Done),
		modifier,
	) {
		currentInputConnection.performEditorAction(EditorInfo.IME_ACTION_DONE)
	}
}

@Composable
internal fun ActionPreviousKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(Icons.AutoMirrored.Filled.ArrowBack),
		modifier,
	) {
		currentInputConnection.performEditorAction(EditorInfo.IME_ACTION_PREVIOUS)
	}
}

@Composable
internal fun ImPickerKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(stringResource(R.string.navigator_im_picker), Icons.Filled.AppShortcut),
		modifier,
	) {
		showImPicker()
	}
}

@Composable
internal fun NavigatorKey(
	plane: Plane,
	icon: ImageVector?,
	modifier: Modifier = Modifier,
) {
	OutlinedKey(
		KeyContent(plane.name, icon),
		modifier,
	) {
		goToPlane(plane)
	}
}

internal val navigatorPlaneIcon
	get() = Icons.Filled.Navigation

internal val editorPlaneIcon
	get() = Icons.Filled.ControlCamera

internal val clipboardPlaneIcon
	get() = Icons.Filled.ContentPaste

internal val qwertyPlaneIcon
	get() = Icons.Filled.Keyboard

internal val qwertyFullwidthPlaneIcon
	get() = Icons.Filled.Keyboard

internal val vwinngjuanPlaneIcon
	get() = Icons.Filled.Keyboard

internal val kanaPlaneIcon
	get() = Icons.Filled.Keyboard

internal val yazhertyPlaneIcon
	get() = Icons.Filled.Keyboard

internal val navigatorPlane: Plane = Plane({ stringResource(R.string.navigator_plane) }) {
	Column(Modifier) {
		Row(Modifier.weight(3F)) {
			Column(Modifier.weight(1F)) {
				NavigatorKey(qwertyPlane, qwertyPlaneIcon, Modifier.weight(1F))
				NavigatorKey(qwertyFullwidthPlane, qwertyFullwidthPlaneIcon, Modifier.weight(1F))
			}
			Column(Modifier.weight(1F)) {
				NavigatorKey(vwinngjuanPlane, vwinngjuanPlaneIcon, Modifier.weight(1F))
			}
			Column(Modifier.weight(1F)) {
				NavigatorKey(kanaPlane, kanaPlaneIcon, Modifier.weight(1F))
			}
			Column(Modifier.weight(1F)) {
				NavigatorKey(yazhertyPlane, yazhertyPlaneIcon, Modifier.weight(1F))
			}
		}
		Row(Modifier.weight(1F)) {
			NavigatorKey(editorPlane, editorPlaneIcon, Modifier.weight(1F))
			NavigatorKey(clipboardPlane, clipboardPlaneIcon, Modifier.weight(1F))
			ImPickerKey(Modifier.weight(1F))
		}
	}
}
