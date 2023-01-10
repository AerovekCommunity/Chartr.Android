package org.aerovek.chartr.data.model.elrond

import org.aerovek.chartr.data.model.elrond.account.Account
import org.aerovek.chartr.data.model.elrond.account.GetAccountResponse
import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.data.model.elrond.address.GetAddressTransactionsResponse
import org.aerovek.chartr.data.model.elrond.contract.*
import org.aerovek.chartr.data.model.elrond.contract.QueryContractResponse
import org.aerovek.chartr.data.model.elrond.esdt.EsdtProperties
import org.aerovek.chartr.data.model.elrond.esdt.EsdtSpecialRole
import org.aerovek.chartr.data.model.elrond.esdt.EsdtSpecialRoles
import org.aerovek.chartr.data.model.elrond.network.NetworkConfig
import org.aerovek.chartr.data.model.elrond.network.GetNetworkConfigResponse
import org.aerovek.chartr.data.model.elrond.transaction.*
import org.aerovek.chartr.data.model.elrond.transaction.GetTransactionInfoResponse
import org.aerovek.chartr.data.model.elrond.transaction.SimulateTransactionResponse
import org.aerovek.chartr.data.util.toHexString
import org.bouncycastle.util.encoders.Base64
import java.math.BigInteger

internal fun GetAccountResponse.AccountData.toDomain(address: Address) = Account(
    address = address,
    nonce = nonce,
    balance = balance,
    code = code,
    username = username
)

internal fun SimulateTransactionResponse.toDomain() = SimulateTransactionInfo(
    status = status ?: "Unavailable",
    hash = hash ?: "Unavailable",
    senderShardHash = if (result?.senderShard != null) {
        result.senderShard.hash
    } else {
        "Unavailable"
    },
    senderShardStatus = if (result?.senderShard != null) {
        result.senderShard.status
    } else {
        "Unavailable"
    },
    receiverShardStatus = if (result?.receiverShard != null) {
        result.receiverShard.status
    } else {
        "Unavailable"
    },
    receiverShardHash = if (result?.receiverShard != null) {
        result.receiverShard.hash
    } else {
        "Unavailable"
    },
    scResultsCount = scResults?.size ?: 0,
    failureReason = failReason ?: "Unavailable",
    scHash = if (scResults != null && scResults.isNotEmpty()) {
        scResults[0].hash
    } else { "Unavailable" },
    scNonce = if (scResults != null && scResults.isNotEmpty()) {
        scResults[0].nonce
    } else { -1 },
    scValue = if (scResults != null && scResults.isNotEmpty()) {
        scResults[0].value
    } else { BigInteger.ZERO },
    scReceiver = if (scResults != null && scResults.isNotEmpty()) {
        scResults[0].receiver
    } else { "Unavailable" },
    scSender = if (scResults != null && scResults.isNotEmpty()) {
        scResults[0].receiver
    } else { "Unavailable" },
    scData = if (scResults != null && scResults.isNotEmpty()) {
        scResults[0].data
    } else { "Unavailable" },
    scPreviousHash = if (scResults != null && scResults.isNotEmpty()) {
        scResults[0].prevTxHash
    } else { "Unavailable" },
    scOriginalHash = if (scResults != null && scResults.isNotEmpty()) {
        scResults[0].originalTxHash
    } else { "Unavailable" },
    scGasLimit = if (scResults != null && scResults.isNotEmpty()) {
        scResults[0].gasLimit
    } else { 0 },
    scGasPrice = if (scResults != null && scResults.isNotEmpty()) {
        scResults[0].gasPrice
    } else { 0 },
    scCallType = if (scResults != null && scResults.isNotEmpty()) {
        scResults[0].callType
    } else { "Unavailable" }
)


internal fun GetAddressTransactionsResponse.TransactionOnNetworkData.toDomain() =
    TransactionOnNetwork(
        sender = Address.fromBech32(sender),
        receiver = Address.fromBech32(receiver),
        senderUsername = senderUsername,
        receiverUsername = receiverUsername,
        nonce = nonce,
        value = value,
        gasPrice = gasPrice,
        gasLimit = gasLimit,
        signature = signature,
        hash = hash,
        data = data?.let { String(Base64.decode(data)) },
        status = status,
        timestamp = timestamp,
        gasUsed = gasUsed,
        receiverShard = receiverShard,
        senderShard = senderShard,
        miniBlockHash = miniBlockHash,
        round = round,
        searchOrder = searchOrder,
        fee = fee,
        hyperblockNonce = hyperblockNonce,
        esdtAmount = esdtValues?.get(0) ?: "0",
        tokenId = tokens?.get(0) ?: ""
    )

internal fun GetTransactionInfoResponse.TransactionInfoData.toDomain(txHash: String) = TransactionInfo(
    txHash = txHash,
    type = type,
    nonce = nonce,
    round = round,
    epoch = epoch,
    value = value,
    sender = Address.fromBech32(sender),
    receiver = Address.fromBech32(receiver),
    senderUsername = senderUsername,
    receiverUsername = receiverUsername,
    gasPrice = gasPrice,
    gasLimit = gasLimit,
    data = data?.let { String(Base64.decode(data)) },
    signature = signature,
    sourceShard = sourceShard,
    destinationShard = destinationShard,
    blockNonce = blockNonce,
    timestamp = timestamp,
    miniBlockHash = miniBlockHash,
    blockHash = blockHash,
    status = status,
    hyperblockNonce = hyperblockNonce,
    smartContractResults = smartContractResults?.map { scr ->
        TransactionInfo.ScResult(
            hash = scr.hash,
            nonce = scr.nonce,
            value = scr.value,
            receiver = Address.fromBech32(scr.receiver),
            sender = Address.fromBech32(scr.sender),
            data = scr.data,
            prevTxHash = scr.prevTxHash,
            originalTxHash = scr.originalTxHash,
            gasLimit = scr.gasLimit,
            gasPrice = scr.gasPrice,
            callType = scr.callType
        )
    },
)

