package ru.kheynov.todoappyandex.data.model.remote

import kotlinx.serialization.Serializable

/**
 * API request for [TodoRemoteDTO]
 */
@Serializable
data class PushListToServerRequest(
    val list: List<TodoRemoteDTO>,
)
