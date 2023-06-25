package ru.kheynov.todoappyandex.core

open class TodoException : Exception()

class OutOfSyncDataException : TodoException()
class TodoItemNotFoundException : TodoException()
class ServerException : TodoException()
class BadRequestException : TodoException()
class UnauthorizedException : TodoException()
