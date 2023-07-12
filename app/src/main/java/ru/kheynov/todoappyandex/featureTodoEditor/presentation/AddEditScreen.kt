package ru.kheynov.todoappyandex.featureTodoEditor.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.kheynov.todoappyandex.core.domain.entities.TodoItem
import ru.kheynov.todoappyandex.core.ui.AppTheme
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders.AddEditState
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders.AddEditUiEvent
import java.time.LocalDateTime
import java.util.UUID

@Composable
fun AddEditScreen(
    state: State<AddEditState>,
    onEvent: (AddEditUiEvent) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        var switchState by remember {
            mutableStateOf(false)
        }
        Box(contentAlignment = Alignment.Center) {
            Switch(checked = switchState, onCheckedChange = { switchState = it })
        }
    }
}

@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO, name = "Light")
@Preview(uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Dark")
@Composable
fun AddEditScreenPreview() {
    AppTheme {
        val state = remember {
            mutableStateOf(
                AddEditState(
                    todo = TodoItem(
                        id = UUID.randomUUID().toString(),
                        text = "Title",
                        urgency = ru.kheynov.todoappyandex.core.domain.entities.TodoUrgency.LOW,
                        isDone = false,
                        createdAt = LocalDateTime.parse("2020-01-01T00:00:00"),
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
                }
            })
    }
}