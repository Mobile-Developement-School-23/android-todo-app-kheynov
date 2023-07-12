package ru.kheynov.todoappyandex.core.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.internal.http.HTTP_BAD_GATEWAY
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_INTERNAL_SERVER_ERROR
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import ru.kheynov.todoappyandex.core.data.cache.TodoLocalDAO
import ru.kheynov.todoappyandex.core.data.mappers.toDomain
import ru.kheynov.todoappyandex.core.data.mappers.toLocalDTO
import ru.kheynov.todoappyandex.core.data.model.local.TodoLocalDTO
import ru.kheynov.todoappyandex.core.data.network.dao.RemoteDataSource
import ru.kheynov.todoappyandex.core.domain.entities.TodoItem
import ru.kheynov.todoappyandex.core.domain.repositories.TodoItemsRepository
import ru.kheynov.todoappyandex.core.utils.BadRequestException
import ru.kheynov.todoappyandex.core.utils.DuplicateItemException
import ru.kheynov.todoappyandex.core.utils.NetworkException
import ru.kheynov.todoappyandex.core.utils.Operation.ADD
import ru.kheynov.todoappyandex.core.utils.Operation.DELETE
import ru.kheynov.todoappyandex.core.utils.Operation.UPDATE
import ru.kheynov.todoappyandex.core.utils.OutOfSyncDataException
import ru.kheynov.todoappyandex.core.utils.Resource
import ru.kheynov.todoappyandex.core.utils.ServerSideException
import ru.kheynov.todoappyandex.core.utils.TodoItemNotFoundException
import ru.kheynov.todoappyandex.core.utils.UnauthorizedException
import ru.kheynov.todoappyandex.core.utils.mergeCacheAndRemote
import java.time.LocalDateTime
import javax.inject.Inject

private fun handleException(e: Exception): Resource.Failure {
    return if (e is HttpException) {
        Resource.Failure(
            e.response()?.errorBody()?.string().toString()
                .let { message ->
                    when (e.code()) {
                        HTTP_BAD_REQUEST -> when {
                            message.contains("unsynchronized") -> OutOfSyncDataException()
                            message.contains("duplicate") -> DuplicateItemException()
                            else -> BadRequestException()
                        }
                        
                        HTTP_INTERNAL_SERVER_ERROR, HTTP_BAD_GATEWAY -> ServerSideException()
                        HTTP_NOT_FOUND -> TodoItemNotFoundException()
                        HTTP_UNAUTHORIZED -> UnauthorizedException()
                        else -> e
                    }
                }
        )
    } else if (e.message?.let { it.contains("hostname") || it.contains("timeout") } == true) {
        Resource.Failure(NetworkException())
    } else {
        Resource.Failure(e)
    }
}

/**
 * Todo repository implementation
 * @property localDataSource [TodoLocalDAO]
 * @property remoteDataSource [RemoteDataSource]
 */
class TodoRepositoryImpl @Inject constructor(
    private val localDataSource: TodoLocalDAO,
    private val remoteDataSource: RemoteDataSource,
) : TodoItemsRepository {
    override val todos: Flow<List<TodoItem>> = localDataSource.observeTodos()
        .map { it.map(TodoLocalDTO::toDomain) }
    
    /**
     * Synchronizes todos
     * Fetches todos from remote data source and pushes them to local data source
     * Than pushes merged local todos to remote data source
     * @return [Resource]
     */
    override suspend fun syncTodos(): Resource<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val remoteData = remoteDataSource.fetchTodos().map { it.toDomain() }
                val localData = localDataSource.getTodos().map { it.toDomain() }
                val merged = mergeCacheAndRemote(
                    local = localData,
                    remote = remoteData,
                )
                merged.forEach {
                    when (it.operation) {
                        ADD, UPDATE -> localDataSource.upsertTodo(it.todo.toLocalDTO())
                        DELETE -> localDataSource.deleteTodoById(it.todo.id)
                    }
                }
                
                val res = localDataSource.getTodos().map { it.toDomain() }
                remoteDataSource.pushTodos(res)
                Resource.Success(Unit)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    
    
    /**
     * Adds todo
     * Adds todo to local data source and pushes it to remote data source
     * @param todo [TodoItem]
     * @return [Resource]
     */
    override suspend fun addTodo(todo: TodoItem): Resource<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                localDataSource.upsertTodo(todo.toLocalDTO())
                remoteDataSource.addTodo(todo)
                Resource.Success(Unit)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    
    /**
     * Deletes todo
     * Deletes todo from local data source and pushes it to remote data source
     * @param id [String]
     * @return [Resource]
     */
    override suspend fun deleteTodo(id: String): Resource<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                localDataSource.deleteTodoById(id)
                remoteDataSource.deleteTodo(id)
                Resource.Success(Unit)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    
    /**
     * Edits todo
     * Edits todo in local data source and pushes it to remote data source
     * @param todo [TodoItem]
     * @return [Resource]
     */
    override suspend fun editTodo(todo: TodoItem): Resource<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val newTodo = todo.copy(
                    editedAt = LocalDateTime.now()
                )
                localDataSource.upsertTodo(newTodo.toLocalDTO())
                remoteDataSource.editTodo(newTodo)
                Resource.Success(Unit)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    
    /**
     * Gets todo by id
     * @param id [String]
     * @return [Resource]
     */
    override suspend fun getTodoById(id: String): Resource<TodoItem> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val todo = localDataSource.getTodoById(id)?.toDomain()
                if (todo == null) {
                    Resource.Failure(TodoItemNotFoundException())
                } else {
                    Resource.Success(todo)
                }
            } catch (e: Exception) {
                handleException(e)
            }
        }
    
    /**
     * Sets todo state
     * Sets todo state in local data source and pushes it to remote data source
     * @param todoItem [TodoItem]
     * @param state [Boolean]
     * @return [Resource]
     */
    override suspend fun setTodoState(todoItem: TodoItem, state: Boolean): Resource<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val newTodo = todoItem.copy(
                    isDone = state,
                    editedAt = LocalDateTime.now()
                )
                localDataSource.upsertTodo(newTodo.toLocalDTO())
                remoteDataSource.editTodo(newTodo)
                Resource.Success(Unit)
            } catch (e: Exception) {
                handleException(e)
            }
        }
}
