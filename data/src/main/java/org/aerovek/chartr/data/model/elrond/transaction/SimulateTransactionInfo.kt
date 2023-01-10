package org.aerovek.chartr.data.model.elrond.transaction

import java.math.BigInteger

data class SimulateTransactionInfo(
    val status: String?,
    val hash: String?,
    val senderShardStatus: String?,
    val senderShardHash: String?,
    val receiverShardHash: String?,
    val receiverShardStatus: String?,
    val scResultsCount: Int,
    val failureReason: String?,
    val scHash: String?,
    val scNonce: Long?,
    val scValue: BigInteger?,
    val scReceiver: String?,
    val scSender: String?,
    val scData: String?,
    val scPreviousHash: String?,
    val scOriginalHash: String?,
    val scGasLimit: Long?,
    val scGasPrice: Long?,
    val scCallType: String?
)