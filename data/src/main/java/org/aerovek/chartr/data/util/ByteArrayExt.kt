package org.aerovek.chartr.data.util

import org.bouncycastle.util.encoders.Hex
import java.nio.ByteBuffer

fun ByteArray.toHexString() = String(Hex.encode(this))

fun ByteBuffer.toByteArray(): ByteArray {
    rewind()
    val data = ByteArray(remaining())
    get(data)
    return data
}