package ru.kheynov.todoappyandex.data.dao.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.kheynov.todoappyandex.data.model.local.TodoLocalDTO

@Dao
interface TodoLocalDAO {
    @Query("SELECT * FROM todo")
    fun getTodos(): Flow<List<TodoLocalDTO>>

    @Query("SELECT * FROM todo WHERE id = :id")
    suspend fun getTodoById(id: String): TodoLocalDTO?

    @Upsert
    suspend fun upsertTodo(todo: TodoLocalDTO)

    @Query("DELETE FROM todo WHERE id = :id")
    suspend fun deleteTodoById(id: String)

    @Query("UPDATE todo SET isDone = :state WHERE id = :id")
    suspend fun setTodoState(id: String, state: Boolean)
}
