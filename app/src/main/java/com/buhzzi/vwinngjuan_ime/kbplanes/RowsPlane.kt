package com.buhzzi.vwinngjuan_ime.kbplanes

import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.buhzzi.vwinngjuan_ime.VwinngjuanIms

open class RowsPlane(ims: VwinngjuanIms, keyboard: Array<Array<Key>>) : Plane() {
	override val layout = RelativeLayout(ims)
	init {
		val rowsLayout = LinearLayout(ims).apply {
			orientation = LinearLayout.VERTICAL
			layout.addView(this, LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
			))
		}
		for (keyRow in keyboard) {
			val rowLayout = LinearLayout(ims).apply {
				orientation = LinearLayout.HORIZONTAL
				rowsLayout.addView(this, LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 0, 1F
				))
			}
			for (key in keyRow) {
				key.createView(ims).apply {
					rowLayout.addView(this, LinearLayout.LayoutParams(
						0, LinearLayout.LayoutParams.MATCH_PARENT, key.weight
					))
				}
			}
		}
	}
}
