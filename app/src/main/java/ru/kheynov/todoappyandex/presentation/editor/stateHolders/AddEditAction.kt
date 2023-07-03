package ru.kheynov.todoappyandex.presentation.editor.stateHolders

import ru.kheynov.todoappyandex.core.UiText

/**
 * Action state holder for AddEditScreen
 */
sealed interface AddEditAction {
    object NavigateBack : AddEditAction
    object ShowDatePicker : AddEditAction
    data class ShowError(val text: UiText) : AddEditAction
}
