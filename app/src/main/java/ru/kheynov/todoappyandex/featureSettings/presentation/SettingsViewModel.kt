package ru.kheynov.todoappyandex.featureSettings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kheynov.todoappyandex.core.data.local.SettingsStorage
import ru.kheynov.todoappyandex.core.ui.UiTheme
import ru.kheynov.todoappyandex.featureSettings.presentation.stateHolders.SettingsAction
import ru.kheynov.todoappyandex.featureSettings.presentation.stateHolders.SettingsState
import ru.kheynov.todoappyandex.featureSettings.presentation.stateHolders.SettingsUiEvent
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val settingsStorage: SettingsStorage,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState(UiTheme.SYSTEM))
    val state = _state.asStateFlow()

    private val _action = Channel<SettingsAction>(Channel.BUFFERED)
    val action = _action.receiveAsFlow()

    init {
        viewModelScope.launch {
            _state.update { _state.value.copy(theme = settingsStorage.getTheme()) }
        }
    }

    fun handleEvent(event: SettingsUiEvent) {
        viewModelScope.launch {
            when (event) {
                is SettingsUiEvent.ChangeTheme -> {
                    settingsStorage.saveTheme(event.theme)
                    _state.update { _state.value.copy(theme = event.theme) }
                }

                SettingsUiEvent.NavigateBack -> _action.send(SettingsAction.NavigateBack)
            }
        }
    }
}