internal fun QueryContractResponse.Data.toDomain() = QueryContractOutput(
    returnData = returnData?.map { base64 ->
        val bytes = Base64.decode(base64)
        val asHex = bytes.toHexString()
        QueryContractOutput.ReturnData(
            asBase64 = base64,
            asString = String(bytes),
            asHex = asHex,
            asBigInt = BigInteger(asHex.ifEmpty { "0" }, 16)
        )
    },
    returnCode = returnCode,
    returnMessage = returnMessage,
    gasRemaining = gasRemaining,
    gasRefund = gasRefund,
    outputAccounts = outputAccounts,
)

internal fun QueryContractStringResponse.toDomain() = QueryContractStringOutput(
    data = data
)

internal fun QueryContractDigitResponse.toDomain() = QueryContractDigitOutput(
    data = data
)

internal fun GetNetworkConfigResponse.NetworkConfigData.toDomain() = NetworkConfig(
    chainID = chainID,
    erdDenomination = erdDenomination,
    gasPerDataByte = gasPerDataByte,
    erdGasPriceModifier = erdGasPriceModifier,
    erdLatestTagSoftwareVersion = erdLatestTagSoftwareVersion,
    erdMetaConsensusGroupSize = erdMetaConsensusGroupSize,
    minGasLimit = minGasLimit,
    minGasPrice = minGasPrice,
    minTransactionVersion = minTransactionVersion,
    erdNumMetachainNodes = erdNumMetachainNodes,
    erdNumNodesInShard = erdNumNodesInShard,
    erdNumShardsWithoutMeta = erdNumShardsWithoutMeta,
    erdRewardsTopUpGradientPoint = erdRewardsTopUpGradientPoint,
    erdRoundDuration = erdRoundDuration,
    erdRoundsPerEpoch = erdRoundsPerEpoch,
    erdShardConsensusGroupSize = erdShardConsensusGroupSize,
    erdStartTime = erdStartTime,
    erdTopUpFactor = erdTopUpFactor
)

internal fun QueryContractOutput.toEsdtProperties() = requireNotNull(returnData).let { returnDatas ->

    // format is `key-value`
    fun <T> QueryContractOutput.ReturnData.extractValue(key: String, convertor: (String) -> T): T =
        asString.split('-').let { keyValue ->
            if (keyValue.size != 2 || keyValue.first() != key) {
                throw IllegalArgumentException("cannot extract value for key `$key` in `$asString`." +
                        " Expected format is `key-value`")
            }
            return convertor.invoke(keyValue.last())
        }

    fun QueryContractOutput.ReturnData.extractBoolean(key: String): Boolean =
        extractValue(key) { value -> value.toBoolean() }

    fun QueryContractOutput.ReturnData.extractLong(key: String): Long =
        extractValue(key) { value -> value.toLong() }

    fun QueryContractOutput.ReturnData.extractBigInteger(key: String): BigInteger =
        extractValue(key) { value -> value.toBigInteger() }

    EsdtProperties(
        tokenName = returnDatas[0].asString,
        tokenType = returnDatas[1].asString,
        address = Address.fromHex(returnDatas[2].asHex),
        totalSupply = returnDatas[3].asString.toBigInteger(), // cannot use asBigInteger
        burntValue = returnDatas[4].asString.toBigInteger(), // cannot use asBigInteger
        numberOfDecimals = returnDatas[5].extractLong("NumDecimals"),
        isPaused = returnDatas[6].extractBoolean("IsPaused"),
        canUpgrade = returnDatas[7].extractBoolean("CanUpgrade"),
        canMint = returnDatas[8].extractBoolean("CanMint"),
        canBurn = returnDatas[9].extractBoolean("CanBurn"),
        canChangeOwner = returnDatas[10].extractBoolean("CanChangeOwner"),
        canPause = returnDatas[11].extractBoolean("CanPause"),
        canFreeze = returnDatas[12].extractBoolean("CanFreeze"),
        canWipe = returnDatas[13].extractBoolean("CanWipe"),
        canAddSpecialRoles = returnDatas[14].extractBoolean("CanAddSpecialRoles"),
        canTransferNftCreateRole = returnDatas[15].extractBoolean("CanTransferNFTCreateRole"),
        nftCreateStopped = returnDatas[16].extractBoolean("NFTCreateStopped"),
        numWiped = returnDatas[17].extractBigInteger("NumWiped")
    )
}

// format is : `address:specialRole,specialRole,..`
internal fun QueryContractOutput.toSpecialRoles(): EsdtSpecialRoles? {
    val formattedData = returnData?.map { returnData ->
        val keyValue = returnData.asString.split(':')
        if (keyValue.size != 2) {
            throw IllegalArgumentException(
                "cannot extract key/value in `${returnData.asString}`. Expected format is `key:value`"
            )
        }
        val key = Address.fromBech32(keyValue.first())
        val value = keyValue.last().split(',').map { specialRole ->
            EsdtSpecialRole.valueOf(specialRole)
        }
        Pair(key, value)
    }
    return formattedData?.toMap()?.let { EsdtSpecialRoles(it) }

}
