package com.buhzzi.vwinngjuanime.keyboards.tzuih

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastJoinToString
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMapIndexed
import com.buhzzi.vwinngjuanime.R
import com.buhzzi.vwinngjuanime.VwinngjuanIms
import com.buhzzi.vwinngjuanime.keyboards.KeyContent
import com.buhzzi.vwinngjuanime.keyboards.OutlinedKey
import com.buhzzi.vwinngjuanime.keyboards.OutlinedSpace
import com.buhzzi.vwinngjuanime.keyboards.Plane
import com.buhzzi.vwinngjuanime.keyboards.backspaceText
import com.buhzzi.vwinngjuanime.keyboards.commitText
import com.buhzzi.vwinngjuanime.keyboards.latin.CtrlKey
import com.buhzzi.vwinngjuanime.keyboards.latin.MetaKey
import com.buhzzi.vwinngjuanime.keyboards.latin.SpaceKey
import com.buhzzi.vwinngjuanime.keyboards.latin.TabKey
import kotlin.io.path.div
import kotlin.io.path.reader

private class LejKeyAction(
	val content: KeyContent,
	val action: VwinngjuanIms.() -> Unit,
) {
	override fun toString() = "LejKeyAction($content)"
}

private val defaultMappedChars = listOf(
	'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
	'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l',
	'z', 'x', 'c', 'v', 'b', 'n', 'm',
)

private fun generateKeyMapFromPattern(
	keyActions: Iterable<LejKeyAction>,
	pattern: String,
	mappedChars: List<Char> = defaultMappedChars,
): Map<Char, LejKeyAction> = run {
	val hasAction = pattern.mapNotNull { when (it) {
		'-' -> false
		'+' -> true
		else -> null
	} }
	val keyActionIterator = keyActions.iterator()
	mappedChars.indices.associate { charIndex ->
		mappedChars[charIndex] to
			if (hasAction[charIndex]) keyActionIterator.next()
			else LejKeyAction(KeyContent("")) { }
	}.also {
		keyActionIterator.hasNext() && error("Didn't consume all keyActions.")
	}
}

private val lejKeyMap: Map<Char, LejKeyAction> = generateKeyMapFromPattern(lejList.fastMapIndexed { lejIndex, lejInfo ->
	LejKeyAction(KeyContent(lejInfo.label)) { keyMap = vwinKeyMapList[lejIndex] }
}, lejKeyMapPattern)

private val vwinKeyMapList = lejList.fastMap { lej ->
	generateKeyMapFromPattern(lej.vwinList.fastMap { vwin ->
		LejKeyAction(vwin.druann
			?.let { KeyContent(druannIcon(it)) }
			?: KeyContent(vwin.label)
		) {
			composingVwinStack.push(vwin)
			keyMap = lejKeyMap
		}
	}, lej.keyMapPattern)
}

private var keyMap: Map<Char, LejKeyAction> by mutableStateOf(lejKeyMap)







