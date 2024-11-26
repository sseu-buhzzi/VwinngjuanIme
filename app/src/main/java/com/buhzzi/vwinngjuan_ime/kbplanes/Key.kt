package com.buhzzi.vwinngjuan_ime.kbplanes

import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.buhzzi.vwinngjuan_ime.R
import com.buhzzi.vwinngjuan_ime.VwinngjuanIms
import com.buhzzi.vwinngjuan_ime.kit.scVibratorManager

interface RepeatableOnTouchListener : View.OnTouchListener {
	override fun onTouch(v: View, event: MotionEvent): Boolean {

		return v.performClick()
	}
}

abstract class Key(val weight: Float) {
	abstract fun createView(ims: VwinngjuanIms): View
}

class PaddingKey(weight: Float) : Key(weight) {
	override fun createView(ims: VwinngjuanIms) = View(ims)
}

open class LabelKey(weight: Float, protected val label: CharSequence) : Key(weight) {
	override fun createView(ims: VwinngjuanIms) = TextView(ims).apply {
		text = label
		setTextColor(ims.getColor(R.color.colorForeground))
		gravity = Gravity.CENTER
		isClickable = true
		setBackgroundResource(R.drawable.ripple_normal)
	}
}

open class ImageKey(weight: Float, private val imageResource: Int) : Key(weight) {
	override fun createView(ims: VwinngjuanIms) = ImageView(ims).apply {
		setImageResource(imageResource)
		scaleType = ImageView.ScaleType.CENTER
		isClickable = true
		setBackgroundResource(R.drawable.ripple_normal)
	}
}

private interface RepeatableKey {
	fun act(ims: VwinngjuanIms)

	fun setOnTouchListener(view: View) {
		view.setOnTouchListener { v, event ->
			v.performClick()
			val ims = v.context as VwinngjuanIms
			when (event.action.and(MotionEvent.ACTION_MASK)) {
				MotionEvent.ACTION_DOWN -> {
					ims.scVibratorManager?.defaultVibrator?.vibrate(
						VibrationEffect.createOneShot(64, VibrationEffect.DEFAULT_AMPLITUDE)
					)
					act(ims)
					true
				}
				else -> false
			}
		}
	}
}

class LiteralLabelKey(weight: Float, label: CharSequence) :
	LabelKey(weight, label),
	RepeatableKey
{
	override fun createView(ims: VwinngjuanIms) = super.createView(ims).apply {
		setOnTouchListener(this)
	}
	override fun act(ims: VwinngjuanIms) {
		ims.currentInputConnection?.commitText(label, label.length)
	}
}

class LiteralImageKey(weight: Float, imageResource: Int, private val text: CharSequence) :
	ImageKey(weight, imageResource),
	RepeatableKey
{
	override fun createView(ims: VwinngjuanIms) = super.createView(ims).apply {
		setOnTouchListener(this)
	}
	override fun act(ims: VwinngjuanIms) {
		ims.currentInputConnection?.commitText(text, text.length)
	}
}

class BackspaceKey(weight: Float) :
	ImageKey(weight, R.drawable.material_keyboard_backspace),
	RepeatableKey
{
	override fun createView(ims: VwinngjuanIms) = super.createView(ims).apply {
		setOnTouchListener(this)
	}
	override fun act(ims: VwinngjuanIms) {
		ims.currentInputConnection?.apply {
			getSelectedText(0)?.let { commitText("", 0) }
				?: deleteSurroundingText(1, 0)
		}
	}
}

class ShiftKey(weight: Float, shift: Boolean, private val dstPlaneClass: Class<out Plane>) :
	ImageKey(weight,
		if (shift) R.drawable.material_shift_fill1
		else R.drawable.material_shift
	),
	RepeatableKey
{
	override fun createView(ims: VwinngjuanIms) = super.createView(ims).apply {
		setOnTouchListener(this)
	}
	override fun act(ims: VwinngjuanIms) {
		ims.activatePlane = dstPlaneClass
	}
}

