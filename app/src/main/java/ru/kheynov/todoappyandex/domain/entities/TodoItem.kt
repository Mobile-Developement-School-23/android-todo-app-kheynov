package ru.kheynov.todoappyandex.domain.entities

import java.time.LocalDate
import java.time.LocalDateTime

data class TodoItem(
    val id: String,
    var text: String,
    var urgency: TodoUrgency,
    var deadline: LocalDate? = null,
    var isDone: Boolean,
    var color: String? = null,
    var createdAt: LocalDateTime,
    var editedAt: LocalDateTime? = null
)
