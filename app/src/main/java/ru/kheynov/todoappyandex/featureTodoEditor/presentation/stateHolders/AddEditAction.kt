package ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders

import ru.kheynov.todoappyandex.core.utils.UiText

sealed interface AddEditAction {
    object NavigateBack : AddEditAction
    data class ShowError(val text: UiText) : AddEditAction
}