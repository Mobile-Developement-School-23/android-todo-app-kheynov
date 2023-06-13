package ru.kheynov.todoappyandex.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.kheynov.todoappyandex.domain.entities.TodoItem

interface TodoItemsRepository {
    val todos: Flow<List<TodoItem>>
    fun addTodo(todo: TodoItem)
    fun deleteTodo(id: Int)
    fun editTodo(todo: TodoItem)
    fun getTodoById(id: Int): TodoItem?
}