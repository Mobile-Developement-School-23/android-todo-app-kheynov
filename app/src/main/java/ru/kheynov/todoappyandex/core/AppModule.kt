package ru.kheynov.todoappyandex.core

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.kheynov.todoappyandex.data.dao.local.TodoLocalDatabase
import ru.kheynov.todoappyandex.data.repositories.LocalTodoRepository
import ru.kheynov.todoappyandex.domain.repositories.TodoItemsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
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
    fun provideLocalRepository(db: TodoLocalDatabase): TodoItemsRepository {
        return LocalTodoRepository(db.dao)
    }
}