package ru.kheynov.todoappyandex.data.util

import androidx.room.TypeConverter
import ru.kheynov.todoappyandex.domain.entities.TodoUrgency

class Converters {
    @TypeConverter
    fun toUrgency(value: String) = when (value) {
        "low" -> TodoUrgency.LOW
        "basic" -> TodoUrgency.STANDARD
        "important" -> TodoUrgency.HIGH
        else -> throw IllegalStateException("Unknown urgency")
    }
    
    @TypeConverter
    fun fromUrgency(value: TodoUrgency) = when (value) {
        TodoUrgency.LOW -> "low"
        TodoUrgency.STANDARD -> "basic"
        TodoUrgency.HIGH -> "important"
    }
}