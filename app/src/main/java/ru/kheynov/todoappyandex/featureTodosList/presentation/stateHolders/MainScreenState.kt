package ru.kheynov.todoappyandex.featureTodosList.presentation.stateHolders

import ru.kheynov.todoappyandex.core.domain.entities.TodoItem

sealed interface MainScreenState {
    object Loading : MainScreenState
    data class Loaded(val data: List<TodoItem>) : MainScreenState
    object Empty : MainScreenState
}
