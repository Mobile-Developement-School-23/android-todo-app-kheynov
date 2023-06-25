package ru.kheynov.todoappyandex.data.model.remote

import kotlinx.serialization.Serializable

@Serializable
data class UpsertTodoRequest(
    val element: TodoRemoteDTO
)
