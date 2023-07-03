package ru.kheynov.todoappyandex.presentation.todos.stateHolders

import ru.kheynov.todoappyandex.core.UiText

/**
 * Action state holder for MainScreen
 */
sealed interface MainScreenAction {
    data class NavigateToEditing(val id: String) : MainScreenAction
    object NavigateToAdding : MainScreenAction
    data class ToggleDoneTasks(val state: Boolean) : MainScreenAction
    data class ShowError(val text: UiText) : MainScreenAction
}
