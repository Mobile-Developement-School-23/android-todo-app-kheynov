package ru.kheynov.todoappyandex.core.data.util

import androidx.room.TypeConverter
import ru.kheynov.todoappyandex.core.domain.entities.TodoUrgency

/**
 * Converters for Room database
 */
class Converters {
    @TypeConverter
    fun toUrgency(value: String) = when (value) {
        "low" -> TodoUrgency.LOW
        "basic" -> TodoUrgency.STANDARD
        "important" -> TodoUrgency.HIGH
        else -> error("Unknown urgency")
    }

    @TypeConverter
    fun fromUrgency(value: TodoUrgency) = when (value) {
        TodoUrgency.LOW -> "low"
        TodoUrgency.STANDARD -> "basic"
        TodoUrgency.HIGH -> "important"
    }
}
