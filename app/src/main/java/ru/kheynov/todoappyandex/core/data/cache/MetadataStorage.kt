package ru.kheynov.todoappyandex.core.data.cache

import android.content.SharedPreferences
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import javax.inject.Inject

private enum class KEYS {
    ID_KEY,
    REV_KEY
}

private const val DEVICE_ID_LENGTH = 6

/**
 * Class that stores metadata such as device id and last known data revision in SharedPreferences
 * @param pref SharedPreferences instance
 */
class MetadataStorage @Inject constructor(
    private val pref: SharedPreferences,
) {
    val deviceId: String = pref.getString(KEYS.ID_KEY.name, null) ?: run {
        val id = UUID.randomUUID().toString().subSequence(0, DEVICE_ID_LENGTH).toString()
        saveToPreferences(id, KEYS.ID_KEY)
        id
    }

    private val mutex = Mutex()

    private var lastKnownRevision: Int? = null

    suspend fun saveRevision(revision: Int) {
        mutex.withLock {
            lastKnownRevision = revision
            saveToPreferences(revision, KEYS.REV_KEY)
        }
    }

    suspend fun getRevision(): Int {
        return mutex.withLock {
            lastKnownRevision ?: pref.getInt(KEYS.REV_KEY.name, 0).also {
                lastKnownRevision = it
            }
        }
    }

    private fun <T> saveToPreferences(value: T?, key: KEYS) {
        val editor: SharedPreferences.Editor = pref.edit()
        when (value) {
            is Int -> editor.putInt(key.name, value)
            is String -> editor.putString(key.name, value)
        }
        editor.apply()
    }
}
