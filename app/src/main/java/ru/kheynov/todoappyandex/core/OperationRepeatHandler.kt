package ru.kheynov.todoappyandex.core

import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger

/**
 * [OperationRepeatHandler] repeats operation [repeat] times with [delay] between each attempt
 * @param fallbackAction action to perform if operation failed
 */

class OperationRepeatHandler(
    val fallbackAction: suspend () -> Resource<Unit>,
) {
    private val repetitions = AtomicInteger(INITIAL_REPETITIONS)
    
    /**
     * Repeats operation [repeat] times with [delay] between each attempt
     * @param repeat number of repetitions
     * @param delay delay between each attempt
     * @param exceptionsCatching list of exceptions to catch
     * @param block operation to perform
     * @return [Resource] with result of operation
     */
    suspend fun <T> repeatOperation(
        repeat: Int = DEFAULT_REPETITIONS,
        delay: Long = 0,
        exceptionsCatching: List<Exception> = listOf(
            OutOfSyncDataException(),
            ServerSideException()
        ),
        block: suspend () -> Resource<T>,
    ): Resource<T> {
        while (repetitions.getAndIncrement() < repeat) {
            val res = block()
            if (res is Resource.Failure && res.exception in exceptionsCatching) {
                fallbackAction()
            } else {
                repetitions.set(INITIAL_REPETITIONS)
                return res
            }
            delay(delay)
        }
        return Resource.Failure(UnableToPerformOperation())
    }
    
    companion object {
        const val INITIAL_REPETITIONS = 0
        const val DEFAULT_REPETITIONS = 3
    }
}
