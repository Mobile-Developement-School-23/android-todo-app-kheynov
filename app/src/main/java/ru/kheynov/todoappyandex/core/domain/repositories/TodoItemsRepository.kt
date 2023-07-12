package ru.kheynov.todoappyandex.core.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.kheynov.todoappyandex.core.domain.entities.TodoItem
import ru.kheynov.todoappyandex.core.utils.Resource

/**
 * Todo repository interface
 */
interface TodoItemsRepository {
    val todos: Flow<List<TodoItem>>
    suspend fun syncTodos(): Resource<Unit>
    suspend fun addTodo(todo: TodoItem): Resource<Unit>
    suspend fun deleteTodo(id: String): Resource<Unit>
    suspend fun editTodo(todo: TodoItem): Resource<Unit>
    suspend fun getTodoById(id: String): Resource<TodoItem>
    suspend fun setTodoState(todoItem: TodoItem, state: Boolean): Resource<Unit>
}
