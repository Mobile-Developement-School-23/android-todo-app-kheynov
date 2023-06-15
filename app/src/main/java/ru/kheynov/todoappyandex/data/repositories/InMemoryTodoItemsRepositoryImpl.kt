package ru.kheynov.todoappyandex.data.repositories

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.kheynov.todoappyandex.domain.entities.TodoItem
import ru.kheynov.todoappyandex.domain.entities.TodoUrgency
import ru.kheynov.todoappyandex.domain.repositories.TodoItemsRepository
import java.time.LocalDateTime

object MockDataSource {
    private val todosList = mutableListOf(
        TodoItem(
            id = "1",
            text = "First todo",
            urgency = TodoUrgency.LOW,
            isDone = false,
            createdAt = LocalDateTime.now(),
        ),
        TodoItem(
            id = "2",
            text = "Second todo",
            urgency = TodoUrgency.STANDARD,
            isDone = false,
            createdAt = LocalDateTime.now(),
        ),
        TodoItem(
            id = "3",
            text = "Third todo",
            urgency = TodoUrgency.HIGH,
            isDone = false,
            createdAt = LocalDateTime.now(),
        ),
        TodoItem(
            id = "4",
            text = "Купить молоко",
            urgency = TodoUrgency.LOW,
            isDone = false,
            createdAt = LocalDateTime.now(),
            deadline = LocalDateTime.now().plusDays(1),
            editedAt = LocalDateTime.now().plusHours(8),
        )
    )
    
    fun getTodoById(id: String): TodoItem? {
        return todosList.find { it.id == id }
    }
    
    fun add(todo: TodoItem) {
        todosList.add(todo)
    }
    
    fun delete(id: String) {
        todosList.removeIf { it.id == id }
    }
    
    fun edit(todo: TodoItem) {
        val index = todosList.indexOfFirst { it.id == todo.id }
        todosList[index] = todo
    }
    
    fun getAll(): List<TodoItem> {
        return todosList.toList()
    }
}

object InMemoryTodoItemsRepositoryImpl : TodoItemsRepository {
    private val dao = MockDataSource
    
    private val _todos = MutableStateFlow<List<TodoItem>>(emptyList())
    override val todos: StateFlow<List<TodoItem>>
        get() = _todos.asStateFlow()
    
    init {
        _todos.update { dao.getAll() }
    }
    
    override fun addTodo(todo: TodoItem) {
        dao.add(todo)
        _todos.update { dao.getAll() }
    }
    
    override fun deleteTodo(id: String) {
        dao.delete(id)
        _todos.update { dao.getAll() }
    }
    
    override fun editTodo(todo: TodoItem) {
        dao.edit(todo)
        _todos.update { dao.getAll() }
    }
    
    override fun getTodoById(id: String): TodoItem? {
        return dao.getTodoById(id)
    }
    
    override fun setTodoState(todoItem: TodoItem, state: Boolean) {
        dao.edit(todoItem.copy(isDone = state))
        _todos.update {
            dao.getAll()
        }
    }
}