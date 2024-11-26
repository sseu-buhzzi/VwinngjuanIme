package com.buhzzi.vwinngjuan_ime.kit

import android.graphics.Color

class ColorKit {
	companion object {
		fun hsv(a: Int, h: Float, s: Float, v: Float) = Color.HSVToColor(a, floatArrayOf(h, s, v))
	}
}
