package ru.kheynov.todoappyandex.core

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    data class PlainText(val value: String) : UiText()
    class StringResource(@StringRes val resId: Int) : UiText()
    
    fun Context.getText(): String {
        return when (this@UiText) {
            is PlainText -> value
            is StringResource -> getString(resId)
        }
    }
}