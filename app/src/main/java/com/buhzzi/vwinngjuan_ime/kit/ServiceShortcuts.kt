package com.buhzzi.vwinngjuan_ime.kit

import android.content.Context
import android.os.VibratorManager
import android.view.inputmethod.InputMethodManager

val Context.scInputMethodManager
	get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

val Context.scVibratorManager
	get() = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager?
