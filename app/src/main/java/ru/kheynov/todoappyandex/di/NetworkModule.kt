package ru.kheynov.todoappyandex.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import ru.kheynov.todoappyandex.core.data.network.interceptors.TokenInterceptor

@OptIn(ExperimentalSerializationApi::class)
@Module
interface NetworkModule {
    companion object {
        private const val BASE_URL = "https://beta.mrdekk.ru/todobackend/"
        
        @Provides
        @AppScope
        fun provideOkHttpClient(): OkHttpClient =
            OkHttpClient.Builder().addInterceptor(TokenInterceptor()).build()
        
        @Provides
        @AppScope
        fun provideRetrofit(
            httpClient: OkHttpClient,
            json: Json,
        ): Retrofit {
            val contentType = "application/json".toMediaType()
            return Retrofit.Builder()
                .addConverterFactory(json.asConverterFactory(contentType))
                .baseUrl(BASE_URL)
                .client(httpClient)
                .build()
        }
    }
}