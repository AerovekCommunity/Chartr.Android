package org.aerovek.chartr.data.model.elrond.contract

data class QueryContractInput(
    val scAddress: String,
    val funcName: String,
    val args: List<String>,
    val caller: String? = null,
    val value: String? = null
)
