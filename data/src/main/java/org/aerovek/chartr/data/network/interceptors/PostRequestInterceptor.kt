package org.aerovek.chartr.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response

internal class PostRequestInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.addHeader("Content-Type", "application/json")
        return chain.proceed(requestBuilder.build())
    }
}