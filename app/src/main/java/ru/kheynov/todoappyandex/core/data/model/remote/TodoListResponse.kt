package ru.kheynov.todoappyandex.core.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * API response for List<[TodoRemoteDTO]>
 */
@Serializable
data class TodoListResponse(
    val revision: Int,
    val status: String,

    @SerialName("list")
    val todos: List<TodoRemoteDTO>,
)
