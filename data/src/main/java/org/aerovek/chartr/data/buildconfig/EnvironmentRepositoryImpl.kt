package org.aerovek.chartr.data.buildconfig

import org.aerovek.chartr.data.network.ElrondNetwork

class EnvironmentRepositoryImpl(private val buildConfigProvider: BuildConfigProvider): EnvironmentRepository {
    override val selectedElrondEnvironment: ElrondNetwork
        get() = if (buildConfigProvider.isProductionBuild) {
            ElrondNetwork.MainNet
        } else {
            ElrondNetwork.DevNet
        }
    override val selectedPrimeTrustEnvironment: String
        get() = if (buildConfigProvider.isProductionBuild) {
            "TODO when we're ready for PROD"
        } else {
            PRIMETRUST_SANDBOX_URL
        }
    override val selectedPlaidEnvironment: String
        get() = if (buildConfigProvider.isProductionBuild) {
            "TODO when we're ready for PROD"
        } else {
            PLAID_SANDBOX_URL
        }
    /** Url to the aerovek nodejs backend */
    override val selectedAerovekEnvironment: String
        get() = AEROVEK_RESTFUL_URL

    companion object {
        private const val PLAID_SANDBOX_URL = "https://sandbox.plaid.com"
        private const val PRIMETRUST_SANDBOX_URL = "https://sandbox.primetrust.com"
        private const val AEROVEK_RESTFUL_URL = "https://restful.aerovek.com:8000"
    }
}