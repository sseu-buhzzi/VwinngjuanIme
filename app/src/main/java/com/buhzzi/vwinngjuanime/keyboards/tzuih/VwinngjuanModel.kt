package com.buhzzi.vwinngjuanime.keyboards.tzuih

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastJoinToString
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMapIndexed
import com.buhzzi.vwinngjuanime.VwinngjuanIms
import com.buhzzi.vwinngjuanime.externalFilesDir
import com.buhzzi.vwinngjuanime.keyboards.KeyContent
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.RandomAccessFile
import java.lang.ref.WeakReference
import java.net.SocketTimeoutException
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.notExists
import kotlin.io.path.readBytes
import kotlin.io.path.useLines
import kotlin.io.path.writeBytes
import kotlin.system.exitProcess

internal var vwinngjuanResourceName by mutableStateOf<String?>(null)

internal var vwinngjuanResourceDownloaded by mutableLongStateOf(0)

private fun VwinngjuanIms.validateVwinngjuanResource(name: String) = run {
	println("Validate $name")
	val vwinngjuanFilesDir = externalFilesDir.toPath() / "vwinngjuan"
	val vwinngjuanResourceURL = "https://381-03011-webe.buhzzi.com/permitted/vwinngjuan/$name"
	val filePath = vwinngjuanFilesDir / name
	if (
		filePath.notExists() ||
		run {
			val hashPath = vwinngjuanFilesDir / "$name.sha256"
			println("File $name exists. Checking SHA-256 sum.")
			val hash = runBlocking {
				withTimeoutOrNull(0x1000) {
					runCatching {

						URL("$vwinngjuanResourceURL.sha256").readBytes().also {
							println("Fetched hash.")
							hashPath.writeBytes(it)
						}

					}.getOrNull()
				} ?: hashPath.takeIf { it.exists() }?.readBytes()?.also {
					println("Use old hash.")
				}
			}
			fun ByteArray.toBigIntString() = joinToString("") {
				it.toUByte().toString(0x10).padStart(0x2, '0')
			}
			println("Given SHA-256 sum: ${hash?.toBigIntString()}")
			val digest = MessageDigest.getInstance("SHA-256")
			val buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE)
			FileChannel.open(filePath).use { `in` ->
				while (`in`.read(buffer) != -0x1) {
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
		val client = OkHttpClient.Builder()
			.addInterceptor { chain ->
				val request = chain.request().newBuilder()
					.addHeader("Accept-Encoding", "gzip")
					.build()
				chain.proceed(request)
			}
			.connectTimeout(0x1000, TimeUnit.MILLISECONDS)
			.build()
		val request = Request.Builder()
			.url(vwinngjuanResourceURL)
			.build()
		do {
			try {
				client.newCall(request).execute().use { response ->
					Channels.newChannel(run {
						response.body ?: error("Failed to download $name: no response body.")
					}.byteStream()).use { `in` ->
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
			} catch (e: SocketTimeoutException) {
				println("Timeout: ${e.message}")
			}
		} while (vwinngjuanResourceName != null)
	}
	filePath
}

internal fun <T> Path.useTsv(block: Sequence<List<String>>.() -> T) = useLines { lines ->
	lines
		.filter { it.isNotEmpty() && !it.startsWith('#') }
		.map { it.split('\t') }
		.block()
}








private class TzhuNodeLazyData(
	treeFile: File,
	offset: Long,
	node: TzhuNode,
) {
	val children = MutableList<TzhuNode?>(savedTzhuComposer!!.fullVwinList.size) { null }

	val tzuihList: List<String>

	init {
		RandomAccessFile(treeFile, "r").use { raf ->
			raf.seek(offset)
			val childCount = raf.readInt()
			repeat(childCount) {
				val childCode = raf.readInt()
				val childOffset = raf.readLong()
				children[childCode] = TzhuNode(treeFile, childOffset, node, childCode)
			}
			val tzuihCount = raf.readInt()
			tzuihList = List(tzuihCount) {
				raf.readUTF()
			}
		}
	}
}

internal class TzhuNode(
	treeFile: File,
	offset: Long,
	private val parent: TzhuNode?,
	private val code: Int,
) {
	private val data by lazy { TzhuNodeLazyData(treeFile, offset, this) }

	val children
		get() = data.children

	val tzuihList
		get() = data.tzuihList

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

	val pathString
		get() = path.fastJoinToString(" ") { it.toString(0x10) }

	override fun toString() = """TzhuNode(${tzuihList.getOrNull(0x0)}, "$pathString")"""
}









internal class ComposingVwinStack(
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
		VwinngjuanIms.instance?.run {
			currentInputConnection?.setComposingText(buildTzuih(), 0x1)
		}
	}
}









internal class TzhuComposer {
	private val lejList: List<LejInfo> = VwinngjuanIms.instanceMust.validateVwinngjuanResource("lej.tsv").useTsv {
		map { (lej, vwinString) ->
			vwinString.split(' ').fastMap { vwinLabel ->
				VwinInfo(vwinLabel, druannMap[vwinLabel])
			}.run {
				LejInfo(lej, lejKeyMapPatternList[size], this)
			}
		}.toList()
	}


	val lejKeyMap: Map<Char, LejKeyAction> = generateKeyMapFromPattern(lejList.fastMapIndexed { lejIndex, lejInfo ->
		LejKeyAction(lejInfo.label.run {
			substring(0x0, offsetByCodePoints(0x0, 0x1))
		}.let { lejRepresent ->
			druannMap[lejRepresent]?.let { KeyContent(druannIcon(it)) }
				?: KeyContent(lejRepresent)
		}) { keyMap = vwinKeyMapList[lejIndex] }
	}, lejKeyMapPatternList[lejList.size])
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
			println("Error on ${lej.label}, ${lej.vwinList.size}/${lej.keyMapPattern.count { it == KeyMapPatternChar.ACTIVE }}: $e")
			e.printStackTrace()
			exitProcess(-0x1)
		}
	}


	val fullVwinList = lejList.fastFlatMap { it.vwinList }

	val vwinCodeMap = fullVwinList.withIndex().associate { (code, vwin) -> vwin.label to code }

	val root = TzhuNode(
		VwinngjuanIms.instanceMust.validateVwinngjuanResource("tzhu-tree.bin").toFile(),
		0x0, null, -0x1,
	)

	val vwinStack = ComposingVwinStack(WeakReference(this))

	private fun generateKeyMapFromPattern(
		keyActions: Iterable<LejKeyAction>,
		pattern: String,
		mappedChars: List<Char> = defaultMappedChars,
	): Map<Char, LejKeyAction> = run {
		val charsActive = pattern.mapNotNull { when (it) {
			KeyMapPatternChar.INACTIVE -> false
			KeyMapPatternChar.ACTIVE -> true
			else -> null
		} }
		val keyActionIterator = keyActions.iterator()
		mappedChars.zip(charsActive).associate { (char, active) ->
			char to if (active) {
				keyActionIterator.next()
			} else {
				LejKeyAction(KeyContent("")) { }
			}
		}.also {
			keyActionIterator.hasNext() && error("Didn't consume all keyActions.")
		}
	}
}

internal var savedTzhuComposer by mutableStateOf<TzhuComposer?>(null)
