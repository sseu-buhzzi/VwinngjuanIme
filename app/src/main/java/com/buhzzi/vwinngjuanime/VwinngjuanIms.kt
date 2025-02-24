package com.buhzzi.vwinngjuanime

import android.content.Intent
import android.inputmethodservice.InputMethodService
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.inputmethod.EditorInfo
import androidx.annotation.CallSuper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.buhzzi.vwinngjuanime.keyboards.OutlinedSpace
import com.buhzzi.vwinngjuanime.keyboards.latin.qwertyPlane
import com.buhzzi.vwinngjuanime.keyboards.navigatorPlane
import com.buhzzi.vwinngjuanime.keyboards.tzuih.vwinngjuanPlane

internal abstract class ComposeInputMethodService : InputMethodService(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {
	private val lifecycleDispatcher = ServiceLifecycleDispatcher(this)
	override val lifecycle
		get() = lifecycleDispatcher.lifecycle

	override val viewModelStore
		get() = ViewModelStore()

	private val savedStateRegistryController = SavedStateRegistryController.create(this)
	override val savedStateRegistry
		get() = savedStateRegistryController.savedStateRegistry

	@CallSuper
	override fun onCreate() {
		lifecycleDispatcher.onServicePreSuperOnCreate()
		super.onCreate()
		savedStateRegistryController.performRestore(null)
	}

	@CallSuper
	override fun onBindInput() {
		lifecycleDispatcher.onServicePreSuperOnBind()
		super.onBindInput()
	}

	@CallSuper
	override fun onCreateInputView() = onCreateInputComposeView().apply {
		setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(lifecycle))

		window.window?.decorView?.apply {
			setViewTreeLifecycleOwner(this@ComposeInputMethodService)
			setViewTreeViewModelStoreOwner(this@ComposeInputMethodService)
			setViewTreeSavedStateRegistryOwner(this@ComposeInputMethodService)
		}
	}

	abstract fun onCreateInputComposeView(): AbstractComposeView

	@CallSuper
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = super.onStartCommand(intent, flags, startId)

	@CallSuper
	override fun onDestroy() {
		lifecycleDispatcher.onServicePreSuperOnDestroy()
		super.onDestroy()
	}
}

internal class VwinngjuanIms : ComposeInputMethodService() {
	override fun onCreate() {
		super.onCreate()
	}

	override fun onCreateInputComposeView() = ComposeView(this).apply {
		setContent {
			ThemeWrapComposable {
				Column(Modifier.height(0x140.dp)) {
//					item {
					Box(Modifier.safeDrawingPadding()) {
						planeStack.last().composableFunction(this@VwinngjuanIms)
					}
//						Text("0\n1\n2\n3\n4\n5\n6\n7\n8\n9\na\nb\nc\nd\ne\nf\n", Modifier.background(Color.Gray).fillMaxWidth())
//					}
//					OutlinedSpace(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)) { }
				}
			}
		}
	}

	override fun onCreateCandidatesView() = run {
		super.onCreateCandidatesView()
	}

	override fun onStartInputView(attribute: EditorInfo?, restarting: Boolean) {
		super.onStartInputView(attribute, restarting)

		inputType = attribute?.inputType ?: EditorInfo.TYPE_NULL
		imeOptions = attribute?.imeOptions ?: EditorInfo.IME_NULL
		println("inputType: ${inputType.toString(0x10)}\timeOptions: ${imeOptions.toString(0x10)}")
//		TODO 微信Bug
//		val BRAND = "微信"
//		val text = "請始終保持${BRAND}在瑩幕顯示"
//		window.window?.isNavigationBarContrastEnforced = false
	}

	override fun onFinishInput() {
		super.onFinishInput()
	}

	override fun onDestroy() {
		super.onDestroy()
	}

	var planeStack = mutableStateListOf(navigatorPlane, vwinngjuanPlane)

	var inputType by mutableIntStateOf(EditorInfo.TYPE_NULL)
	var imeOptions by mutableIntStateOf(EditorInfo.IME_NULL)
}
