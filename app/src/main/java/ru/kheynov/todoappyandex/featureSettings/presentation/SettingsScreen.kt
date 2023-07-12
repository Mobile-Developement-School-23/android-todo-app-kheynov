package ru.kheynov.todoappyandex.featureSettings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.core.ui.AppTheme
import ru.kheynov.todoappyandex.core.ui.LocalSpacing
import ru.kheynov.todoappyandex.core.ui.UiTheme
import ru.kheynov.todoappyandex.featureSettings.presentation.components.DropDownSelector
import ru.kheynov.todoappyandex.featureSettings.presentation.stateHolders.SettingsState
import ru.kheynov.todoappyandex.featureSettings.presentation.stateHolders.SettingsUiEvent


@Composable
fun SettingsScreen(
    state: State<SettingsState>,
    onEvent: (SettingsUiEvent) -> Unit,
) {
    Surface(
        modifier = Modifier.background(MaterialTheme.colors.background),
    ) {

        val spacing = LocalSpacing.current
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                modifier = Modifier
                    .clickable {
                        onEvent(SettingsUiEvent.NavigateBack)
                    }
                    .padding(spacing.spaceMedium)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null,
                )
                Text(
                    text = stringResource(id = R.string.back_button),
                    style = MaterialTheme.typography.body1,
                )
            }
            Divider(modifier = Modifier.padding(bottom = spacing.spaceMedium))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.app_theme),
                    style = MaterialTheme.typography.body1
                )
                DropDownSelector(
                    items = UiTheme.values(),
                    selectedItem = state.value.theme,
                    onItemSelected = { onEvent(SettingsUiEvent.ChangeTheme(it)) },
                )
            }
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    AppTheme {
        Surface(modifier = Modifier.height(200.dp)) {
            val state = remember {
                mutableStateOf(
                    SettingsState(
                        theme = UiTheme.SYSTEM
                    )
                )
            }
            SettingsScreen(
                state = state,
                onEvent = {
                    if (it is SettingsUiEvent.ChangeTheme) {
                        state.value = state.value.copy(theme = it.theme)
                    }
                }
            )
        }
    }
}