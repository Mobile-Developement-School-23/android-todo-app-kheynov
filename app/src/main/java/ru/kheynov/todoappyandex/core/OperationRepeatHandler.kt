package ru.kheynov.todoappyandex.core

import java.util.concurrent.atomic.AtomicInteger

//class OperationRepeatHandler(
//     fallbackAction: suspend () -> Unit = {},
//     onFailure: suspend () -> Unit = {},
//) {
//    private var repetions = AtomicInteger(INITIAL_REPETITIONS)
//
//
//
//    companion object {
//        const val INITIAL_REPETITIONS = 0
//        const val DEFAULT_REPETITIONS = 3
//    }
//}

suspend inline fun <T> repeatOperation(
    repeat: Int = 3,
    crossinline fallbackAction: suspend () -> Unit,
    crossinline block: suspend () -> Resource<T>,
): Resource<T> {
    repeat(repeat) {
        val res = block()
        if (res is Resource.Failure) {
            fallbackAction()
        } else
            return res
    }
    return Resource.Failure(UnableToPerformOperation())
}