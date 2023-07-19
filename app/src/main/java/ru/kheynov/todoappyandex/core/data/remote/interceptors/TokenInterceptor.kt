package ru.kheynov.todoappyandex.core.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import ru.kheynov.todoappyandex.BuildConfig

/**
 * Interceptor for adding token to request
 */
class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = BuildConfig.token
        val newRequest =
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        return chain.proceed(newRequest)
    }
}
