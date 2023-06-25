package ru.kheynov.todoappyandex.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.kheynov.todoappyandex.domain.entities.TodoItem

interface TodoItemsRepository {
    val todos: Flow<List<TodoItem>>
    suspend fun addTodo(todo: TodoItem)
    suspend fun deleteTodo(id: String)
    suspend fun editTodo(todo: TodoItem)
    suspend fun getTodoById(id: String): TodoItem?
    suspend fun setTodoState(todoItem: TodoItem, state: Boolean)
}
