package org.aerovek.chartr.data.elrondsdk.usecase

import org.aerovek.chartr.data.repository.elrond.EsdtRepository

@Deprecated("DO NOT USE!!! This should be converted into its respective repository implementation")
class GetAllIssuedEsdtUsecase internal constructor(private val esdtRepository: EsdtRepository) {

    fun execute() = esdtRepository.getAllEsdtIssued()

}
