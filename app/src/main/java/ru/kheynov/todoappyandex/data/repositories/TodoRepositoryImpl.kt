package ru.kheynov.todoappyandex.data.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.http.HTTP_BAD_REQUEST
import okhttp3.internal.http.HTTP_INTERNAL_SERVER_ERROR
import okhttp3.internal.http.HTTP_NOT_FOUND
import okhttp3.internal.http.HTTP_UNAUTHORIZED
import retrofit2.HttpException
import ru.kheynov.todoappyandex.core.BadRequestException
import ru.kheynov.todoappyandex.core.DuplicateItemException
import ru.kheynov.todoappyandex.core.NetworkException
import ru.kheynov.todoappyandex.core.OutOfSyncDataException
import ru.kheynov.todoappyandex.core.Resource
import ru.kheynov.todoappyandex.core.ServerSideException
import ru.kheynov.todoappyandex.core.TodoItemNotFoundException
import ru.kheynov.todoappyandex.core.UnauthorizedException
import ru.kheynov.todoappyandex.data.cache.TodoLocalDAO
import ru.kheynov.todoappyandex.data.mappers.toDomain
import ru.kheynov.todoappyandex.data.mappers.toLocalDTO
import ru.kheynov.todoappyandex.data.model.local.TodoLocalDTO
import ru.kheynov.todoappyandex.data.network.dao.RemoteDataSource
import ru.kheynov.todoappyandex.domain.entities.TodoItem
import ru.kheynov.todoappyandex.domain.repositories.TodoItemsRepository
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
                        
                        HTTP_INTERNAL_SERVER_ERROR -> ServerSideException()
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
    private val _todos: MutableStateFlow<List<TodoItem>> = MutableStateFlow(emptyList())
    override val todos: StateFlow<List<TodoItem>> = _todos.asStateFlow()
    
    init {
        CoroutineScope(Dispatchers.IO).launch {
            _todos.update { localDataSource.getTodos().map(TodoLocalDTO::toDomain) }
        }
    }
    
    /**
     * Synchronizes todos
     * Fetches todos from remote data source and pushes them to local data source
     * Than pushes merged local todos to remote data source
     * @return [Resource]
     */
    override suspend fun syncTodos(): Resource<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val remoteData = remoteDataSource.fetchTodos()
                remoteData.forEach { localDataSource.upsertTodo(it.toDomain().toLocalDTO()) }
                localDataSource
                    .getTodos()
                    .map { todos -> todos.toDomain() }
                    .let { todos ->
                        remoteDataSource.pushTodos(todos)
                        _todos.update { todos }
                    }
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
                _todos.update { localDataSource.getTodos().map { it.toDomain() } }
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
                _todos.update { localDataSource.getTodos().map { it.toDomain() } }
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
                localDataSource.upsertTodo(todo.toLocalDTO())
                remoteDataSource.editTodo(todo)
                _todos.update { localDataSource.getTodos().map { it.toDomain() } }
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
                localDataSource.setTodoState(todoItem.id, state)
                remoteDataSource.setTodoState(todoItem, state)
                _todos.update { localDataSource.getTodos().map { it.toDomain() } }
                Resource.Success(Unit)
            } catch (e: Exception) {
                handleException(e)
            }
        }
}
