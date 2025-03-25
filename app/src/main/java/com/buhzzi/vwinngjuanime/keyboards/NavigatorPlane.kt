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
	planeStack.apply {
		if (size > 0x1) {
			removeLastOrNull()!!.finish(this@planeGoBack)
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
	currentInputConnection?.apply {
		getSelectedText(0x0)?.also { commitText("", 0x0) }
			?: deleteSurroundingText(0x1, 0x0)
	}
}

@Composable
internal fun PlaneGoBackKey(modifier: Modifier = Modifier) {
	OutlinedKey(
		KeyContent(stringResource(R.string.navigator_go_back), Icons.AutoMirrored.Filled.ArrowBackIos),
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

@Composable
internal fun NavigatorPlaneNavigatorKey(modifier: Modifier = Modifier) {
	NavigatorKey(navigatorPlane, Icons.Filled.Navigation, modifier)
}

@Composable
internal fun EditorPlaneNavigatorKey(modifier: Modifier = Modifier) {
	NavigatorKey(editorPlane, Icons.Filled.ControlCamera, modifier)
}

@Composable
internal fun ClipboardPlaneNavigatorKey(modifier: Modifier = Modifier) {
	NavigatorKey(clipboardPlane, Icons.Filled.ContentPaste, modifier)
}

@Composable
internal fun QwertyPlaneNavigatorKey(modifier: Modifier = Modifier) {
	NavigatorKey(qwertyPlane, Icons.Filled.Keyboard, modifier)
}

@Composable
internal fun VwinngjuanPlaneNavigatorKey(modifier: Modifier = Modifier) {
	NavigatorKey(vwinngjuanPlane, Icons.Filled.Keyboard, modifier)
}

@Composable
internal fun KanaPlaneNavigatorKey(modifier: Modifier = Modifier) {
	NavigatorKey(kanaPlane, Icons.Filled.Keyboard, modifier)
}

internal val navigatorPlane = Plane({ stringResource(R.string.navigator_plane) }) {
	Column(Modifier) {
		Row(Modifier.weight(1F)) {
			QwertyPlaneNavigatorKey(Modifier.weight(1F))
			VwinngjuanPlaneNavigatorKey(Modifier.weight(1F))
			KanaPlaneNavigatorKey(Modifier.weight(1F))
		}
		Row(Modifier.weight(1F)) {
			EditorPlaneNavigatorKey(Modifier.weight(1F))
			ClipboardPlaneNavigatorKey(Modifier.weight(1F))
			ImPickerKey(Modifier.weight(1F))
		}
	}
}
