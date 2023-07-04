package ru.kheynov.todoappyandex.core.data.network.dao

import ru.kheynov.todoappyandex.core.data.cache.MetadataStorage
import ru.kheynov.todoappyandex.core.data.mappers.toRemoteDTO
import ru.kheynov.todoappyandex.core.data.model.remote.PushListToServerRequest
import ru.kheynov.todoappyandex.core.data.model.remote.TodoRemoteDTO
import ru.kheynov.todoappyandex.core.data.model.remote.UpsertTodoRequest
import ru.kheynov.todoappyandex.core.domain.entities.TodoItem
import javax.inject.Inject

/**
 * Remote data source
 */
class RemoteDataSource @Inject constructor(
    private val api: TodoAPI,
    private val metadata: MetadataStorage,
) {
    /**
     * Fetches todos from server
     * @return list of [TodoRemoteDTO]
     */
    suspend fun fetchTodos(): List<TodoRemoteDTO> {
        val res = api.getTodos()
        res.revision.let { metadata.saveRevision(it) }
        return res.todos
    }
    
    /**
     * Pushes todos to server
     * @param todos list of [TodoItem]
     */
    suspend fun pushTodos(todos: List<TodoItem>) {
        val response = api.syncTodosWithServer(
            revision = metadata.getRevision(),
            request = PushListToServerRequest(
                todos.map { it.toRemoteDTO(metadata.deviceId) }
            )
        )
        metadata.saveRevision(response.revision)
    }
    
    /**
     * Adds todo to server
     * @param todo [TodoItem]
     */
    suspend fun addTodo(todo: TodoItem) {
        val response = api.addTodo(
            revision = metadata.getRevision(),
            request = UpsertTodoRequest(todo.toRemoteDTO(metadata.deviceId))
        )
        metadata.saveRevision(response.revision)
    }
    
    /**
     * Deletes todo from server
     * @param id [String]
     */
    suspend fun deleteTodo(id: String) {
        val response = api.deleteTodoById(
            revision = metadata.getRevision(),
            id = id
        )
        metadata.saveRevision(response.revision)
    }
    
    /**
     * Edits todo on server
     * @param todo [TodoItem]
     */
    suspend fun editTodo(todo: TodoItem) {
        val response = api.updateTodoById(
            revision = metadata.getRevision(),
            request = UpsertTodoRequest(todo.toRemoteDTO(metadata.deviceId)),
            id = todo.id
        )
        metadata.saveRevision(response.revision)
    }
    
    /**
     * Sets todo state on server
     * @param todoItem [TodoItem]
     * @param state [Boolean]
     */
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
