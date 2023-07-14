package ru.kheynov.todoappyandex.featureSettings.presentation.stateHolders

import ru.kheynov.todoappyandex.core.ui.UiTheme

sealed interface SettingsUiEvent {
    data class ChangeTheme(val theme: UiTheme) : SettingsUiEvent
    data class ToggleNotifications(val state: Boolean) : SettingsUiEvent
    object NavigateBack : SettingsUiEvent
}