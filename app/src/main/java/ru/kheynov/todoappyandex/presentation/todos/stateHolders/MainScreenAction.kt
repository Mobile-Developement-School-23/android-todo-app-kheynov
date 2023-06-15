package ru.kheynov.todoappyandex.presentation.todos.stateHolders

sealed interface MainScreenAction {
    data class RouteToEdit(val id: String) : MainScreenAction
    object RouteToAdd : MainScreenAction
    data class ToggleDoneTasks(val state: Boolean) : MainScreenAction
}