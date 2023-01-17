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
package org.aerovek.chartr.data.exceptions

class ElrondException {

    open class AddressException(message: String? = null) : KnownException(message) {
        private val serialVersionUID = 7303569975530215510L
    }

    class CannotCreateAddressException(input: Any) :
        AddressException("Cannot create address from: $input") {
        private val serialVersionUID = 1249335179408397539L
    }

    class CannotCreateBech32AddressException(input: Any) :
        AddressException("Cannot create bech32 address from: $input") {
        private val serialVersionUID = 1249335179408397539L
    }

    class BadAddressHrpException(message: String? = null) : AddressException(message) {
        private val serialVersionUID = 7074540271315613570L
    }

    class EmptyAddressException(message: String? = null) : AddressException(message) {
        private val serialVersionUID = -170346454394596227L
    }

    class CannotConvertBitsException(message: String? = null) : AddressException(message) {
        private val serialVersionUID = 7002466269883351644L
    }

    class InvalidCharactersException(message: String? = null) : AddressException(message) {
        private val serialVersionUID = 440923894748025560L
    }

    class InconsistentCasingException(message: String? = null) : AddressException(message) {
        private val serialVersionUID = -6909226964519236168L
    }

    class MissingAddressHrpException(message: String? = null) : AddressException(message) {
        private val serialVersionUID = -2279315088416839103L
    }

    class CannotGenerateMnemonicException(message: String? = null) : KnownException(message) {
        private val serialVersionUID = -9089149758748689110L
    }

    class CannotDeriveKeysException(message: String? = null) : KnownException(message) {
        private val serialVersionUID = 6759812280546343157L
    }

    class CannotSerializeTransactionException(message: String? = null) : KnownException(message) {
        private val serialVersionUID = -1322742374396410484L
    }

    class CannotSignTransactionException(message: String? = null) : KnownException(message) {
        private val serialVersionUID = -5983779627162656410L
    }

    class ProxyRequestException(message: String? = null) : KnownException(message) {
        private val serialVersionUID = 1344143859356453293L
    }
}