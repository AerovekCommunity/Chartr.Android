/*
The MIT License (MIT)

Copyright (c) 2023-present Aerovek

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
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
