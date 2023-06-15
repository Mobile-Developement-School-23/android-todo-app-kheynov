package ru.kheynov.todoappyandex.domain.repositories

import kotlinx.coroutines.flow.Flow
import ru.kheynov.todoappyandex.domain.entities.TodoItem

interface TodoItemsRepository {
    val todos: Flow<List<TodoItem>>
    fun addTodo(todo: TodoItem)
    fun deleteTodo(id: String)
    fun editTodo(todo: TodoItem)
    fun getTodoById(id: String): TodoItem?
    fun setTodoState(todoItem: TodoItem, state: Boolean)
}