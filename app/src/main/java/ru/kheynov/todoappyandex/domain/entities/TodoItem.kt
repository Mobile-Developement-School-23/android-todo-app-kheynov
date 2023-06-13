package ru.kheynov.todoappyandex.domain.entities

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

data class TodoItem(
    val id: Int,
    val text: String,
    val urgency: TodoUrgency,
    val deadline: LocalDateTime? = null,
    val isDone: Boolean,
    val createdAt: LocalDateTime,
    val editedAt: LocalDateTime? = null,
)
