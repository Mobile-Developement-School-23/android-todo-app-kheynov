package ru.kheynov.todoappyandex.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import ru.kheynov.todoappyandex.data.cache.TodoLocalDatabase
import ru.kheynov.todoappyandex.data.network.dao.RemoteDataSource
import ru.kheynov.todoappyandex.data.network.dao.TodoAPI
import ru.kheynov.todoappyandex.data.network.interceptors.TokenInterceptor
import ru.kheynov.todoappyandex.data.repositories.TodoRepositoryImpl
import ru.kheynov.todoappyandex.domain.repositories.TodoItemsRepository
import javax.inject.Singleton

@OptIn(ExperimentalSerializationApi::class)
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://beta.mrdekk.ru/todobackend/"

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(TokenInterceptor()).build()

    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory(contentType))
            .baseUrl(BASE_URL)
            .client(httpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(
        app: Application
    ): SharedPreferences =
        app.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideTodosApi(retrofit: Retrofit): TodoAPI = retrofit.create(TodoAPI::class.java)

    @Provides
    fun provideConnectivityManager(@ApplicationContext context: Context) =
        context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager

    @Provides
    @Singleton
    fun provideTodoDatabase(app: Application): TodoLocalDatabase {
        return Room.databaseBuilder(
            app,
            TodoLocalDatabase::class.java,
            TodoLocalDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideRepository(db: TodoLocalDatabase, remote: RemoteDataSource): TodoItemsRepository {
        return TodoRepositoryImpl(db.dao, remote)
    }
}
