package ru.kheynov.todoappyandex.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoItemResponse(
    val revision: Int,
    val status: String,
    @SerialName("element") val todo: TodoRemoteDTO
)
