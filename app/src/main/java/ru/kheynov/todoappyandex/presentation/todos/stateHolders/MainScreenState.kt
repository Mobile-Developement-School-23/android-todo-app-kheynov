package ru.kheynov.todoappyandex.presentation.todos.stateHolders

import ru.kheynov.todoappyandex.domain.entities.TodoItem

/**
 * State holder for MainScreen
 */
sealed interface MainScreenState {
    object Loading : MainScreenState
    data class Loaded(val data: List<TodoItem>) : MainScreenState
    object Empty : MainScreenState
}
