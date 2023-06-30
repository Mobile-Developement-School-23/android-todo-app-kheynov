package ru.kheynov.todoappyandex.data.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                        400 -> when {
                            message.contains("unsynchronized") -> OutOfSyncDataException()
                            message.contains("duplicate") -> DuplicateItemException()
                            else -> BadRequestException()
                        }
                        
                        500 -> ServerSideException()
                        404 -> TodoItemNotFoundException()
                        401 -> UnauthorizedException()
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
    
    override suspend fun getTodoById(id: String): Resource<TodoItem> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val todo = localDataSource.getTodoById(id)?.toDomain()
                if (todo == null) Resource.Failure(TodoItemNotFoundException())
                else Resource.Success(todo)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    
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
