package org.aerovek.chartr.data.util

import java.util.*

internal object ScUtils {

    // source:
    // https://github.com/ElrondNetwork/elrond-sdk/blob/576fdc4bc0fa713738d8556600f04e6377c7623f/erdpy/contracts.py#L156
    fun prepareArgument(arg: String): String {
        val hexPrefix = "0X"
        val argUpCase = arg.uppercase(Locale.ROOT)

        if (argUpCase.startsWith(hexPrefix)){
            return argUpCase.substring(startIndex = hexPrefix.length)
        }

        if (!argUpCase.isDigitsOnly()){
            throw IllegalArgumentException("unknown format for $arg")
        }

        return argUpCase.toBigInteger().toHex().uppercase(Locale.ROOT)
    }

    fun prepareBooleanArgument(key: String, value: Boolean) =
        "${key.toHex()}@${value.toString().toHex()}"

}
