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