/*
The MIT License (MIT)

Copyright (c) 2023-present Aerovek

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
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