package com.buhzzi.vwinngjuanime.keyboards.tzuih

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastJoinToString
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMapIndexed
import com.buhzzi.vwinngjuanime.VwinngjuanIms
import com.buhzzi.vwinngjuanime.keyboards.KeyContent
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import java.lang.ref.WeakReference
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.security.MessageDigest
import kotlin.io.path.div
import kotlin.io.path.notExists
import kotlin.io.path.readBytes
import kotlin.io.path.useLines
import kotlin.io.path.writeBytes
import kotlin.system.exitProcess

internal var vwinngjuanResourceName by mutableStateOf<String?>(null)

internal var vwinngjuanResourceDownloaded by mutableLongStateOf(0)

private fun VwinngjuanIms.validateVwinngjuanResource(name: String) = run {
	println("Validate $name")
	val vwinngjuanFilesDir = run {
		getExternalFilesDir("vwinngjuan") ?: error("Cannot open vwinngjuan files dir.")
	}.toPath()
	val filePath = vwinngjuanFilesDir / name
	if (
		filePath.notExists() ||
		run {
			val hashPath = vwinngjuanFilesDir / "$name.sha256"
			println("File $name exists. Checking SHA-256 sum.")
			val hash = runBlocking {
				withTimeoutOrNull(0x1000) {
					runCatching {

						URL("https://381-02007.buhzzi.com/permitted/vwinngjuan/$name.sha256").readBytes().also {
							println("Fetched hash.")
							hashPath.writeBytes(it)
						}

					}.getOrNull()
				} ?: hashPath.readBytes().also {
					println("Use old hash.")
				}
			}
			fun ByteArray.toBigIntString() = joinToString("") {
				it.toUByte().toString(0x10).padStart(0x2, '0')
			}
			println("Given SHA-256 sum: ${hash.toBigIntString()}")
			val digest = MessageDigest.getInstance("SHA-256")
			val buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE)
			FileChannel.open(filePath).use { `in` ->
				while (`in`.read(buffer) != -1) {
					buffer.flip()
					digest.update(buffer)
					buffer.clear()
				}
			}
			!digest.digest().also { calcHash ->
				println("Digest: ${calcHash.toBigIntString()}")
			}.contentEquals(hash)
		}
	) {
		println("Start downloading vwinngjuan resource $name.")
		vwinngjuanResourceName = name
		Channels.newChannel(URL("https://381-02007.buhzzi.com/permitted/vwinngjuan/$name").openStream()).use { `in` ->
			FileChannel.open(
				filePath,
				StandardOpenOption.WRITE,
				StandardOpenOption.TRUNCATE_EXISTING,
				StandardOpenOption.CREATE,
			).use { out ->
				val buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE)
				var transferred = 0x0L
				vwinngjuanResourceDownloaded = 0x0
				while (`in`.read(buffer).also { transferred += it } != -0x1) {
					vwinngjuanResourceDownloaded = transferred
					buffer.flip()
					out.write(buffer)
					buffer.clear()
					Thread.sleep(0x4)
				}
				vwinngjuanResourceName = null
			}
		}
	}
	filePath
}

internal fun <T> Path.useTsv(block: Sequence<List<String>>.() -> T) = useLines { lines ->
	lines
		.filter { it.isNotEmpty() && !it.startsWith('#') }
		.map { it.split('\t') }
		.block()
}








internal class TzhuNode(
	private val tzhuComposer: WeakReference<TzhuComposer>,
	private val parent: TzhuNode?,
	private val code: Int,
) {
	val children by lazy { arrayOfNulls<TzhuNode>(tzhuComposer.get()!!.fullVwinList.size) }

	val tzuihList = mutableListOf<String>()

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

	val pathString
		get() = path.fastJoinToString(" ") { "${it.toString(0x10)}:${tzhuComposer.get()?.fullVwinList?.get(it)?.label}" }

	override fun toString() = """TzhuNode(${tzuihList.getOrNull(0x0)}, "$pathString")"""

	fun createChild(code: Int) = TzhuNode(tzhuComposer, this, code).also {
		children[code] = it
	}
}









