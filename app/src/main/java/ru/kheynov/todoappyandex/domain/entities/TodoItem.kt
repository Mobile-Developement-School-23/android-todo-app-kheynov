package ru.kheynov.todoappyandex.domain.entities

import java.time.LocalDateTime

data class TodoItem(
    val id: String,
    var text: String,
    var urgency: TodoUrgency,
    var deadline: LocalDateTime? = null,
    var isDone: Boolean,
    var createdAt: LocalDateTime,
    var editedAt: LocalDateTime? = null,
)
