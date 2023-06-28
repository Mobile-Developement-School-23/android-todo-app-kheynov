package ru.kheynov.todoappyandex.core

import android.content.SharedPreferences
import java.util.UUID
import javax.inject.Inject

private enum class KEYS {
    ID_KEY,
    REV_KEY
}

class MetadataStorage @Inject constructor(
    private val pref: SharedPreferences
) {
    val deviceId: String = pref.getString(KEYS.ID_KEY.name, null) ?: run {
        val id = UUID.randomUUID().toString().subSequence(0, 6).toString()
        saveToPreferences(id, KEYS.ID_KEY)
        id
    }

    var lastKnownRevision: Int = 0
        set(value) {
            field = value
            saveToPreferences(value, KEYS.REV_KEY)
        }
        get() = pref.getInt(KEYS.REV_KEY.name, 0)

    private fun <T> saveToPreferences(value: T?, key: KEYS) {
        val editor: SharedPreferences.Editor = pref.edit()
        when (value) {
            is Int -> editor.putInt(key.name, value)
            is String -> editor.putString(key.name, value)
        }
        editor.apply()
    }
}
