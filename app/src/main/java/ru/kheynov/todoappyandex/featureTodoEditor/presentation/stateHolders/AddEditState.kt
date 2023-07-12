package ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders

import ru.kheynov.todoappyandex.core.domain.entities.TodoItem

data class AddEditState(
    val todo: TodoItem,
    val isEditing: Boolean = false,
)