package org.aerovek.chartr.data.util

import org.bouncycastle.util.encoders.Hex
import java.nio.charset.StandardCharsets

internal fun String.isDigitsOnly(): Boolean {
    val integerChars = '0'..'9'
    return all { it in integerChars }
}

fun String.toHex() = String(Hex.encode(toByteArray(StandardCharsets.UTF_8)))
fun String.toHexBytes() = Hex.encode(toByteArray(StandardCharsets.UTF_8))
