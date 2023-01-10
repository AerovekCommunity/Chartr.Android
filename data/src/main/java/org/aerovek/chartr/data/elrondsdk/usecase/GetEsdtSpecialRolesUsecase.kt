package org.aerovek.chartr.data.elrondsdk.usecase

import org.aerovek.chartr.data.repository.elrond.EsdtRepository

@Deprecated("DO NOT USE!!! This should be converted into its respective repository implementation")
class GetEsdtSpecialRolesUsecase internal constructor(private val esdtRepository: EsdtRepository) {

    fun execute(tokenIdentifier: String) = esdtRepository.getEsdtSpecialRoles(
        tokenIdentifier
    )

}
