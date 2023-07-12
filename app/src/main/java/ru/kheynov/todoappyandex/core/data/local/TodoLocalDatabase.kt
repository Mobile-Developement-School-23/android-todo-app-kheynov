package ru.kheynov.todoappyandex.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.kheynov.todoappyandex.core.data.model.local.TodoLocalDTO
import ru.kheynov.todoappyandex.core.data.local.converters.Converters

/**
 * The Room Database that contains the todos table.
 */
@Database(
    entities = [TodoLocalDTO::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TodoLocalDatabase : RoomDatabase() {
    abstract val dao: TodoLocalDAO

    companion object {
        const val DATABASE_NAME = "todos_db"
    }
}
