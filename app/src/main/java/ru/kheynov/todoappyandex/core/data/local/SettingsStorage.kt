package ru.kheynov.todoappyandex.core.data.local

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.kheynov.todoappyandex.core.ui.UiTheme
import ru.kheynov.todoappyandex.di.AppScope
import javax.inject.Inject

private enum class SettingsKeys {
    THEME_KEY,
    NOTIFICATIONS_KEY
}

@AppScope
class SettingsStorage @Inject constructor(
    private val prefs: SharedPreferences,
) {

    var theme: UiTheme = UiTheme.SYSTEM
        get() =
            UiTheme.parseTheme(
                prefs.getInt(
                    SettingsKeys.THEME_KEY.name,
                    UiTheme.SYSTEM.value
                )
            )
        set(value) {
            saveToPreferences(value.value, SettingsKeys.THEME_KEY)
            _themeFlow.update { value }
            field = value
        }

    var notificationsEnabled: Boolean = false
        get() = prefs.getBoolean(SettingsKeys.NOTIFICATIONS_KEY.name, false)
        set(value) {
            saveToPreferences(value, SettingsKeys.NOTIFICATIONS_KEY)
            _notificationsFlow.update { value }
            field = value
        }

    private val _themeFlow: MutableStateFlow<UiTheme> = MutableStateFlow(theme)
    val themeFlow: StateFlow<UiTheme?> = _themeFlow.asStateFlow()

    private val _notificationsFlow = MutableStateFlow(notificationsEnabled)
    val notificationsFlow: StateFlow<Boolean> = _notificationsFlow.asStateFlow()

    private fun <T> saveToPreferences(value: T?, key: SettingsKeys) {
        val editor: SharedPreferences.Editor = prefs.edit()
        when (value) {
            is Int -> editor.putInt(key.name, value)
            is String -> editor.putString(key.name, value)
            is Boolean -> editor.putBoolean(key.name, value)
            else -> error("Unspecified pref type")
        }
        editor.apply()
    }
}