package com.buhzzi.vwinngjuanime

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.inputmethodservice.InputMethodService
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.CallSuper
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.buhzzi.vwinngjuanime.keyboards.editor.clipboardPlane
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

		instance = this
	}

	override fun onDestroy() {
		super.onDestroy()

		while (localBroadcastReceivers.isNotEmpty()) {
			unregisterContextBroadcastReceiver(localBroadcastReceivers.last())
		}
		if (this === instance) {
			instance = null
		}
	}

	override fun onCreateInputComposeView() = ComposeView(this).apply {
		setContent {
			ThemeWrapComposable {
				Box(Modifier.height(0x180.dp)
					.safeDrawingPadding(),
				) {
					currentPlane.composableFunction()
				}
			}
		}

		layoutParams = ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT,
		)
	}

	override fun onStartInputView(attribute: EditorInfo?, restarting: Boolean) {
		super.onStartInputView(attribute, restarting)

		inputType = attribute?.inputType ?: EditorInfo.TYPE_NULL
		imeOptions = attribute?.imeOptions ?: EditorInfo.IME_NULL
		// TODO Debug.
		println("inputType: ${inputType.toString(0x10)}\timeOptions: ${imeOptions.toString(0x10)}")
	}

	override fun onFinishInput() {
		super.onFinishInput()

		currentPlane.onFinishInput(this)
	}

	override fun onWindowHidden() {
		super.onWindowHidden()

		currentPlane.onWindowHidden(this)
	}

	var planeStack = mutableStateListOf(navigatorPlane, clipboardPlane, qwertyPlane, vwinngjuanPlane)

	val currentPlane
		get() = planeStack.last()

	var inputType by mutableIntStateOf(EditorInfo.TYPE_NULL)

	var imeOptions by mutableIntStateOf(EditorInfo.IME_NULL)

	private val localBroadcastReceivers = mutableListOf<BroadcastReceiver>()

	fun registerContextBroadcastReceiver(receiver: BroadcastReceiver, filter: IntentFilter) {
		if (receiver !in localBroadcastReceivers) {
			LocalBroadcastManager.getInstance(this)
				.registerReceiver(receiver, filter)
			localBroadcastReceivers.add(receiver)
		}
	}

	fun unregisterContextBroadcastReceiver(receiver: BroadcastReceiver) {
		if (localBroadcastReceivers.remove(receiver)) {
			LocalBroadcastManager.getInstance(this)
				.unregisterReceiver(receiver)
		}
	}

	companion object {
		var instance: VwinngjuanIms? = null
			private set

		val instanceMust
			get() = instance ?: error("${VwinngjuanIms::class.qualifiedName} instance is not created.")
	}
}
