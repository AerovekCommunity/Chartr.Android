package org.aerovek.chartr.data.elrondsdk.usecase

import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.data.model.elrond.contract.QueryContractInput
import org.aerovek.chartr.data.model.elrond.contract.QueryContractOutput
import org.aerovek.chartr.data.repository.elrond.VmRepository

@Deprecated("DO NOT USE!!! This should be converted into its respective repository implementation")
class QueryContractUsecase internal constructor(
    private val vmRepository: VmRepository
) {

    fun execute(
        contractAddress: Address,
        funcName: String,
        args: List<String> = emptyList(),
        caller: String? = null,
        value: String? = null
    ): QueryContractOutput {
        val payload = QueryContractInput(
            scAddress = contractAddress.bech32,
            funcName = funcName,
            args = args,
            caller = caller,
            value = value
        )
        return vmRepository.queryContract(payload)
    }
}
