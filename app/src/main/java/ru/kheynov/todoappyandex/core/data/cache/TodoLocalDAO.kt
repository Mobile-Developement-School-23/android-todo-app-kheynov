package ru.kheynov.todoappyandex.core.data.cache

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.kheynov.todoappyandex.core.data.model.local.TodoLocalDTO

/**
 * Data Access Object for the SQLite todos table.
 */
@Dao
interface TodoLocalDAO {
    @Query("SELECT * FROM todo")
    fun observeTodos(): Flow<List<TodoLocalDTO>>
    
    @Query("SELECT * FROM todo")
    suspend fun getTodos(): List<TodoLocalDTO>
    
    @Query("SELECT * FROM todo WHERE id = :id limit 1")
    suspend fun getTodoById(id: String): TodoLocalDTO?
    
    @Upsert
    suspend fun upsertTodo(todo: TodoLocalDTO)
    
    @Query("DELETE FROM todo WHERE id = :id")
    suspend fun deleteTodoById(id: String)
}
