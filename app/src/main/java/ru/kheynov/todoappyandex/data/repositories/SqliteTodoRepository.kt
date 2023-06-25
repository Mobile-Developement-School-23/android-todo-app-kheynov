package ru.kheynov.todoappyandex.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.kheynov.todoappyandex.data.dao.local.TodoLocalDAO
import ru.kheynov.todoappyandex.data.mappers.toDomain
import ru.kheynov.todoappyandex.data.mappers.toLocalDTO
import ru.kheynov.todoappyandex.data.model.local.TodoLocalDTO
import ru.kheynov.todoappyandex.domain.entities.TodoItem
import ru.kheynov.todoappyandex.domain.repositories.TodoItemsRepository
import javax.inject.Inject

class LocalTodoRepository @Inject constructor(
    private val dao: TodoLocalDAO,
) : TodoItemsRepository {
    override val todos: Flow<List<TodoItem>>
        get() = dao.getTodos().map { it.map(TodoLocalDTO::toDomain) }
    
    override suspend fun addTodo(todo: TodoItem) = dao.upsertTodo(todo.toLocalDTO())
    override suspend fun deleteTodo(id: String) = dao.deleteTodoById(id)
    override suspend fun editTodo(todo: TodoItem) = dao.upsertTodo(todo.toLocalDTO())
    override suspend fun getTodoById(id: String): TodoItem? =
        dao.getTodoById(id)?.let(TodoLocalDTO::toDomain)
    
    override suspend fun setTodoState(todoItem: TodoItem, state: Boolean) =
        dao.setTodoState(todoItem.id, state)
}