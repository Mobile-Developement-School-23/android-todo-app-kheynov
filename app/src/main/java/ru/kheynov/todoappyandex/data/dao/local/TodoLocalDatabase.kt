package ru.kheynov.todoappyandex.data.dao.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.kheynov.todoappyandex.core.Converters
import ru.kheynov.todoappyandex.data.model.local.TodoLocalDTO

@Database(
    entities = [TodoLocalDTO::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class TodoLocalDatabase : RoomDatabase() {
    abstract val dao: TodoLocalDAO
    
    companion object {
        const val DATABASE_NAME = "todos_db"
    }
}