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
package org.aerovek.chartr.data.di

import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.aerovek.chartr.data.buildconfig.EnvironmentRepository
import org.aerovek.chartr.data.buildconfig.EnvironmentRepositoryImpl
import org.aerovek.chartr.data.network.*
import org.aerovek.chartr.data.network.AeroPlaidService
import org.aerovek.chartr.data.network.PrimeTrustServiceImpl
import org.aerovek.chartr.data.network.interceptors.PostRequestInterceptor
import org.aerovek.chartr.data.repository.AeroPlaidRepository
import org.aerovek.chartr.data.repository.AeroPlaidRepositoryImpl
import org.aerovek.chartr.data.repository.PrimeTrustRepository
import org.aerovek.chartr.data.repository.PrimeTrustRepositoryImpl
import org.aerovek.chartr.data.repository.elrond.*
import org.aerovek.chartr.data.repository.elrond.AccountRepositoryImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

object DataModules {
    val dataModule = module {
        single { Gson() }

        single<EnvironmentRepository> { EnvironmentRepositoryImpl(buildConfigProvider = get()) }
        single<PrimeTrustRepository> { PrimeTrustRepositoryImpl(primeTrustService = get())}
        single<AeroPlaidRepository> { AeroPlaidRepositoryImpl(aeroPlaidService = get())}
        single<AccountRepository> { AccountRepositoryImpl(elrondGatewayService = get(), elrondApiService = get()) }
        single<ElrondNetworkRepository> { ElrondNetworkRepositoryImpl(elrondGatewayService = get(), elrondApiService = get()) }
        single<TransactionRepository> { TransactionRepositoryImpl(elrondService = get()) }
        single<VmRepository> { VmRepositoryImpl(elrondService = get()) }
        single<EsdtRepository> { EsdtRepositoryImpl(elrondApiService = get(), elrondGatewayService = get(), vmRepository = get()) }
    }

    val networkModule = module {
        single<OkHttpClient>(named(KoinNamedNetwork.PrimeTrust)) {
            OkHttpClient.Builder().build()
        }
        single<OkHttpClient>(named(KoinNamedNetwork.ElrondApi)) {
            OkHttpClient.Builder().build()
        }
        single<OkHttpClient>(named(KoinNamedNetwork.ElrondGateway)) {
            OkHttpClient.Builder().build()
        }
        single<OkHttpClient>(named(KoinNamedNetwork.AeroNodeJs)) {
            OkHttpClient.Builder().build()
        }

        single { PostRequestInterceptor() }

        single<RestClient>(named(KoinNamedNetwork.PrimeTrust)) {
            providePrimeTrustRestClient(environmentRepository = get(), okHttpClient = get(named(KoinNamedNetwork.PrimeTrust)), gson = get())
        }
        single<RestClient>(named(KoinNamedNetwork.ElrondApi)) {
            provideElrondApiRestClient(environmentRepository = get(), okHttpClient = get(named(KoinNamedNetwork.ElrondApi)), gson = get())
        }
        single<RestClient>(named(KoinNamedNetwork.ElrondGateway)) {
            provideElrondGatewayRestClient(environmentRepository = get(), okHttpClient = get(named(KoinNamedNetwork.ElrondGateway)), gson = get())
        }
        single<RestClient>(named(KoinNamedNetwork.AeroNodeJs)) {
            provideAeroRestClient(environmentRepository = get(), okHttpClient = get(named(KoinNamedNetwork.AeroNodeJs)), gson = get())
        }

        single<PrimeTrustService> {
            PrimeTrustServiceImpl(restClient = get(named(KoinNamedNetwork.PrimeTrust)), gson = get())
        }
        single<ElrondApiService> {
            ElrondApiServiceImpl(restClient = get(named(KoinNamedNetwork.ElrondApi)), gson = get())
        }
        single<ElrondGatewayService> {
            ElrondGatewayServiceImpl(environmentRepository = get(), restClient = get(named(KoinNamedNetwork.ElrondGateway)), gson = get())
        }
        single<AeroPlaidService> {
            AeroPlaidServiceImpl(restClient = get(named(KoinNamedNetwork.AeroNodeJs)), gson = get())
        }
    }

    private fun provideElrondApiRestClient(environmentRepository: EnvironmentRepository, okHttpClient: OkHttpClient, gson: Gson): RestClient {
        return RestClient(httpClient = okHttpClient, baseUrl = environmentRepository.selectedElrondEnvironment.apiUrl, gson = gson)
    }
    private fun provideElrondGatewayRestClient(environmentRepository: EnvironmentRepository, okHttpClient: OkHttpClient, gson: Gson): RestClient {
        return RestClient(httpClient = okHttpClient, baseUrl = environmentRepository.selectedElrondEnvironment.gatewayUrl, gson = gson)
    }

    private fun providePrimeTrustRestClient(environmentRepository: EnvironmentRepository, okHttpClient: OkHttpClient, gson: Gson): RestClient {
        return RestClient(httpClient = okHttpClient, baseUrl = environmentRepository.selectedPrimeTrustEnvironment, gson = gson)
    }

    private fun provideAeroRestClient(environmentRepository: EnvironmentRepository, okHttpClient: OkHttpClient, gson: Gson): RestClient {
        return RestClient(httpClient = okHttpClient, baseUrl = environmentRepository.selectedAerovekEnvironment, gson = gson)
    }
}

/** This allows us to register different network configurations in the koin container, for example
 * injecting PrimeTrustService in a class will have the prime trust base URL configured on its rest client */
private enum class KoinNamedNetwork {
    ElrondApi, ElrondGateway, Plaid, PrimeTrust, AeroNodeJs
}
