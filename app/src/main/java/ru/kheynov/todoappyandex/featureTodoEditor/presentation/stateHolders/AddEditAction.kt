package ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders

import ru.kheynov.todoappyandex.core.ui.UiText

sealed interface AddEditAction {
    object NavigateBack : AddEditAction
    object ShowDatePicker : AddEditAction
    data class ShowError(val text: UiText) : AddEditAction
}