package ru.kheynov.todoappyandex.featureSettings.presentation.stateHolders

import ru.kheynov.todoappyandex.core.ui.UiTheme

sealed interface SettingsUiEvent {
    data class ChangeTheme(val theme: UiTheme) : SettingsUiEvent
    object NavigateBack : SettingsUiEvent
}