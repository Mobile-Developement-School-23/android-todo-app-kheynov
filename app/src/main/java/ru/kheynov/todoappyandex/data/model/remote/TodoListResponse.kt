package ru.kheynov.todoappyandex.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoListResponse(
    val revision: Int,
    val status: String,

    @SerialName("list")
    val todos: List<TodoRemoteDTO>
)