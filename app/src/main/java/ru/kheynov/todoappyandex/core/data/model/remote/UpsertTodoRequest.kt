package ru.kheynov.todoappyandex.core.data.model.remote

import kotlinx.serialization.Serializable

/**
 * API update request for [TodoRemoteDTO]
 */
@Serializable
data class UpsertTodoRequest(
    val element: TodoRemoteDTO,
)
