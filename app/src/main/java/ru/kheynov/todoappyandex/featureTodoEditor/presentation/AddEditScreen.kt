package ru.kheynov.todoappyandex.featureTodoEditor.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders.AddEditState
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders.AddEditUiEvent

@Composable
fun AddEditScreen(
    state: State<AddEditState>,
    onEvent: (AddEditUiEvent) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        var switchState by remember {
            mutableStateOf(false)
        }
        Box(contentAlignment = Alignment.Center) {
            Switch(checked = switchState, onCheckedChange = { switchState = it })
        }
    }
}