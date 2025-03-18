package com.buhzzi.vwinngjuanime.keyboards.tzuih

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastJoinToString
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMapIndexed
import com.buhzzi.vwinngjuanime.VwinngjuanIms
import com.buhzzi.vwinngjuanime.keyboards.KeyContent
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.div
import kotlin.io.path.notExists
import kotlin.io.path.useLines
import kotlin.system.exitProcess

private fun VwinngjuanIms.downloadVwinngjuanResource(name: String) = (run {
	getExternalFilesDir("vwinngjuan") ?: error("Cannot open vwinngjuan files dir.")
}.toPath() / name).also { filePath ->
	if (filePath.notExists()) {
		val conn = URL("https://381-02007.buhzzi.com/permitted/vwinngjuan/$name").openConnection() as HttpURLConnection
		conn.connect()
		conn.responseCode == HttpURLConnection.HTTP_OK || error("Server responds with $conn")
		Channels.newChannel(conn.inputStream).use { `in` ->
			FileChannel.open(
				filePath,
				StandardOpenOption.WRITE,
				StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.CREATE,
			).use { out ->
				out.transferFrom(`in`, 0, Long.MAX_VALUE)
			}
		}
	}
}

internal fun <T> Path.useTsv(block: Sequence<List<String>>.() -> T) = useLines { lines ->
	lines
		.filter { it.isNotEmpty() && !it.startsWith('#') }
		.map { it.split('\t') }
		.block()
}








internal class TzhuNode(
	tzhuComposer: TzhuComposer,
	private val parent: TzhuNode?,
	private val code: Int,
) {
	val children by lazy { arrayOfNulls<TzhuNode>(tzhuComposer.fullVwinList.size) }

	var tzuihUni: String? = null

	private val path
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

	@Deprecated(
		"Default toString won't provide tree node names.",
		ReplaceWith("toString(tzhuComposer)")
	)
	override fun toString() = super.toString()

	fun toString(tzhuComposer: TzhuComposer) = """TzhuNode($tzuihUni, "${
		path.fastJoinToString(" ") { "$it/${tzhuComposer.fullVwinList[it].label}" }
	}")"""

	fun createChild(tzhuComposer: TzhuComposer, code: Int) = TzhuNode(tzhuComposer, this, code).also {
		children[code] = it
	}
}









internal class ComposingVwinStack(
	private val ims: WeakReference<VwinngjuanIms>,
	private val tzhuComposer: WeakReference<TzhuComposer>,
) {
	private val list = mutableStateListOf<VwinInfo>()

	val notEmpty
		get() = list.isNotEmpty()

	private fun update() {
		ims.get()?.run {
			currentInputConnection.setComposingText(buildTzuih(), 1)
		}
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

	fun buildTzuih() = mutableListOf<String>().apply {
//			TODO 偵錯用。
//			list.run {
//				clear()
//				"丄 干 口 𡿨".split(' ')
//					.map { tzhuComposer.fullVwinList[tzhuComposer.vwinCodeMap[it]!!] }
//					.let { addAll(it) }
//			}
		tzhuComposer.get()?.also { tzhuComposer ->
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
		}
	}.fastJoinToString("")
}









internal class TzhuComposer(ims: VwinngjuanIms) {
	private val lejList: List<LejInfo> = ims.downloadVwinngjuanResource("lej.tsv").useTsv {
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

	private val vwinKeyMapList = lejList.fastMap { lej ->
		// TODO 移除try-catch塊。
		try {
			generateKeyMapFromPattern(lej.vwinList.fastMap { vwin ->
				LejKeyAction(vwin.druann
					?.let { KeyContent(druannIcon(it)) }
					?: KeyContent(vwin.label)
				) {
					vwinStack.push(vwin)
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

	val root = TzhuNode(this, null, 0)

	val vwinStack = ComposingVwinStack(WeakReference(ims), WeakReference(this))

	private fun generateKeyMapFromPattern(
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

	private fun createOnPath(path: List<Int>, tzuihUni: String) {
		var node = root
		path.fastForEachIndexed { i, code ->
			val child = node.children[code]
			when {
				i != path.lastIndex ->
					node = child ?: node.createChild(this, code)
				child != null -> {
					child.tzuihUni == null ||
						error("Duplicated tzuih $tzuihUni on ${node.children[code]?.toString(this)}.")
					child.tzuihUni = tzuihUni
				}
				else ->
					node.createChild(this, code).tzuihUni = tzuihUni
			}
		}
	}

	init {
		val tzhuMap = ims.downloadVwinngjuanResource("tzhu.tsv").useTsv {
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
