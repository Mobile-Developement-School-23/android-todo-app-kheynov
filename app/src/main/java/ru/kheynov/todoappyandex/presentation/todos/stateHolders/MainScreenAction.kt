package ru.kheynov.todoappyandex.presentation.todos.stateHolders

sealed interface MainScreenAction {
    data class NavigateToEditing(val id: String) : MainScreenAction
    object NavigateToAdding : MainScreenAction
    data class ToggleDoneTasks(val state: Boolean) : MainScreenAction
}