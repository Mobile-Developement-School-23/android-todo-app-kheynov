package ru.kheynov.todoappyandex.core.data.local

import android.content.SharedPreferences
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import javax.inject.Inject

private enum class MetadataKeys {
    ID_KEY,
    REV_KEY
}

private const val DEVICE_ID_LENGTH = 6

/**
 * Class that stores metadata such as device id and last known data revision in SharedPreferences
 * @param prefs SharedPreferences instance
 */
class MetadataStorage @Inject constructor(
    private val prefs: SharedPreferences,
) {
    val deviceId: String = prefs.getString(MetadataKeys.ID_KEY.name, null) ?: run {
        val id = UUID.randomUUID().toString().subSequence(0, DEVICE_ID_LENGTH).toString()
        saveToPreferences(id, MetadataKeys.ID_KEY)
        id
    }

    private val mutex = Mutex()

    private var lastKnownRevision: Int? = null

    suspend fun saveRevision(revision: Int) {
        mutex.withLock {
            lastKnownRevision = revision
            saveToPreferences(revision, MetadataKeys.REV_KEY)
        }
    }

    suspend fun getRevision(): Int {
        return mutex.withLock {
            lastKnownRevision ?: prefs.getInt(MetadataKeys.REV_KEY.name, 0).also {
                lastKnownRevision = it
            }
        }
    }

    private fun <T> saveToPreferences(value: T?, key: MetadataKeys) {
        val editor: SharedPreferences.Editor = prefs.edit()
        when (value) {
            is Int -> editor.putInt(key.name, value)
            is String -> editor.putString(key.name, value)
        }
        editor.apply()
    }
}
