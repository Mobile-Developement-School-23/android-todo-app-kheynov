package ru.kheynov.todoappyandex.core.utils

/**
 * [Resource] is a wrapper for data
 * @param R type of data
 */
sealed class Resource<out R> {
    data class Success<out R>(val result: R) : Resource<R>()
    data class Failure(val exception: Exception) : Resource<Nothing>()
}