internal class ComposingVwinStack(
	private val ims: WeakReference<VwinngjuanIms>,
	private val tzhuComposer: WeakReference<TzhuComposer>,
) {
	private val vwinList = mutableListOf<VwinInfo>()

	val tzhuList = mutableListOf<Pair<TzhuNode, Int>>()

	val notEmpty
		get() = vwinList.isNotEmpty()

	private fun update() {
		collectNodes()
		dumpTzuih()
	}

	fun clear() {
		vwinList.clear()
		update()
	}

	fun push(code: VwinInfo) {
		vwinList.add(code)
		update()
	}

	fun pop() {
		vwinList.removeAt(vwinList.lastIndex)
		update()
	}

	private fun collectNodes() {
//			TODO 偵錯用。
//			list.run {
//				clear()
//				"丄 干 口 𡿨".split(' ')
//					.map { tzhuComposer.fullVwinList[tzhuComposer.vwinCodeMap[it]!!] }
//					.let { addAll(it) }
//			}
		tzhuComposer.get()?.also { tzhuComposer ->
			tzhuList.clear()
			var nextAvailable = 0x0
			while (nextAvailable < vwinList.size) {
				var node = tzhuComposer.root
				var availableNode: TzhuNode? = null
				for (i in nextAvailable ..< vwinList.size) {
					val code = tzhuComposer.vwinCodeMap[vwinList[i].label]!!
					val child = node.children[code] ?: break
					node = child
					node.takeIf { it.tzuihList.isNotEmpty() }?.also {
						nextAvailable = i + 0x1
						availableNode = it
					}
//					TODO 偵錯用。
// 					println("${nextAvailable - 1}\t$i\t${vwinList[i]}\t$node\t$this")
				}
				tzhuList.add(availableNode!! to 0x0)
			}
			// TODO 特殊處理擴展區字元。
		}
	}

	fun buildTzuih() = run {
		println(tzhuList)
		tzhuList.fastJoinToString("") { (node, selection) ->
			node.tzuihList[selection]
		}
	}

	fun dumpTzuih() {
		ims.get()?.run {
			currentInputConnection.setComposingText(buildTzuih(), 1)
		}
	}
}









internal class TzhuComposer(ims: VwinngjuanIms) {
	private val lejList: List<LejInfo> = ims.validateVwinngjuanResource("lej.tsv").useTsv {
		map { (lej, vwinList) ->
			val keyMapPattern = lejKeyMapPatternMap[lej] ?: error("Cannot find key map pattern for lej $lej.")
			LejInfo(lej, keyMapPattern, vwinList.split(' ').fastMap { vwinLabel ->
				VwinInfo(vwinLabel, druannMap[vwinLabel])
			})
		}.toList()
	}


	val lejKeyMap: Map<Char, LejKeyAction> = generateKeyMapFromPattern(lejList.fastMapIndexed { lejIndex, lejInfo ->
		LejKeyAction(KeyContent(lejInfo.label.run {
			substring(0x0, offsetByCodePoints(0x0, 0x1))
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

	val root = TzhuNode(WeakReference(this), null, 0x0)

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

	private fun createOnPath(path: List<Int>) = run {
		var node = root
		path.fastForEach { code ->
			node = node.children[code] ?: node.createChild(code)
		}
		node
	}

	init {
		val tzhuMap = ims.validateVwinngjuanResource("tzhu.tsv").useTsv {
			associate { (tzuih, tzhu) ->
				tzuih to (tzhu.takeIf { it.isNotEmpty() }?.split(' ') ?: emptyList())
			}
		}
		val nodeMap = mutableMapOf<String, List<Int>>()
		fun resolveNodePath(tzuih: String): List<Int> = nodeMap.getOrPut(tzuih) {
			(vwinCodeMap[tzuih]?.let { listOf(it) } ?: (tzhuMap[tzuih] ?: run {
				error("Tzuih $tzuih not found.")
			}).fastFlatMap { resolveNodePath(it) })
				.also { tzhu ->
					val node = createOnPath(tzhu)
					node.tzuihList.add(tzuih)
					lastCreatedTzhu = tzuih to node
				}
		}
		tzhuMap.keys.forEach { tzuih -> resolveNodePath(tzuih) }
		lastCreatedTzhu = null
	}

	companion object {
		var lastCreatedTzhu by mutableStateOf<Pair<String, TzhuNode>?>(null)
	}
}

internal var savedTzhuComposer by mutableStateOf<TzhuComposer?>(null)
