package ru.kheynov.todoappyandex.core.data.remote.dao

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import ru.kheynov.todoappyandex.core.data.model.remote.PushListToServerRequest
import ru.kheynov.todoappyandex.core.data.model.remote.TodoItemResponse
import ru.kheynov.todoappyandex.core.data.model.remote.TodoListResponse
import ru.kheynov.todoappyandex.core.data.model.remote.UpsertTodoRequest

/**
 * Retrofit API interface
 */
interface TodoAPI {
    @GET("list")
    suspend fun getTodos(): TodoListResponse

    @PATCH("list")
    suspend fun syncTodosWithServer(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: PushListToServerRequest,
    ): TodoListResponse

    @POST("list")
    suspend fun addTodo(
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: UpsertTodoRequest,
    ): TodoItemResponse

    @PUT("list/{id}")
    suspend fun updateTodoById(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") revision: Int,
        @Body request: UpsertTodoRequest,
    ): TodoItemResponse

    @DELETE("list/{id}")
    suspend fun deleteTodoById(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") revision: Int,
    ): TodoItemResponse
}
