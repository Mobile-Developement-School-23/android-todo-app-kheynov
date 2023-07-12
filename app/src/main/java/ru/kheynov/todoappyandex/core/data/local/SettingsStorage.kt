package ru.kheynov.todoappyandex.core.data.local

import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.kheynov.todoappyandex.core.ui.UiTheme
import ru.kheynov.todoappyandex.di.AppScope
import javax.inject.Inject

private enum class SettingsKeys {
    THEME_KEY,
}

@AppScope
class SettingsStorage @Inject constructor(
    private val prefs: SharedPreferences
) {

    private val mutex = Mutex()

    private var theme: UiTheme? = null

    private val _themeObservable: MutableStateFlow<UiTheme?> = MutableStateFlow(theme)
    val themeObservable: StateFlow<UiTheme?> = _themeObservable.asStateFlow()

    suspend fun saveTheme(theme: UiTheme) {
        mutex.withLock {
            this.theme = theme
            saveToPreferences(theme.value, SettingsKeys.THEME_KEY)
            _themeObservable.update { theme }
        }
    }

    suspend fun getTheme(): UiTheme {
        return mutex.withLock {
            theme ?: UiTheme.parseTheme(
                prefs.getInt(
                    SettingsKeys.THEME_KEY.name,
                    UiTheme.SYSTEM.value
                )
            ).also { theme ->
                this.theme = theme
                _themeObservable.update { theme }
            }
        }
    }

    private fun <T> saveToPreferences(value: T?, key: SettingsKeys) {
        val editor: SharedPreferences.Editor = prefs.edit()
        when (value) {
            is Int -> editor.putInt(key.name, value)
            is String -> editor.putString(key.name, value)
        }
        editor.apply()
    }
}