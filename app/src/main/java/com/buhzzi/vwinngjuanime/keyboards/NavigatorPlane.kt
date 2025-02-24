package com.buhzzi.vwinngjuanime.keyboards

import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
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
import com.buhzzi.vwinngjuanime.keyboards.editor.clipboardPlane
import com.buhzzi.vwinngjuanime.keyboards.editor.editorPlane
import com.buhzzi.vwinngjuanime.keyboards.kana.kanaPlane
import com.buhzzi.vwinngjuanime.keyboards.latin.qwertyPlane
import com.buhzzi.vwinngjuanime.keyboards.tzuih.vwinngjuanPlane

internal fun VwinngjuanIms.planeGoBack() {
	planeStack.takeIf { it.size > 1 }?.removeLastOrNull()
}

internal fun VwinngjuanIms.goToPlane(plane: Plane) {
	planeStack.takeIf { it.size > 1 }?.apply {
		remove(plane)
		add(plane)
	}
}

internal fun VwinngjuanIms.showImPicker() {
	getSystemService(InputMethodManager::class.java).showInputMethodPicker()
}

internal fun VwinngjuanIms.commitText(text: CharSequence) {
	currentInputConnection?.commitText(text, 1)
}

internal fun VwinngjuanIms.backspaceText() {
	currentInputConnection?.apply {
		getSelectedText(0)?.also { commitText("", 0) }
			?: deleteSurroundingText(1, 0)
	}
}

@Composable
internal fun PlaneGoBackKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent(stringResource(R.string.navigator_go_back), Icons.AutoMirrored.Filled.ArrowBackIos),
		modifier,
	) {
		planeGoBack()
	}
}

@Composable
internal fun ActionGoKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent(Icons.AutoMirrored.Filled.Send),
		modifier,
	) {
		currentInputConnection.performEditorAction(EditorInfo.IME_ACTION_GO)
	}
}

@Composable
internal fun ActionSearchKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent(Icons.Filled.Search),
		modifier,
	) {
		currentInputConnection.performEditorAction(EditorInfo.IME_ACTION_SEARCH)
	}
}

@Composable
internal fun ActionSendKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent(Icons.AutoMirrored.Filled.Send),
		modifier,
	) {
		currentInputConnection.performEditorAction(EditorInfo.IME_ACTION_SEND)
	}
}

@Composable
internal fun ActionNextKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent(Icons.AutoMirrored.Filled.ArrowForward),
		modifier,
	) {
		currentInputConnection.performEditorAction(EditorInfo.IME_ACTION_NEXT)
	}
}

@Composable
internal fun ActionDoneKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent(Icons.Filled.Done),
		modifier,
	) {
		currentInputConnection.performEditorAction(EditorInfo.IME_ACTION_DONE)
	}
}

@Composable
internal fun ActionPreviousKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent(Icons.AutoMirrored.Filled.ArrowBack),
		modifier,
	) {
		currentInputConnection.performEditorAction(EditorInfo.IME_ACTION_PREVIOUS)
	}
}

@Composable
internal fun ImPickerKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent(stringResource(R.string.navigator_im_picker), Icons.Filled.AppShortcut),
		modifier,
	) {
		showImPicker()
	}
}

@Composable
internal fun NavigatorKey(
	ims: VwinngjuanIms,
	plane: Plane,
	icon: ImageVector?,
	modifier: Modifier = Modifier,
) {
	OutlinedKey(
		ims,
		KeyContent(plane.name, icon),
		modifier,
	) {
		goToPlane(plane)
	}
}

@Composable
internal fun NavigatorPlaneNavigatorKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	NavigatorKey(ims, navigatorPlane, Icons.Filled.Navigation, modifier)
}

@Composable
internal fun EditorPlaneNavigatorKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	NavigatorKey(ims, editorPlane, Icons.Filled.ControlCamera, modifier)
}

@Composable
internal fun ClipboardPlaneNavigatorKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	NavigatorKey(ims, clipboardPlane, Icons.Filled.ContentPaste, modifier)
}

@Composable
internal fun QwertyPlaneNavigatorKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	NavigatorKey(ims, qwertyPlane, Icons.Filled.Keyboard, modifier)
}

@Composable
internal fun VwinngjuanPlaneNavigatorKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	NavigatorKey(ims, vwinngjuanPlane, Icons.Filled.Keyboard, modifier)
}

@Composable
internal fun KanaPlaneNavigatorKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	NavigatorKey(ims, kanaPlane, Icons.Filled.Keyboard, modifier)
}

internal val navigatorPlane = Plane({ stringResource(R.string.navigator_plane) }) { ims ->
	Column(Modifier) {
		Row(Modifier.weight(1F)) {
			QwertyPlaneNavigatorKey(ims, Modifier.weight(1F))
			VwinngjuanPlaneNavigatorKey(ims, Modifier.weight(1F))
			KanaPlaneNavigatorKey(ims, Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			EditorPlaneNavigatorKey(ims, Modifier.weight(1F))
			ClipboardPlaneNavigatorKey(ims, Modifier.weight(1F))
			ImPickerKey(ims, Modifier.weight(1F))
		}
	}
}
