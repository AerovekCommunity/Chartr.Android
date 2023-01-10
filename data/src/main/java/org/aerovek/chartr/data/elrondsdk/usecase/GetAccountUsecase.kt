package org.aerovek.chartr.data.elrondsdk.usecase

import org.aerovek.chartr.data.model.elrond.address.Address
import org.aerovek.chartr.data.repository.elrond.AccountRepository

@Deprecated("DO NOT USE!!! This should be converted into its respective repository implementation")
class GetAccountUsecase internal constructor(private val accountRepository: AccountRepository) {

    fun execute(address: Address) = accountRepository.getAccount(address)

}
