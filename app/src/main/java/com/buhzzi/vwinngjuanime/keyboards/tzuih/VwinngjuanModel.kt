package com.buhzzi.vwinngjuanime.keyboards.tzuih

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.util.fastJoinToString
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMapNotNull
import com.buhzzi.util.bigIntegerToString
import com.buhzzi.util.getSha256Sum
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
import java.util.concurrent.TimeUnit
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.readBytes
import kotlin.io.path.useLines
import kotlin.io.path.writeBytes

internal var vwinngjuanResourceName by mutableStateOf<String?>(null)

internal var vwinngjuanResourceDownloaded by mutableLongStateOf(0)

private fun VwinngjuanIms.validateVwinngjuanResource(name: String) = run {
	println("Validate $name")
	val vwinngjuanFilesDir = (externalFilesDir.toPath() / "vwinngjuan").createDirectories()
	val vwinngjuanResourceURL = "https://381-03011-http.buhzzi.com/permitted/vwinngjuan/$name"
	val networkThrottling = (externalFilesDir.toPath() / "vwinngjuan" / "network_throttling.lck").exists()
	val filePath = vwinngjuanFilesDir / name
	if (networkThrottling) {
		println("Under network throttling. Unable to download.")
	} else if (
		filePath.exists() &&
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
			println("Given SHA-256 sum: ${hash?.bigIntegerToString()}")
			filePath.getSha256Sum().also { calcHash ->
				println("Digest: ${calcHash.bigIntegerToString()}")
			}.contentEquals(hash)
		}
	) {
		println("Digest matched. No need to download again.")
	} else {
		println("Start downloading vwinngjuan resource $name.")
		try {
			vwinngjuanResourceName = name
			vwinngjuanResourceDownloaded = 0x0
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
						response.run {
							isSuccessful || error("Failed to download $name: $code\n$body")
							body ?: error("Failed to download $name: no response body.")
						}.byteStream().let { Channels.newChannel(it) }.use { `in` ->
							FileChannel.open(
								filePath,
								StandardOpenOption.WRITE,
								StandardOpenOption.TRUNCATE_EXISTING,
								StandardOpenOption.CREATE,
							).use { out ->
								val buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE)
								var transferred = 0x0L
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
		} finally {
			vwinngjuanResourceName = null
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

	val tzhuList = mutableStateListOf<Pair<TzhuNode, Int>>()

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
	val lejDataMap = VwinngjuanIms.instanceMust.validateVwinngjuanResource("lej.tsv").useTsv {
		map { (keyTableLabel, keyLabel, keyString) ->
			Triple(keyTableLabel.takeIf { it.isNotEmpty() }, keyLabel.takeIf { it.isNotEmpty() }, keyString[0x0])
		}.groupBy({ (keyTableLabel, _, _) ->
			keyTableLabel
		}) { (_, keyLabel, keyChar) ->
			keyLabel to keyChar
		}.toMutableMap()
	}

	val fullVwinList = lejDataMap.mapNotNull { (keyTableLabel, vwinDataList) ->
		keyTableLabel?.let {
			vwinDataList.fastMapNotNull { (keyLabel, _) ->
				keyLabel
			}.distinct().fastMap { vwinLabel ->
				VwinInfo(vwinLabel, druannMap[vwinLabel])
			}
		}
	}.flatten()

	val vwinCodeMap = fullVwinList.withIndex().associate { (code, vwin) -> vwin.label to code }

	val lejKeyTable = lejDataMap.remove(null)!!.associate { (lejLabel, keyChar) ->
		keyChar to if (lejLabel == null) {
			LejKeyContentAction(KeyContent("")) { }
		} else {
			LejKeyContentAction(lejLabel.run {
				substring(0x0, length - 0x3)
			}.let { lejRepresent ->
				druannMap[lejRepresent]?.let { KeyContent(druannIcon(it)) }
					?: KeyContent(lejRepresent)
			}) {
				keyTable = vwinKeyTableMap[lejLabel]
			}
		}
	}.also { keyTable = it }

	private val vwinKeyTableMap: Map<String, Map<Char, LejKeyContentAction>> = lejDataMap.map { (lejLabel, vwinDataList) ->
		lejLabel!! to vwinDataList.associate { (vwinLabel, keyChar) ->
			keyChar to if (vwinLabel == null) {
				LejKeyContentAction(KeyContent("")) { }
			} else {
				LejKeyContentAction(druannMap[vwinLabel]?.let { KeyContent(druannIcon(it)) }
					?: KeyContent(vwinLabel)
				) {
					vwinStack.push(fullVwinList[vwinCodeMap[vwinLabel]!!])
					keyTable = lejKeyTable
				}
			}
		}
	}.toMap()

	val root = TzhuNode(
		VwinngjuanIms.instanceMust.validateVwinngjuanResource("tzhu-tree.bin").toFile(),
		0x0, null, -0x1,
	)

	val vwinStack = ComposingVwinStack(WeakReference(this))

	private fun createKeyTable(
		mappings: Iterable<Pair<LejKeyContentAction?, Char>>,
	): Map<Char, LejKeyContentAction> = run {
		mappings.associate { (keyAction, mappedChar) ->
			mappedChar to (keyAction ?: LejKeyContentAction(KeyContent("")) { })
		}
	}
}

internal var savedTzhuComposer by mutableStateOf<TzhuComposer?>(null)
