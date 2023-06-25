package ru.kheynov.todoappyandex.core

import androidx.room.TypeConverter
import ru.kheynov.todoappyandex.domain.entities.TodoUrgency

class Converters {
    @TypeConverter
    fun toUrgency(value: String) = enumValueOf<TodoUrgency>(value)
    
    @TypeConverter
    fun fromUrgency(value: TodoUrgency) = value.name
}