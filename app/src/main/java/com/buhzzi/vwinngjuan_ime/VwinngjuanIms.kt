package com.buhzzi.vwinngjuan_ime

import android.annotation.SuppressLint
import android.content.ComponentName
import android.inputmethodservice.InputMethodService
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodInfo
import android.view.inputmethod.InputMethodSubtype
import android.widget.FrameLayout
import androidx.preference.PreferenceManager
import com.buhzzi.vwinngjuan_ime.kbplanes.LatinPlane
import com.buhzzi.vwinngjuan_ime.kbplanes.LatinShiftPlane
import com.buhzzi.vwinngjuan_ime.kbplanes.Plane
import com.buhzzi.vwinngjuan_ime.kit.scInputMethodManager

class VwinngjuanIms : InputMethodService() {
	private lateinit var planeFrameLayout: FrameLayout

	private lateinit var planes: Array<Plane>
	var activatePlane: Class<out Plane> = LatinPlane::class.java
		set(plane) {
			field = plane
			planes.forEach { it.layout.visibility =
				if (field.isInstance(it)) ViewGroup.VISIBLE
				else ViewGroup.GONE
			}
		}
	val inputMethodInfo: InputMethodInfo?
		get() {
			val componentName = ComponentName(packageName, VwinngjuanIms::class.java.name)
			return scInputMethodManager?.enabledInputMethodList?.find { it.component == componentName }
		}
	val enabledInputMethodSubtypeList
		get() = scInputMethodManager?.getEnabledInputMethodSubtypeList(inputMethodInfo, true)
			as? List<InputMethodSubtype>

	override fun onCreate() {
		super.onCreate()
	}

	@SuppressLint("InflateParams")
	override fun onCreateInputView(): View {
		val inputRoot = FrameLayout(this)
		planeFrameLayout = FrameLayout(this).apply {
			inputRoot.addView(this)
		}
		planes = arrayOf(
			LatinPlane(this),
			LatinShiftPlane(this)
		)
		for (plane in planes) {
			planeFrameLayout.addView(plane.layout, FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
			))
		}
		activatePlane = LatinPlane::class.java
		return inputRoot
	}

	override fun onCreateCandidatesView(): View? {
		return super.onCreateCandidatesView()
	}

	override fun onCreateExtractTextView(): View? {
		return super.onCreateExtractTextView()
	}

	override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
		super.onStartInput(attribute, restarting)
	}

	override fun onStartInputView(editorInfo: EditorInfo?, restarting: Boolean) {
		super.onStartInputView(editorInfo, restarting)
		planeFrameLayout.run { layoutParams = layoutParams.apply {
			height = PreferenceManager.getDefaultSharedPreferences(this@VwinngjuanIms)
				.getString(getString(R.string.kb_height_key), null)?.toIntOrNull() ?: 0x400
		} }
	}

	override fun onDestroy() {
		super.onDestroy()
	}
}
