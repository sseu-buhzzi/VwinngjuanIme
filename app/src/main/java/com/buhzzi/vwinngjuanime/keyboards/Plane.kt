package com.buhzzi.vwinngjuanime.keyboards

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.util.fastAny
import com.buhzzi.vwinngjuanime.VwinngjuanIms
import kotlin.math.abs

const val LONG_PRESS_TIMEOUT = 0x200L
const val REPEAT_INTERVAL = 0x20L

internal class Plane(
	val getName: @Composable () -> String,
	val onFinishInput: VwinngjuanIms.() -> Unit = { },
	val onWindowHidden: VwinngjuanIms.() -> Unit = { },
	val composableFunction: @Composable () -> Unit,
) {
	val name
		@Composable
		get() = getName()
}

@Composable
internal inline fun OutlinedSpace(
	modifier: Modifier = Modifier,
	crossinline content: @Composable () -> Unit,
) {
	Surface(
		modifier.fillMaxSize(),
		border = ButtonDefaults.outlinedBorder,
	) {
		content()
	}
}

internal enum class PointerStatus {
	// To click down, is to put the pointer down and keep not moving it.
	HELD,
	RELEASED,
	MOVED,
}

internal suspend fun AwaitPointerEventScope.awaitPointerStatus(movedThreshold: Float) =
	awaitPointerEvent().changes.run {
		when {
			!fastAny { it.pressed } -> PointerStatus.RELEASED
			fastAny { it.positionChange().run {
				abs(x) > movedThreshold || abs(y) > movedThreshold
			} } -> PointerStatus.MOVED
			else -> PointerStatus.HELD
		}
	}

internal suspend fun AwaitPointerEventScope.timeoutPointerStatus(time: Long, movedThreshold: Float) =
	/*
		null -> false (timed out)
		break -> true (released or moved)
	 */
	withTimeoutOrNull(time) {
		var lastStatus: PointerStatus
		do {
			lastStatus = awaitPointerStatus(movedThreshold)
		} while (lastStatus == PointerStatus.HELD)
		lastStatus
	}

private fun VwinngjuanIms.vibrate() {
	getSystemService(Vibrator::class.java).vibrate(
		VibrationEffect.createOneShot(0x40, 0xff)
	)
}

@Composable
internal inline fun OutlinedClickable(
	crossinline action: VwinngjuanIms.() -> Unit,
	modifier: Modifier = Modifier,
	keysPointerInput: Array<Any?> = arrayOf(Unit),
	movedThreshold: Float = 16F,
	crossinline content: @Composable () -> Unit,
) {
	val ims = VwinngjuanIms.instanceMust
	OutlinedSpace(modifier
		.clickable { }
		.pointerInput(keys = keysPointerInput) {
			awaitPointerEventScope {
				while (true) {
					awaitPointerStatus(movedThreshold) == PointerStatus.HELD || continue
					ims.vibrate()
					when (timeoutPointerStatus(LONG_PRESS_TIMEOUT, movedThreshold)) {
						PointerStatus.RELEASED -> {
//							println("action once")
							ims.action()
							continue
						}

						PointerStatus.MOVED -> continue

						else -> do {
//							println("action repeat")
							ims.action()
						} while (
							timeoutPointerStatus(REPEAT_INTERVAL, movedThreshold)?.equals(PointerStatus.HELD) != false
						)
					}
				}
			}
		},
	) { content() }
}

internal class KeyContent(
	val desc: String?,
	val icon: ImageVector?,
) {
	constructor(desc: String) : this(desc, null)

	constructor(icon: ImageVector) : this(null, icon)

	override fun toString() = "KeyContent($desc, $icon)"
}

@Composable
internal inline fun OutlinedKey(
	content: KeyContent,
	modifier: Modifier = Modifier,
	keysPointerInput: Array<Any?> = arrayOf(Unit),
	movedThreshold: Float = 16F,
	crossinline action: VwinngjuanIms.() -> Unit,
) {
	OutlinedClickable(action, modifier, keysPointerInput, movedThreshold) {
		Column(
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			content.apply {
				icon?.also { Icon(it, desc) }
				desc?.also { Text(it, textAlign = TextAlign.Center) }
			}
		}
	}
}