val qwertyKeyboard: Array<Array<Key>> = arrayOf(
	arrayOf(
		LiteralImageKey(1F, R.drawable.material_keyboard_tab, "\t"),
		LiteralLabelKey(1F, "`"),
		LiteralLabelKey(1F, "'"),
		LiteralLabelKey(1F, "["),
		LiteralLabelKey(1F, "]"),
		LiteralLabelKey(1F, "\\"),
		LiteralLabelKey(1F, "-"),
		LiteralLabelKey(1F, "="),
		BackspaceKey(2F)
	),
	arrayOf(
		LiteralLabelKey(1F, "1"),
		LiteralLabelKey(1F, "2"),
		LiteralLabelKey(1F, "3"),
		LiteralLabelKey(1F, "4"),
		LiteralLabelKey(1F, "5"),
		LiteralLabelKey(1F, "6"),
		LiteralLabelKey(1F, "7"),
		LiteralLabelKey(1F, "8"),
		LiteralLabelKey(1F, "9"),
		LiteralLabelKey(1F, "0")
	),
	arrayOf(
		LiteralLabelKey(1F, "q"),
		LiteralLabelKey(1F, "w"),
		LiteralLabelKey(1F, "e"),
		LiteralLabelKey(1F, "r"),
		LiteralLabelKey(1F, "t"),
		LiteralLabelKey(1F, "y"),
		LiteralLabelKey(1F, "u"),
		LiteralLabelKey(1F, "i"),
		LiteralLabelKey(1F, "o"),
		LiteralLabelKey(1F, "p")
	),
	arrayOf(
		LiteralLabelKey(1F, "a"),
		LiteralLabelKey(1F, "s"),
		LiteralLabelKey(1F, "d"),
		LiteralLabelKey(1F, "f"),
		LiteralLabelKey(1F, "g"),
		LiteralLabelKey(1F, "h"),
		LiteralLabelKey(1F, "j"),
		LiteralLabelKey(1F, "k"),
		LiteralLabelKey(1F, "l"),
		LiteralLabelKey(1F, ";")
	),
	arrayOf(
		LiteralLabelKey(1F, "/"),
		LiteralLabelKey(1F, "z"),
		LiteralLabelKey(1F, "x"),
		LiteralLabelKey(1F, "c"),
		LiteralLabelKey(1F, "v"),
		LiteralLabelKey(1F, "b"),
		LiteralLabelKey(1F, "n"),
		LiteralLabelKey(1F, "m"),
		LiteralLabelKey(1F, ","),
		LiteralLabelKey(1F, ".")
	),
	arrayOf(
		ShiftKey(2F, false, LatinShiftPlane::class.java),
		LiteralImageKey(6F, R.drawable.material_space_bar, " "),
		LiteralImageKey(2F, R.drawable.material_keyboard_return, "\n")
	)
)

val qwertyShiftKeyboard: Array<Array<Key>> = arrayOf(
	arrayOf(
		LiteralImageKey(1F, R.drawable.material_keyboard_tab, "\t"),
		LiteralLabelKey(1F, "~"),
		LiteralLabelKey(1F, "\""),
		LiteralLabelKey(1F, "{"),
		LiteralLabelKey(1F, "}"),
		LiteralLabelKey(1F, "|"),
		LiteralLabelKey(1F, "_"),
		LiteralLabelKey(1F, "+"),
		BackspaceKey(2F)
	),
	arrayOf(
		LiteralLabelKey(1F, "!"),
		LiteralLabelKey(1F, "@"),
		LiteralLabelKey(1F, "#"),
		LiteralLabelKey(1F, "$"),
		LiteralLabelKey(1F, "%"),
		LiteralLabelKey(1F, "^"),
		LiteralLabelKey(1F, "&"),
		LiteralLabelKey(1F, "*"),
		LiteralLabelKey(1F, "("),
		LiteralLabelKey(1F, ")")
	),
	arrayOf(
		LiteralLabelKey(1F, "Q"),
		LiteralLabelKey(1F, "W"),
		LiteralLabelKey(1F, "E"),
		LiteralLabelKey(1F, "R"),
		LiteralLabelKey(1F, "T"),
		LiteralLabelKey(1F, "Y"),
		LiteralLabelKey(1F, "U"),
		LiteralLabelKey(1F, "I"),
		LiteralLabelKey(1F, "O"),
		LiteralLabelKey(1F, "P")
	),
	arrayOf(
		LiteralLabelKey(1F, "A"),
		LiteralLabelKey(1F, "S"),
		LiteralLabelKey(1F, "D"),
		LiteralLabelKey(1F, "F"),
		LiteralLabelKey(1F, "G"),
		LiteralLabelKey(1F, "H"),
		LiteralLabelKey(1F, "J"),
		LiteralLabelKey(1F, "K"),
		LiteralLabelKey(1F, "L"),
		LiteralLabelKey(1F, ":")
	),
	arrayOf(
		LiteralLabelKey(1F, "?"),
		LiteralLabelKey(1F, "Z"),
		LiteralLabelKey(1F, "X"),
		LiteralLabelKey(1F, "C"),
		LiteralLabelKey(1F, "V"),
		LiteralLabelKey(1F, "B"),
		LiteralLabelKey(1F, "N"),
		LiteralLabelKey(1F, "M"),
		LiteralLabelKey(1F, "<"),
		LiteralLabelKey(1F, ">")
	),
	arrayOf(
		ShiftKey(2F, true, LatinPlane::class.java),
		LiteralImageKey(6F, R.drawable.material_space_bar, " "),
		LiteralImageKey(2F, R.drawable.material_keyboard_return, "\n")
	)
)
