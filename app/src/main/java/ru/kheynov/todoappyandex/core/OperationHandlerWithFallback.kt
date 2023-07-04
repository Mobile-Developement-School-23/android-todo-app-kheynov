package ru.kheynov.todoappyandex.core

import kotlinx.coroutines.delay

/**
 * [OperationHandlerWithFallback] tries to perform operation, if it wasn't successful execute
 * [fallbackAction]
 * and repeat it [repeat] times with [delay] between each attempt
 * @param fallbackAction action to perform if operation failed
 */

class OperationHandlerWithFallback(
    val fallbackAction: suspend () -> Resource<Unit>,
) {
    /**
     * Repeats operation [repeat] times with [delay] between each attempt
     * @param repeat number of repetitions
     * @param delay delay between each attempt
     * @param exceptionsCatching list of exceptions to catch
     * @param block operation to perform
     * @return [Resource] with result of operation
     */
    suspend fun <T> executeOperation(
        repeat: Int = DEFAULT_REPETITIONS,
        delay: Long = 0,
        exceptionsCatching: List<Exception> = listOf(
            OutOfSyncDataException(),
            ServerSideException()
        ),
        block: suspend () -> Resource<T>,
    ): Resource<T> {
        var repetitions = INITIAL_REPETITIONS
        while (repetitions < repeat) {
            val res = block()
            if (res is Resource.Failure && res.exception in exceptionsCatching) {
                fallbackAction()
            } else return res
            repetitions++
            delay(delay)
        }
        return Resource.Failure(UnableToPerformOperation())
    }
    
    companion object {
        const val INITIAL_REPETITIONS = 0
        const val DEFAULT_REPETITIONS = 3
    }
}
