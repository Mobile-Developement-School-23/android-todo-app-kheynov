package ru.kheynov.todoappyandex.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.kheynov.todoappyandex.core.Resource
import ru.kheynov.todoappyandex.domain.entities.TodoItem

interface TodoItemsRepository {
    val todos: Flow<List<TodoItem>>
    suspend fun addTodo(todo: TodoItem): Resource<Unit>
    suspend fun deleteTodo(id: String): Resource<Unit>
    suspend fun editTodo(todo: TodoItem): Resource<Unit>
    suspend fun getTodoById(id: String): Resource<TodoItem?>
    suspend fun setTodoState(todoItem: TodoItem, state: Boolean): Resource<Unit>
}
