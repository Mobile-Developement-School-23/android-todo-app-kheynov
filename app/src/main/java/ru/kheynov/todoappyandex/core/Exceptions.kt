package ru.kheynov.todoappyandex.core

open class TodoException : Exception()

class OutOfSyncDataException : TodoException()
class TodoItemNotFoundException : TodoException()
class BadRequestException : TodoException()
class UnauthorizedException : TodoException()
class ServerSideException : TodoException()
class DuplicateItemException : TodoException()
class NetworkException : TodoException()

class EmptyFieldException : IllegalStateException()

class UnableToPerformOperation : Exception()