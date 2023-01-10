package org.aerovek.chartr.data.model

import org.aerovek.chartr.data.exceptions.RestClientRequestException

class ResponseBase<T> {
    val data: T? = null
    val error: String? = null
    val code: String? = null // ex: "successful"

    @Throws(RestClientRequestException::class)
    fun throwIfError() {
        if (!error.isNullOrEmpty()) {
            throw RestClientRequestException(error)
        }
        /*
        if (code != "successful") {
            throw RestClientRequestException(code)
        }

         */
    }
}