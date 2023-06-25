package ru.kheynov.todoappyandex.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import ru.kheynov.todoappyandex.core.BadRequestException
import ru.kheynov.todoappyandex.core.MetadataStorage
import ru.kheynov.todoappyandex.core.OutOfSyncDataException
import ru.kheynov.todoappyandex.core.Resource
import ru.kheynov.todoappyandex.core.TodoItemNotFoundException
import ru.kheynov.todoappyandex.core.UnauthorizedException
import ru.kheynov.todoappyandex.data.dao.remote.TodoAPI
import ru.kheynov.todoappyandex.data.mappers.toDomain
import ru.kheynov.todoappyandex.data.mappers.toRemoteDTO
import ru.kheynov.todoappyandex.data.model.remote.UpsertTodoRequest
import ru.kheynov.todoappyandex.domain.entities.TodoItem
import ru.kheynov.todoappyandex.domain.repositories.TodoItemsRepository
import javax.inject.Inject

class RemoteTodoRepository @Inject constructor(
    private val api: TodoAPI,
    private val metadata: MetadataStorage
) : TodoItemsRepository {
    private val _todos: MutableStateFlow<List<TodoItem>> = MutableStateFlow(emptyList())
    override val todos: Flow<List<TodoItem>> = _todos.asStateFlow()

    override suspend fun addTodo(todo: TodoItem): Resource<Unit> {
        return try {
            val response = api.addTodo(
                revision = metadata.lastKnownRevision,
                request = UpsertTodoRequest(todo.toRemoteDTO(metadata.deviceId))
            )
            metadata.lastKnownRevision = response.revision
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is HttpException) {
                Resource.Failure(
                    e.response()?.errorBody()?.string().toString()
                        .let { message ->
                            when (e.code()) {
                                400 -> {
                                    if (message.contains("unsynchronized")) {
                                        OutOfSyncDataException()
                                    } else {
                                        BadRequestException()
                                    }
                                }

                                401 -> UnauthorizedException()
                                else -> e
                            }
                        }
                )
            } else {
                Resource.Failure(e)
            }
        }
    }

    override suspend fun deleteTodo(id: String): Resource<Unit> {
        return try {
            val response = api.deleteTodoById(
                revision = metadata.lastKnownRevision,
                id = id
            )
            metadata.lastKnownRevision = response.revision
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is HttpException) {
                Resource.Failure(
                    e.response()?.errorBody()?.string().toString()
                        .let { message ->
                            when (e.code()) {
                                400 -> {
                                    if (message.contains("unsynchronized")) {
                                        OutOfSyncDataException()
                                    } else {
                                        BadRequestException()
                                    }
                                }

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
    }

    override suspend fun editTodo(todo: TodoItem): Resource<Unit> {
        return try {
            val response = api.updateTodoById(
                revision = metadata.lastKnownRevision,
                request = UpsertTodoRequest(todo.toRemoteDTO(metadata.deviceId)),
                id = todo.id
            )
            metadata.lastKnownRevision = response.revision
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is HttpException) {
                Resource.Failure(
                    e.response()?.errorBody()?.string().toString()
                        .let { message ->
                            when (e.code()) {
                                400 -> {
                                    if (message.contains("unsynchronized")) {
                                        OutOfSyncDataException()
                                    } else {
                                        BadRequestException()
                                    }
                                }

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
    }

    override suspend fun getTodoById(id: String): Resource<TodoItem> {
        return try {
            val response = api.getTodoById(id)
            metadata.lastKnownRevision = response.revision
            Resource.Success(response.todo.toDomain())
        } catch (e: Exception) {
            if (e is HttpException) {
                Resource.Failure(
                    e.response()?.errorBody()?.string().toString()
                        .let { message ->
                            when (e.code()) {
                                400 -> {
                                    if (message.contains("unsynchronized")) {
                                        OutOfSyncDataException()
                                    } else {
                                        BadRequestException()
                                    }
                                }

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
    }

    override suspend fun setTodoState(todoItem: TodoItem, state: Boolean): Resource<Unit> {
        return try {
            val response = api.updateTodoById(
                revision = metadata.lastKnownRevision,
                request = UpsertTodoRequest(
                    todoItem.toRemoteDTO(metadata.deviceId).copy(done = state)
                ),
                id = todoItem.id
            )
            metadata.lastKnownRevision = response.revision
            Resource.Success(Unit)
        } catch (e: Exception) {
            if (e is HttpException) {
                Resource.Failure(
                    e.response()?.errorBody()?.string().toString()
                        .let { message ->
                            when (e.code()) {
                                400 -> {
                                    if (message.contains("unsynchronized")) {
                                        OutOfSyncDataException()
                                    } else {
                                        BadRequestException()
                                    }
                                }

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
    }
}
