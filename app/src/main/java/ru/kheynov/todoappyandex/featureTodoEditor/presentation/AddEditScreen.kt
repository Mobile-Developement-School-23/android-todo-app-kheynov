package ru.kheynov.todoappyandex.featureTodoEditor.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import ru.kheynov.todoappyandex.core.domain.entities.TodoItem
import ru.kheynov.todoappyandex.core.domain.entities.TodoUrgency
import ru.kheynov.todoappyandex.core.ui.AppTheme
import ru.kheynov.todoappyandex.core.ui.LocalSpacing
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.components.BottomSheetContent
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.components.DeadlineSelector
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.components.DeleteButton
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.components.EditorTopBar
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.components.TodoTitleInputField
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.components.UrgencySelector
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders.AddEditState
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders.AddEditUiEvent
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddEditScreen(
    state: State<AddEditState>,
    onEvent: (AddEditUiEvent) -> Unit,
) {
    val spacing = LocalSpacing.current

    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetShape = RoundedCornerShape(
                topStart = spacing.spaceMedium,
                topEnd = spacing.spaceMedium,
            ),
            sheetContent = {
                BottomSheetContent(
                    selected = state.value.todo.urgency,
                    onUrgencySelected = { urgency ->
                        onEvent(AddEditUiEvent.ChangeUrgency(urgency))
                    })
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
            ) {
                EditorTopBar(
                    onCloseClick = { onEvent(AddEditUiEvent.Close) },
                    onSaveClick = { onEvent(AddEditUiEvent.SaveTodo) },
                )

                TodoTitleInputField(
                    text = state.value.todo.text,
                    onChanged = { onEvent(AddEditUiEvent.ChangeTitle(it)) },
                )

                Spacer(modifier = Modifier.height(spacing.spaceMedium))

                UrgencySelector(
                    urgency = state.value.todo.urgency,
                    onClick = { scope.launch { sheetState.show() } },
                )

                Divider()

                DeadlineSelector(
                    deadline = state.value.todo.deadline,
                    clearDeadline = { onEvent(AddEditUiEvent.ClearDeadline) },
                    showDatePicker = { onEvent(AddEditUiEvent.ShowDatePickerDialog) },
                )

                Divider()

                DeleteButton(
                    enabled = state.value.isEditing,
                    onClick = { onEvent(AddEditUiEvent.DeleteTodo) },
                )
            }
        }
    }

}

@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO, name = "Light")
@Preview(
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
    name = "Dark (RU)",
    locale = "RU"
)
@Composable
fun AddEditScreenPreview() {
    AppTheme {
        val state = remember {
            mutableStateOf(
                AddEditState(
                    todo = TodoItem(
                        id = UUID.randomUUID().toString(),
                        text = "",
                        urgency = TodoUrgency.LOW,
                        isDone = false,
                        createdAt = LocalDateTime.parse("2020-01-01T00:00:00"),
                        deadline = LocalDate.now(),
                    ),
                    isEditing = true,
                )
            )
        }
        AddEditScreen(
            state = state, onEvent = {
                state.value = when (it) {
                    is AddEditUiEvent.ChangeDeadline -> state.value.copy(
                        todo = state.value.todo.copy(
                            deadline = it.deadline
                        )
                    )

                    is AddEditUiEvent.ChangeTitle -> state.value.copy(
                        todo = state.value.todo.copy(
                            text = it.text
                        )
                    )

                    is AddEditUiEvent.ChangeUrgency -> state.value.copy(
                        todo = state.value.todo.copy(
                            urgency = it.urgency
                        )
                    )

                    AddEditUiEvent.DeleteTodo -> state.value
                    AddEditUiEvent.SaveTodo -> state.value
                    AddEditUiEvent.Close -> state.value
                    AddEditUiEvent.ShowDatePickerDialog -> state.value.copy(
                        todo = state.value.todo.copy(
                            deadline = LocalDate.now()
                        )
                    )

                    AddEditUiEvent.ClearDeadline -> state.value.copy(
                        todo = state.value.todo.copy(
                            deadline = null
                        )
                    )
                }
            })
    }
}