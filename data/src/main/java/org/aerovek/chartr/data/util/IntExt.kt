package org.aerovek.chartr.data.util

fun Int.toHexString() = "%02X".format(this and 0xFF)
