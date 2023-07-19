package ru.kheynov.todoappyandex.core.ui

import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import ru.kheynov.todoappyandex.R

enum class UiTheme(val value: Int, val title: UiText) {
    DARK(
        MODE_NIGHT_YES,
        UiText.StringResource(R.string.dark_theme_name),
    ),
    LIGHT(
        MODE_NIGHT_NO,
        UiText.StringResource(R.string.light_theme_name),
    ),
    SYSTEM(
        MODE_NIGHT_FOLLOW_SYSTEM,
        UiText.StringResource(R.string.system_theme_name),
    );

    companion object {
        fun parseTheme(value: Int) =
            when (value) {
                MODE_NIGHT_YES -> DARK
                MODE_NIGHT_NO -> LIGHT
                MODE_NIGHT_FOLLOW_SYSTEM -> SYSTEM
                else -> error("Unable to parse theme")
            }
    }
}