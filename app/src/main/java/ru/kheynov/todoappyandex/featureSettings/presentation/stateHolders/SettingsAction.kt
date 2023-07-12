package ru.kheynov.todoappyandex.featureSettings.presentation.stateHolders

sealed interface SettingsAction {
    object NavigateBack : SettingsAction
}