package ru.kheynov.todoappyandex.featureTodosList.presentation.stateHolders

import ru.kheynov.todoappyandex.core.ui.UiText

sealed interface MainScreenAction {
    data class NavigateToEditing(val id: String) : MainScreenAction
    object NavigateToAdding : MainScreenAction
    data class ToggleDoneTasks(val state: Boolean) : MainScreenAction
    data class ShowError(val text: UiText) : MainScreenAction
}
