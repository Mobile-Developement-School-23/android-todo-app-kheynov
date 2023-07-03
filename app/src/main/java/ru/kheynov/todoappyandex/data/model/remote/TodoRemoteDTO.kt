package ru.kheynov.todoappyandex.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Remote DTO for todo
 */
@Serializable
data class TodoRemoteDTO(
    val id: String,
    val text: String,
    val importance: String,
    val done: Boolean,
    val color: String?,
    val deadline: Long?,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("changed_at") val updatedAt: Long,
    @SerialName("last_updated_by") val updatedBy: String,
)
