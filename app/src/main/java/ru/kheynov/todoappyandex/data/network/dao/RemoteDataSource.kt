package ru.kheynov.todoappyandex.data.network.dao

import ru.kheynov.todoappyandex.data.cache.MetadataStorage
import ru.kheynov.todoappyandex.data.mappers.toRemoteDTO
import ru.kheynov.todoappyandex.data.model.remote.PushListToServerRequest
import ru.kheynov.todoappyandex.data.model.remote.TodoRemoteDTO
import ru.kheynov.todoappyandex.data.model.remote.UpsertTodoRequest
import ru.kheynov.todoappyandex.domain.entities.TodoItem
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val api: TodoAPI,
    private val metadata: MetadataStorage,
) {
    suspend fun fetchTodos(): List<TodoRemoteDTO> {
        val res = api.getTodos()
        res.revision.let { metadata.saveRevision(it) }
        return res.todos
    }
    
    suspend fun pushTodos(todos: List<TodoItem>) {
        val response = api.syncTodosWithServer(
            revision = metadata.getRevision(),
            request = PushListToServerRequest(
                todos.map { it.toRemoteDTO(metadata.deviceId) }
            )
        )
        metadata.saveRevision(response.revision)
    }
    
    suspend fun addTodo(todo: TodoItem) {
        val response = api.addTodo(
            revision = metadata.getRevision(),
            request = UpsertTodoRequest(todo.toRemoteDTO(metadata.deviceId))
        )
        metadata.saveRevision(response.revision)
    }
    
    suspend fun deleteTodo(id: String) {
        val response = api.deleteTodoById(
            revision = metadata.getRevision(),
            id = id
        )
        metadata.saveRevision(response.revision)
    }
    
    suspend fun editTodo(todo: TodoItem) {
        val response = api.updateTodoById(
            revision = metadata.getRevision(),
            request = UpsertTodoRequest(todo.toRemoteDTO(metadata.deviceId)),
            id = todo.id
        )
        metadata.saveRevision(response.revision)
    }
    
    suspend fun setTodoState(todoItem: TodoItem, state: Boolean) {
        val response = api.updateTodoById(
            revision = metadata.getRevision(),
            request = UpsertTodoRequest(
                todoItem.toRemoteDTO(metadata.deviceId).copy(done = state)
            ),
            id = todoItem.id
        )
        metadata.saveRevision(response.revision)
    }
}
