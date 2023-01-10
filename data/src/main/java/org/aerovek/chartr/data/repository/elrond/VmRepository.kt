package org.aerovek.chartr.data.repository.elrond

import org.aerovek.chartr.data.model.ChartrAccount
import org.aerovek.chartr.data.model.elrond.contract.QueryContractDigitOutput
import org.aerovek.chartr.data.model.elrond.contract.QueryContractInput
import org.aerovek.chartr.data.model.elrond.contract.QueryContractOutput
import org.aerovek.chartr.data.model.elrond.contract.QueryContractStringOutput

interface VmRepository {
    fun queryContract(queryContractInput: QueryContractInput): QueryContractOutput
    fun queryContractHex(queryContractInput: QueryContractInput): QueryContractStringOutput
    fun queryContractString(queryContractInput: QueryContractInput): QueryContractStringOutput
    fun queryContractInt(queryContractInput: QueryContractInput): QueryContractDigitOutput
    fun getChartrAccount(queryContractInput: QueryContractInput): ChartrAccount?
    fun getChartrAccountList(queryContractInput: QueryContractInput): List<ChartrAccount>?
}
