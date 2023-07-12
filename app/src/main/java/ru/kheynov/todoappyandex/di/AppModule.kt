package ru.kheynov.todoappyandex.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import ru.kheynov.todoappyandex.core.data.cache.TodoLocalDatabase
import ru.kheynov.todoappyandex.core.data.network.dao.RemoteDataSource
import ru.kheynov.todoappyandex.core.data.network.dao.TodoAPI
import ru.kheynov.todoappyandex.core.data.repositories.TodoRepositoryImpl
import ru.kheynov.todoappyandex.core.domain.repositories.TodoItemsRepository

@OptIn(ExperimentalSerializationApi::class)
@Module
interface AppModule {
    companion object {

        @Provides
        fun provideJsonSerializer(): Json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }

        @Provides
        @AppScope
        fun provideTodosApi(retrofit: Retrofit): TodoAPI = retrofit.create(TodoAPI::class.java)

        @Provides
        @AppScope
        fun provideSharedPreferences(
            app: Application,
        ): SharedPreferences =
            app.getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)

        @Provides
        @Reusable
        fun provideConnectivityManager(context: Context) =
            context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager

        @Provides
        @AppScope
        fun provideTodoDatabase(app: Application): TodoLocalDatabase =
            Room.databaseBuilder(
                app,
                TodoLocalDatabase::class.java,
                TodoLocalDatabase.DATABASE_NAME
            ).build()

        @Provides
        @AppScope
        fun provideRepository(
            db: TodoLocalDatabase,
            remote: RemoteDataSource,
        ): TodoItemsRepository = TodoRepositoryImpl(
            localDataSource = db.dao,
            remoteDataSource = remote
        )
    }
}