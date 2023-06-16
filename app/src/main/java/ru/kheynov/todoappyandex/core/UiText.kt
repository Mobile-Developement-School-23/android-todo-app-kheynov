package ru.kheynov.todoappyandex.core

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    data class PlainText(val value: String) : UiText()
    class StringResource(@StringRes val resId: Int) : UiText()

    fun toString(context: Context): String {
        return when (this) {
            is PlainText -> value
            is StringResource -> context.getString(resId)
        }
    }
}
