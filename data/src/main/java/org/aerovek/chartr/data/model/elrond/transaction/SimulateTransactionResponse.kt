package org.aerovek.chartr.data.model.elrond.transaction

internal data class SimulateTransactionResponse(
    val result: TransactionShardInfo?,
    val status: String?,
    val hash: String?,
    val failReason: String?,
    val scResults: List<GetTransactionInfoResponse.ScResult>?
) {
    data class TransactionShardInfo(
        val senderShard: ShardInfo?,
        val receiverShard: ShardInfo?
    )

    data class ShardInfo(
        val status: String?,
        val hash: String?
    )
}