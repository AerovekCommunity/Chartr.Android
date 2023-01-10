package org.aerovek.chartr.util

/**
 * Converts the big integer string representation of a token
 * balance into the decimal version derived by dividing
 * the big int by 1000000000000000000 which essentially moves the decimal over 18 places to the left
*/
fun String.formatTokenBalance(decimalPrecision: Int? = null): String {
    return if (this.isNotEmpty() && this != "0") {
        val amountParts = this.toBigInteger().divideAndRemainder("1000000000000000000".toBigInteger())
        val quotient = amountParts[0].toString()
        var remainder = amountParts[1].toString()

        if (remainder.length < 18) {
            for (i in remainder.length..17) {
                remainder = "0$remainder"
            }
        }

        // Format display value to 4 decimal places
        val doubleVal = "$quotient.$remainder".toBigDecimal()
        if (decimalPrecision != null) {
            String.format("%.${decimalPrecision}f", doubleVal)
        } else {
            doubleVal.toString()
        }
    } else {
        "0"
    }
}
