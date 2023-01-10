package org.aerovek.chartr.data.elrondsdk.usecase

import org.aerovek.chartr.data.model.elrond.address.Address
import java.math.BigInteger

@Deprecated("DO NOT USE!!! This should be converted into its respective repository implementation")
class GetDnsRegistrationCostUsecase internal constructor(
    private val queryContractUsecase: QueryContractUsecase,
    private val computeDnsAddressUsecase: ComputeDnsAddressUsecase
) {

    fun execute(shardId: Byte): BigInteger {
        return execute(computeDnsAddressUsecase.execute(shardId))
    }

    fun execute(dnsAddress: Address): BigInteger {
        val result = queryContractUsecase.execute(dnsAddress, "getRegistrationCost")
        return when {
            result.returnData.isNullOrEmpty() -> BigInteger.ZERO
            else -> result.returnData[0].asBigInt
        }
    }
}
