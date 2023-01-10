package org.aerovek.chartr.data.repository.elrond

import com.google.gson.GsonBuilder
import org.aerovek.chartr.data.model.ChartrAccount
import org.aerovek.chartr.data.model.elrond.toDomain
import org.aerovek.chartr.data.network.ElrondGatewayService
import org.aerovek.chartr.data.model.elrond.contract.QueryContractDigitOutput
import org.aerovek.chartr.data.model.elrond.contract.QueryContractInput
import org.aerovek.chartr.data.model.elrond.contract.QueryContractOutput
import org.aerovek.chartr.data.model.elrond.contract.QueryContractStringOutput
import org.aerovek.chartr.data.util.deserialize
import org.bouncycastle.util.encoders.Base64
import java.lang.Exception

internal class VmRepositoryImpl(private val elrondService: ElrondGatewayService): VmRepository {

    override fun queryContract(queryContractInput: QueryContractInput): QueryContractOutput {
        return requireNotNull(elrondService.queryContract(queryContractInput).data).data.toDomain()
    }

    override fun queryContractHex(queryContractInput: QueryContractInput): QueryContractStringOutput {
        return requireNotNull(elrondService.queryContractHex(queryContractInput).data).toDomain()
    }

    override fun queryContractString(queryContractInput: QueryContractInput): QueryContractStringOutput {
        return requireNotNull(elrondService.queryContractString(queryContractInput).data).toDomain()
    }

    override fun queryContractInt(queryContractInput: QueryContractInput): QueryContractDigitOutput {
        return requireNotNull(elrondService.queryContractInt(queryContractInput).data).toDomain()
    }

    override fun getChartrAccountList(queryContractInput: QueryContractInput): List<ChartrAccount>? {
        val accountList = retrieveChartrAccounts(queryContractInput)
        return if (accountList.isNullOrEmpty()) {
            null
        } else {
            accountList
        }
    }

    override fun getChartrAccount(queryContractInput: QueryContractInput): ChartrAccount? {
        val accountList = retrieveChartrAccounts(queryContractInput)
        return if (accountList.isNullOrEmpty()) {
            null
        } else {
            accountList[0]
        }
    }

    private fun retrieveChartrAccounts(queryContractInput: QueryContractInput): List<ChartrAccount>? {
        // The base64List will have the base64 encoded chartrAccount object
        val base64List = requireNotNull(elrondService.queryContract(queryContractInput).data).data.returnData
        if (base64List.isNullOrEmpty()) {
            return null
        }

        val gson = GsonBuilder().disableHtmlEscaping().create()

        // Decode the base64 list into a string list so we can deserialize it
        val decoded = base64List.map { base64 ->
            String(Base64.decode(base64))
        }

        if (decoded.isEmpty()) {
            return null
        }

        return try {
            val chartrAccounts: MutableList<ChartrAccount> = mutableListOf()
            for (item in decoded) {
                val obj = gson.deserialize<List<ChartrAccount>>(item)
                chartrAccounts.add(obj[0])
            }

            return chartrAccounts.ifEmpty {
                null
            }

        } catch (e: Exception) {
            null
        }
    }
}
