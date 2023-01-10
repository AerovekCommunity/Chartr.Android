package org.aerovek.chartr.data.model.elrond.transaction

internal data class GetTransactionStatusResponse(
    val status: String // ex: "pending", "success"
)
