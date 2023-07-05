package ru.kheynov.todoappyandex.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import ru.kheynov.todoappyandex.data.util.ApiToken

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = ApiToken.token
        val newRequest =
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        return chain.proceed(newRequest)
    }
}
