package ru.kheynov.todoappyandex.core.data.model.remote

import kotlinx.serialization.Serializable

/**
 * API request for [TodoRemoteDTO]
 */
@Serializable
data class PushListToServerRequest(
    val list: List<TodoRemoteDTO>,
)
