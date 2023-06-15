package ru.kheynov.todoappyandex.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.kheynov.todoappyandex.domain.entities.TodoItem
import ru.kheynov.todoappyandex.domain.entities.TodoUrgency
import ru.kheynov.todoappyandex.domain.repositories.TodoItemsRepository
import java.time.LocalDateTime
import java.util.UUID

class InMemoryTodoItemsRepositoryImpl : TodoItemsRepository {
    private val todosList = mutableListOf(
        TodoItem(
            id = UUID.randomUUID().toString(),
            text = "First todo",
            urgency = TodoUrgency.LOW,
            isDone = false,
            createdAt = LocalDateTime.now(),
        ),
        TodoItem(
            id = UUID.randomUUID().toString(),
            text = "Second todo",
            urgency = TodoUrgency.STANDARD,
            isDone = false,
            createdAt = LocalDateTime.now(),
        ),
        TodoItem(
            id = UUID.randomUUID().toString(),
            text = "Third todo",
            urgency = TodoUrgency.HIGH,
            isDone = false,
            createdAt = LocalDateTime.now(),
        ),
        TodoItem(
            id = UUID.randomUUID().toString(),
            text = "Купить молоко",
            urgency = TodoUrgency.LOW,
            isDone = false,
            createdAt = LocalDateTime.now(),
            deadline = LocalDateTime.now().plusDays(1),
            editedAt = LocalDateTime.now().plusHours(8),
        )
    )
    
    private val _todos = MutableStateFlow<List<TodoItem>>(emptyList())
    override val todos: Flow<List<TodoItem>>
        get() = _todos
    
    init {
        _todos.update { todosList.toList() }
    }
    
    override fun addTodo(todo: TodoItem) {
        todosList.add(todo)
        _todos.update { todosList.toList() }
    }
    
    override fun deleteTodo(id: String) {
        todosList.removeIf { it.id == id }
        _todos.update { todosList.toList() } //TODO
    }
    
    override fun editTodo(todo: TodoItem) {
        val index = todosList.indexOfFirst { it.id == todo.id }
        todosList[index] = todo
        _todos.update { todosList.toList() }
    }
    
    override fun getTodoById(id: String): TodoItem? {
        return todosList.find { it.id == id }
    }
    
    override fun setTodoState(todoItem: TodoItem, state: Boolean) {
        todosList.find { it.id == todoItem.id }?.isDone = state
        _todos.update { todosList.toList() }
    }
}