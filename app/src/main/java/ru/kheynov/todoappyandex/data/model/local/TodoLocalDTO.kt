package ru.kheynov.todoappyandex.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kheynov.todoappyandex.domain.entities.TodoUrgency

@Entity(tableName = "todo")
data class TodoLocalDTO(
    @PrimaryKey val id: String,
    val text: String,
    val urgency: TodoUrgency,
    val deadline: Long? = null,
    val isDone: Boolean,
    val createdAt: Long,
    val editedAt: Long
)