private class TzhuNode private constructor(
	val parent: TzhuNode?,
	val code: Int,
) {
	val children = arrayOfNulls<TzhuNode>(vwinTable.size)

	var tzuihUni: String? = null
		private set

	val path
		get() = run {
			var node: TzhuNode? = this
			generateSequence {
				node?.run {
					parent?.let {
						code.also { node = parent }
					}
				}
			}.toList().asReversed()
		}

	override fun toString() = """TzhuNode($tzuihUni, "${
		path.fastJoinToString(" ") { "$it/${vwinTable[it].label}" }
	}")"""

	fun createChild(code: Int) = TzhuNode(this, code).also {
		children[code] = it
	}

	fun getChildOf(vwin: VwinInfo) = children[vwinCodeMap[vwin.label]!!]

	companion object {
		val vwinTable = lejList.fastFlatMap { it.vwinList }

		val vwinCodeMap = vwinTable.withIndex().associate { (code, vwin) -> vwin.label to code }

		val root = TzhuNode(null, 0)

		fun clear() {
			root.children.fill(null)
		}

		fun createOnPath(path: List<Int>, tzuihUni: String) {
			var node = root
			path.fastForEachIndexed { i, code ->
				val child = node.children[code]
				when {
					i != path.lastIndex ->
						node = child ?: node.createChild(code)
					child != null -> {
						child.tzuihUni == null || error("Duplicated tzuih $tzuihUni on ${node.children[code]}.")
						child.tzuihUni = tzuihUni
					}
					else ->
						node.createChild(code).tzuihUni = tzuihUni
				}
			}
		}

		fun sync(ims: VwinngjuanIms) {
			clear()
			val tzhuMap = (ims.getExternalFilesDir("vwinngjuan")!!.toPath() / "tzhu.tsv").reader().useLines { lines ->
				lines.associate { line ->
					val (tzuihUni, tzhu) = line.split('\t', limit = 2)
					tzuihUni to (tzhu.takeIf { it.isNotEmpty() }?.split(' ') ?: emptyList())
				}
			}
			val nodeMap = mutableMapOf<String, List<Int>>()
			fun resolveNodePath(tzuihUni: String): List<Int> = nodeMap.getOrPut(tzuihUni) {
				(vwinCodeMap[tzuihUni]?.let { listOf(it) } ?: tzhuMap[tzuihUni]!!.fastFlatMap { resolveNodePath(it) })
					.also { createOnPath(it, tzuihUni) }
			}
			tzhuMap.keys.forEach { tzuihUni -> resolveNodePath(tzuihUni) }
		}
	}
}

private class ComposingVwinStack(private val ims: VwinngjuanIms) {
	private fun update() {
		ims.currentInputConnection.setComposingText(buildTzuih(), 1)
	}

	fun clear() {
		list.clear()
		update()
	}

	fun push(code: VwinInfo) {
		list.add(code)
		update()
	}

	fun pop() {
		list.removeAt(list.lastIndex)
		update()
	}

	companion object {
		private val list = mutableStateListOf<VwinInfo>()

		val notEmpty
			get() = list.isNotEmpty()

		fun buildTzuih() = mutableListOf("").apply {
			var node = TzhuNode.root
			list.forEach { vwin ->
				node = node.getChildOf(vwin)
					?: TzhuNode.root.getChildOf(vwin).also { add("") }
					?: error("Cannot get child on $node or ${TzhuNode.root}")
				node.tzuihUni?.also { this[lastIndex] = it }
			}
			// TODO 特殊處理擴展區字元。
		}.fastJoinToString("　") { it }
	}
}

private val VwinngjuanIms.composingVwinStack
	get() = ComposingVwinStack(this)









@Composable
private fun LejKey(
	ims: VwinngjuanIms,
	keyChar: Char,
	modifier: Modifier = Modifier,
) {
	(keyMap[keyChar] ?: "$keyChar".let {
		LejKeyAction(KeyContent(it)) { commitText(it) }
	}).apply {
		OutlinedKey(
			ims,
			content,
			modifier,
			arrayOf(keyMap),
			action,
		)
	}
}

@Composable
internal fun BackspaceKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent(Icons.AutoMirrored.Filled.Backspace),
		modifier,
	) {
		when {
			keyMap !== lejKeyMap -> keyMap = lejKeyMap
			ComposingVwinStack.notEmpty -> composingVwinStack.pop()
			else -> backspaceText()
		}
	}
}

@Composable
internal fun EnterKey(ims: VwinngjuanIms, modifier: Modifier = Modifier) {
	OutlinedKey(
		ims,
		KeyContent(Icons.AutoMirrored.Filled.KeyboardReturn),
		modifier,
	) {
		if (ComposingVwinStack.notEmpty) {
			commitText(ComposingVwinStack.buildTzuih())
			composingVwinStack.clear()
		} else {
			commitText("\n")
		}
	}
}

