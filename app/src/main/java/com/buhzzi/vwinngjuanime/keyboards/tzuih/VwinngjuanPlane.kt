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
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.system.exitProcess

internal fun <T> Path.useTsv(block: Sequence<List<String>>.() -> T) = useLines { lines ->
	lines
		.filter { it.isNotEmpty() && !it.startsWith('#') }
		.map { it.split('\t') }
		.block()
}








private class TzhuNode(
	val parent: TzhuNode?,
	val code: Int,
) {
	val children by lazy { arrayOfNulls<TzhuNode>(tzhuComposer.fullVwinList.size) }

	var tzuihUni: String? = null

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
		path.fastJoinToString(" ") { "$it/${tzhuComposer.fullVwinList[it].label}" }
	}")"""

	fun createChild(code: Int) = TzhuNode(this, code).also {
		children[code] = it
	}
}

private val VwinngjuanIms.vwinngjuanFilesPath
	get() = getExternalFilesDir("vwinngjuan")?.toPath()
		?: error("Cannot open vwinngjuan files dir.")

private class TzhuComposer(ims: VwinngjuanIms) {
	val lejList: List<LejInfo> = (ims.vwinngjuanFilesPath / "lej.tsv").useTsv {
		map { (lej, vwinList) ->
			val keyMapPattern = lejKeyMapPatternMap[lej] ?: error("Cannot find key map pattern for lej $lej.")
			LejInfo(lej, keyMapPattern, vwinList.split(' ').fastMap { vwinLabel ->
				VwinInfo(vwinLabel, druannMap[vwinLabel])
			})
		}.toList()
	}


	val lejKeyMap: Map<Char, LejKeyAction> = generateKeyMapFromPattern(lejList.fastMapIndexed { lejIndex, lejInfo ->
		LejKeyAction(KeyContent(lejInfo.label.run {
			substring(0, offsetByCodePoints(0, 1))
		})) { keyMap = vwinKeyMapList[lejIndex] }
	}, LEJ_KEY_MAP_PATTERN)
		.also { keyMap = it }

	val vwinKeyMapList = lejList.fastMap { lej ->
		// TODO 移除try-catch塊。
		try {
			generateKeyMapFromPattern(lej.vwinList.fastMap { vwin ->
				LejKeyAction(vwin.druann
					?.let { KeyContent(druannIcon(it)) }
					?: KeyContent(vwin.label)
				) {
					composingVwinStack.push(vwin)
					keyMap = lejKeyMap
				}
			}, lej.keyMapPattern)
		} catch (e: Exception) {
			println("Error on ${lej.label}, ${lej.vwinList.size}/${lej.keyMapPattern.count { it == KeyMapPatternChar.ACTIVE }}")
			exitProcess(-1)
		}
	}


	val fullVwinList = lejList.fastFlatMap { it.vwinList }

	val vwinCodeMap = fullVwinList.withIndex().associate { (code, vwin) -> vwin.label to code }

	init {
		tzhuComposer = this
	}

	val root = TzhuNode(null, 0)

	fun generateKeyMapFromPattern(
		keyActions: Iterable<LejKeyAction>,
		pattern: String,
		mappedChars: List<Char> = defaultMappedChars,
	): Map<Char, LejKeyAction> = run {
		val hasAction = pattern.mapNotNull { when (it) {
			KeyMapPatternChar.INACTIVE -> false
			KeyMapPatternChar.ACTIVE -> true
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

	init {
		val tzhuMap = (ims.vwinngjuanFilesPath / "tzhu.tsv").useTsv {
			associate { (tzuihUni, tzhu) ->
				tzuihUni to (tzhu.takeIf { it.isNotEmpty() }?.split(' ') ?: emptyList())
			}
		}
		val nodeMap = mutableMapOf<String, List<Int>>()
		fun resolveNodePath(tzuihUni: String): List<Int> = nodeMap.getOrPut(tzuihUni) {
			(vwinCodeMap[tzuihUni]?.let { listOf(it) } ?: (tzhuMap[tzuihUni] ?: run {
				error("Tzuih $tzuihUni not found.")
			}).fastFlatMap { resolveNodePath(it) })
				.also { createOnPath(it, tzuihUni) }
		}
		tzhuMap.keys.forEach { tzuihUni -> resolveNodePath(tzuihUni) }
	}
}

private lateinit var tzhuComposer: TzhuComposer

private fun VwinngjuanIms.createTzhuComposer() = TzhuComposer(this)










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

		fun buildTzuih() = mutableListOf<String>().apply {
//			TODO 偵錯用。
//			list.run {
//				clear()
//				"丄 干 口 𡿨".split(' ')
//					.map { tzhuComposer.fullVwinList[tzhuComposer.vwinCodeMap[it]!!] }
//					.let { addAll(it) }
//			}
			var nextAvailable = 0
			do {
				var node = tzhuComposer.root
				add("")
				for (i in nextAvailable ..< list.size) {
					val code = tzhuComposer.vwinCodeMap[list[i].label]!!
					val child = node.children[code] ?: break
					node = child
					node.tzuihUni?.also {
						nextAvailable = i + 1
						this[lastIndex] = it
					}
//					TODO 偵錯用。
					println("${nextAvailable - 1}\t$i\t${list[i]}\t$node\t$this")
				}
			} while (nextAvailable < list.size)
			// TODO 特殊處理擴展區字元。
		}.fastJoinToString("")
	}
}

private val VwinngjuanIms.composingVwinStack
	get() = ComposingVwinStack(this)












private class LejKeyAction(
	val content: KeyContent,
	val action: VwinngjuanIms.() -> Unit,
) {
	override fun toString() = "LejKeyAction($content)"
}

private var keyMap by mutableStateOf<Map<Char, LejKeyAction>?>(null)


@Composable
private fun LejKey(
	ims: VwinngjuanIms,
	keyChar: Char,
	modifier: Modifier = Modifier,
) {
	(keyMap?.get(keyChar) ?: "$keyChar".let {
		LejKeyAction(KeyContent(it)) {
			if (ComposingVwinStack.notEmpty) {
				commitText(ComposingVwinStack.buildTzuih())
				composingVwinStack.clear()
			}
			commitText(it)
		}
	}).apply {
		OutlinedKey(
			ims,
			content,
			modifier,
			arrayOf(keyMap),
		) { action() }
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
			keyMap !== tzhuComposer.lejKeyMap -> keyMap = tzhuComposer.lejKeyMap
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
		when {
			ComposingVwinStack.notEmpty -> {
				commitText(ComposingVwinStack.buildTzuih())
				composingVwinStack.clear()
			}
			else -> commitText("\n")
		}
	}
}

internal val vwinngjuanPlane = Plane({ stringResource(R.string.vwinngjuan_plane) }, {
	composingVwinStack.clear()
}) { ims ->
	var exceptionNullable by remember { mutableStateOf<Exception?>(null) }
	LaunchedEffect(Unit) {
		try {
			ims.createTzhuComposer()
		} catch (e: Exception) {
			exceptionNullable = e
		}
	}

	Column {
		Row(Modifier.weight(1F)) {
			TabKey(ims, Modifier.weight(1F))
			LejKey(ims, '`', Modifier.weight(1F))
			LejKey(ims, '\'',  Modifier.weight(1F))
			LejKey(ims, '「', Modifier.weight(1F))
			LejKey(ims, '」', Modifier.weight(1F))
			LejKey(ims, '・', Modifier.weight(1F))
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
				LejKey(ims, '、', Modifier.weight(1F))
				LejKey(ims, '。', Modifier.weight(1F))
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
