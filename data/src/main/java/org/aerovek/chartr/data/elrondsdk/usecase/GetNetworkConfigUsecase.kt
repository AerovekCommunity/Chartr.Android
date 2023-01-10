package org.aerovek.chartr.data.elrondsdk.usecase

import org.aerovek.chartr.data.repository.elrond.ElrondNetworkRepository


@Deprecated("DO NOT USE!!! This should be converted into its respective repository implementation")
class GetNetworkConfigUsecase internal constructor(
    private val networkRepository: ElrondNetworkRepository
) {
    fun execute() = networkRepository.getNetworkConfig()
}
