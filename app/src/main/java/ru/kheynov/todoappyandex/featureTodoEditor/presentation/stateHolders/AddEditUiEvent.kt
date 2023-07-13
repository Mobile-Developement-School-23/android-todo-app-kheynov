package ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders

import ru.kheynov.todoappyandex.core.domain.entities.TodoUrgency
import java.time.LocalDate

sealed interface AddEditUiEvent {
    data class ChangeTitle(val text: String) : AddEditUiEvent
    data class ChangeUrgency(val urgency: TodoUrgency) : AddEditUiEvent
    data class ChangeDeadline(val deadline: LocalDate?) : AddEditUiEvent
    object ShowDatePickerDialog : AddEditUiEvent
    object ClearDeadline : AddEditUiEvent
    object SaveTodo : AddEditUiEvent
    object DeleteTodo : AddEditUiEvent
    object Close : AddEditUiEvent
}