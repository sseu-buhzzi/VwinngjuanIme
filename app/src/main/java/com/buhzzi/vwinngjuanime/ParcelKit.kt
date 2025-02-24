package com.buhzzi.vwinngjuanime

import android.os.Parcel
import android.os.Parcelable

internal inline fun <T> useParcel(block: Parcel.() -> T) =
	Parcel.obtain().run {
		try {
			block()
		} catch (e: Exception) {
			null
		} finally {
			recycle()
		}
	}

internal fun Parcelable.toBytes() = useParcel {
	writeToParcel(this, 0)
	marshall().copyOf()
}

internal fun <T : Parcelable> Parcelable.Creator<T>.fromBytes(bytes: ByteArray) = useParcel {
	unmarshall(bytes, 0, bytes.size)
	setDataPosition(0)
	createFromParcel(this)
}

