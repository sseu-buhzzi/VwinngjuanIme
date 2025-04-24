package com.buhzzi.util

import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.security.MessageDigest

fun Path.getSha256Sum(): ByteArray = MessageDigest.getInstance("SHA-256").run {
	val buffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE)
	FileChannel.open(this@getSha256Sum).use { `in` ->
		while (`in`.read(buffer) != -0x1) {
			buffer.flip()
			update(buffer)
			buffer.clear()
		}
	}
	digest()
}

fun ByteArray.bigIntegerToString(): String = BigInteger(0x1, this).toString(0x10)