internal val vwinngjuanPlane = Plane({ stringResource(R.string.vwinngjuan_plane) }) { ims ->
	var exceptionNullable by remember { mutableStateOf<Exception?>(null) }
	LaunchedEffect(Unit) {
		try {
			TzhuNode.sync(ims)
		} catch (e: Exception) {
			exceptionNullable = e
		}
	}

	Column {
		Row(Modifier.weight(1F)) {
			TabKey(ims, Modifier.weight(1F))
			LejKey(ims, '`', Modifier.weight(1F))
			LejKey(ims, '\'',  Modifier.weight(1F))
			LejKey(ims, '[', Modifier.weight(1F))
			LejKey(ims, ']', Modifier.weight(1F))
			LejKey(ims, '\\', Modifier.weight(1F))
			LejKey(ims, '-', Modifier.weight(1F))
			LejKey(ims, '=', Modifier.weight(1F))
			BackspaceKey(ims, Modifier.weight(2F))
		}
		exceptionNullable?.also { exception ->
			OutlinedSpace(Modifier
				.weight(4F)
				.verticalScroll(rememberScrollState())) {
				Text(exception.stackTraceToString().also { println(it) })
			}
		} ?: run {
			Row(Modifier.weight(1F)) {
				LejKey(ims, '1', Modifier.weight(1F))
				LejKey(ims, '2', Modifier.weight(1F))
				LejKey(ims, '3', Modifier.weight(1F))
				LejKey(ims, '4', Modifier.weight(1F))
				LejKey(ims, '5', Modifier.weight(1F))
				LejKey(ims, '6', Modifier.weight(1F))
				LejKey(ims, '7', Modifier.weight(1F))
				LejKey(ims, '8', Modifier.weight(1F))
				LejKey(ims, '9', Modifier.weight(1F))
				LejKey(ims, '0', Modifier.weight(1F))
			}
			Row(Modifier.weight(1F)) {
				LejKey(ims, 'q', Modifier.weight(1F))
				LejKey(ims, 'w', Modifier.weight(1F))
				LejKey(ims, 'e', Modifier.weight(1F))
				LejKey(ims, 'r', Modifier.weight(1F))
				LejKey(ims, 't', Modifier.weight(1F))
				LejKey(ims, 'y', Modifier.weight(1F))
				LejKey(ims, 'u', Modifier.weight(1F))
				LejKey(ims, 'i', Modifier.weight(1F))
				LejKey(ims, 'o', Modifier.weight(1F))
				LejKey(ims, 'p', Modifier.weight(1F))
			}
			Row(Modifier.weight(1F)) {
				LejKey(ims, 'a', Modifier.weight(1F))
				LejKey(ims, 's', Modifier.weight(1F))
				LejKey(ims, 'd', Modifier.weight(1F))
				LejKey(ims, 'f', Modifier.weight(1F))
				LejKey(ims, 'g', Modifier.weight(1F))
				LejKey(ims, 'h', Modifier.weight(1F))
				LejKey(ims, 'j', Modifier.weight(1F))
				LejKey(ims, 'k', Modifier.weight(1F))
				LejKey(ims, 'l', Modifier.weight(1F))
				LejKey(ims, ';', Modifier.weight(1F))
			}
			Row(Modifier.weight(1F)) {
				LejKey(ims, '/', Modifier.weight(1F))
				LejKey(ims, 'z', Modifier.weight(1F))
				LejKey(ims, 'x', Modifier.weight(1F))
				LejKey(ims, 'c', Modifier.weight(1F))
				LejKey(ims, 'v', Modifier.weight(1F))
				LejKey(ims, 'b', Modifier.weight(1F))
				LejKey(ims, 'n', Modifier.weight(1F))
				LejKey(ims, 'm', Modifier.weight(1F))
				LejKey(ims, ',', Modifier.weight(1F))
				LejKey(ims, '.', Modifier.weight(1F))
			}
		}
		Row(Modifier.weight(1F)) {
			OutlinedSpace(Modifier.weight(2F)) { }
			MetaKey(ims, Modifier.weight(1F))
			SpaceKey(ims, Modifier.weight(4F))
			CtrlKey(ims, Modifier.weight(1F))
			EnterKey(ims, Modifier.weight(2F))
		}
	}
}
