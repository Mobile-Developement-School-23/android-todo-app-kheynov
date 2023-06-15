package ru.kheynov.todoappyandex.presentation.editor.state_holders

import ru.kheynov.todoappyandex.core.UiText

sealed interface AddEditAction {
    object NavigateBack : AddEditAction
    object ShowDatePicker : AddEditAction
    data class ShowError(val error: UiText) : AddEditAction
}