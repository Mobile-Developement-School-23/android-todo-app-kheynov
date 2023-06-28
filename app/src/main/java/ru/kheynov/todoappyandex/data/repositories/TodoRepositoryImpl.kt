package ru.kheynov.todoappyandex.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import ru.kheynov.todoappyandex.core.BadRequestException
import ru.kheynov.todoappyandex.core.DuplicateItemException
import ru.kheynov.todoappyandex.core.OutOfSyncDataException
import ru.kheynov.todoappyandex.core.Resource
import ru.kheynov.todoappyandex.core.ServerSideException
import ru.kheynov.todoappyandex.core.TodoItemNotFoundException
import ru.kheynov.todoappyandex.core.UnauthorizedException
import ru.kheynov.todoappyandex.data.cache.TodoLocalDAO
import ru.kheynov.todoappyandex.data.network.dao.RemoteDataSource
import ru.kheynov.todoappyandex.data.mappers.toDomain
import ru.kheynov.todoappyandex.data.mappers.toLocalDTO
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
    } else {
        Resource.Failure(e)
    }
}

class TodoRepositoryImpl @Inject constructor(
    private val localDataSource: TodoLocalDAO,
    private val remoteDataSource: RemoteDataSource,
) : TodoItemsRepository {
    override val todos: Flow<List<TodoItem>>
        get() = localDataSource.getTodos().map {
            it.map { todo -> todo.toDomain() }
        }
    
    override suspend fun syncTodos(): Resource<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val remoteData = remoteDataSource.fetchTodos()
                remoteData.forEach { localDataSource.upsertTodo(it.toDomain().toLocalDTO()) }
                todos.last().let {
                    remoteDataSource.pushTodos(it)
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
                Resource.Success(Unit)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    
    override suspend fun getTodoById(id: String): Resource<TodoItem?> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val todo = localDataSource.getTodoById(id)?.toDomain()
                Resource.Success(todo)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    
    override suspend fun setTodoState(todoItem: TodoItem, state: Boolean): Resource<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                localDataSource.setTodoState(todoItem.id, state)
                remoteDataSource.setTodoState(todoItem, state)
                Resource.Success(Unit)
            } catch (e: Exception) {
                handleException(e)
            }
        }
}
