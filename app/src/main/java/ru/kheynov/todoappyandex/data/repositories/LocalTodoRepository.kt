package ru.kheynov.todoappyandex.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.kheynov.todoappyandex.core.Resource
import ru.kheynov.todoappyandex.data.dao.local.TodoLocalDAO
import ru.kheynov.todoappyandex.data.mappers.toDomain
import ru.kheynov.todoappyandex.data.mappers.toLocalDTO
import ru.kheynov.todoappyandex.data.model.local.TodoLocalDTO
import ru.kheynov.todoappyandex.domain.entities.TodoItem
import ru.kheynov.todoappyandex.domain.repositories.TodoItemsRepository

class LocalTodoRepository(
    private val dao: TodoLocalDAO
) : TodoItemsRepository {
    override val todos: Flow<List<TodoItem>>
        get() = dao.getTodos().map { it.map(TodoLocalDTO::toDomain) }

    override suspend fun addTodo(todo: TodoItem): Resource<Unit> =
        try {
            Resource.Success(dao.upsertTodo(todo.toLocalDTO()))
        } catch (e: Exception) {
            Resource.Failure(e)
        }

    override suspend fun deleteTodo(id: String): Resource<Unit> =
        try {
            Resource.Success(dao.deleteTodoById(id))
        } catch (e: Exception) {
            Resource.Failure(e)
        }

    override suspend fun editTodo(todo: TodoItem): Resource<Unit> =
        try {
            Resource.Success(dao.upsertTodo(todo.toLocalDTO()))
        } catch (e: Exception) {
            Resource.Failure(e)
        }

    override suspend fun getTodoById(id: String): Resource<TodoItem?> =
        try {
            Resource.Success(dao.getTodoById(id)?.let(TodoLocalDTO::toDomain))
        } catch (e: Exception) {
            Resource.Failure(e)
        }

    override suspend fun setTodoState(todoItem: TodoItem, state: Boolean) =
        try {
            Resource.Success(dao.setTodoState(todoItem.id, state))
        } catch (e: Exception) {
            Resource.Failure(e)
        }
}
