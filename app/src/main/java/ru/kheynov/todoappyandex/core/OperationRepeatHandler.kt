package ru.kheynov.todoappyandex.core

import android.util.Log
import java.util.concurrent.atomic.AtomicInteger

class OperationRepeatHandler(
    val fallbackAction: suspend () -> Resource<Unit>,
) {
    private val repetitions = AtomicInteger(INITIAL_REPETITIONS)
    
    suspend fun <T> repeatOperation(
        repeat: Int = DEFAULT_REPETITIONS,
        block: suspend () -> Resource<T>,
    ): Resource<T> {
        while (repetitions.get() < repeat) {
            val res = block()
            if (res is Resource.Failure) {
                try {
                    val fb = fallbackAction()
                    Log.e("Handler", fb.toString())
                } catch (e: Exception) {
                    Log.e("Handler", e.message.toString())
                }
            } else {
                repetitions.set(INITIAL_REPETITIONS)
                return res
            }
            repetitions.incrementAndGet()
        }
        return Resource.Failure(UnableToPerformOperation())
    }
    
    companion object {
        const val INITIAL_REPETITIONS = 0
        const val DEFAULT_REPETITIONS = 3
    }
}

