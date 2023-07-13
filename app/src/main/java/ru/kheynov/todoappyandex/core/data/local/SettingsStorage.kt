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
}

@AppScope
class SettingsStorage @Inject constructor(
    private val prefs: SharedPreferences,
) {
    private val _themeObservable: MutableStateFlow<UiTheme> = MutableStateFlow(getTheme())
    val themeObservable: StateFlow<UiTheme?> = _themeObservable.asStateFlow()

    fun saveTheme(theme: UiTheme) {
        saveToPreferences(theme.value, SettingsKeys.THEME_KEY)
        _themeObservable.update { theme }
    }

    fun getTheme(): UiTheme = UiTheme.parseTheme(
        prefs.getInt(
            SettingsKeys.THEME_KEY.name,
            UiTheme.SYSTEM.value
        )
    )


    private fun <T> saveToPreferences(value: T?, key: SettingsKeys) {
        val editor: SharedPreferences.Editor = prefs.edit()
        when (value) {
            is Int -> editor.putInt(key.name, value)
            is String -> editor.putString(key.name, value)
        }
        editor.apply()
    }
}