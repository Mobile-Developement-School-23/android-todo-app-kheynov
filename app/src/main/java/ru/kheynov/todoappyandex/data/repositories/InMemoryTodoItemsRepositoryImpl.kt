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
            createdAt = LocalDateTime.now().minusDays(1),
        ),
        TodoItem(
            id = "3",
            text = "Third todo",
            urgency = TodoUrgency.HIGH,
            isDone = false,
            createdAt = LocalDateTime.now().minusDays(2),
        ),
        TodoItem(
            id = "4",
            text = "Купить молоко",
            urgency = TodoUrgency.LOW,
            isDone = false,
            createdAt = LocalDateTime.now().minusDays(3),
            deadline = LocalDateTime.now().plusDays(1),
            editedAt = LocalDateTime.now().plusHours(8),
        ),
        TodoItem(
            id = "5",
            text = "Купить хлеб",
            urgency = TodoUrgency.STANDARD,
            isDone = false,
            createdAt = LocalDateTime.now().minusDays(4),
            deadline = LocalDateTime.now().plusDays(1),
            editedAt = LocalDateTime.now().plusHours(8),
        ),
        TodoItem(
            id = "6",
            text = "Купить масло",
            urgency = TodoUrgency.HIGH,
            isDone = false,
            createdAt = LocalDateTime.now().minusDays(5),
            deadline = LocalDateTime.now().plusDays(1),
            editedAt = LocalDateTime.now().plusHours(8),
        ),
        TodoItem(
            id = "7",
            text = "Купить quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum",
            urgency = TodoUrgency.LOW,
            isDone = true,
            createdAt = LocalDateTime.now().minusDays(6),
            deadline = LocalDateTime.now().plusDays(1),
            editedAt = LocalDateTime.now().plusHours(8),
        ),
        TodoItem(
            id = "8",
            text = "Купить хлеб",
            urgency = TodoUrgency.STANDARD,
            isDone = false,
            createdAt = LocalDateTime.now().minusDays(7),
            deadline = LocalDateTime.now().plusDays(1),
            editedAt = LocalDateTime.now().plusHours(8),
        ),
        TodoItem(
            id = "9",
            text = "Купить масло",
            urgency = TodoUrgency.HIGH,
            isDone = false,
            createdAt = LocalDateTime.now().minusDays(8),
            deadline = LocalDateTime.now().plusDays(1),
            editedAt = LocalDateTime.now().plusHours(8),
        ),
        TodoItem(
            id = "10",
            text = "harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda",
            urgency = TodoUrgency.LOW,
            isDone = true,
            createdAt = LocalDateTime.now().minusDays(9),
            deadline = LocalDateTime.now().plusDays(1),
            editedAt = LocalDateTime.now().plusHours(8),
        ),

        TodoItem(
            id = "11",
            text = "Купить хлеб",
            urgency = TodoUrgency.STANDARD,
            isDone = false,
            createdAt = LocalDateTime.parse("2021-09-01T10:15:30"),
            deadline = LocalDateTime.parse("2023-09-02T10:15:30"),
            editedAt = LocalDateTime.now().plusHours(8),
        ),

        TodoItem(
            id = "12",
            text = "Купить масло",
            urgency = TodoUrgency.HIGH,
            isDone = true,
            createdAt = LocalDateTime.now(),
            deadline = LocalDateTime.now().plusDays(1),
            editedAt = LocalDateTime.now().plusHours(8),
        ),

        TodoItem(
            id = "13",
            text = "Купить молоко",
            urgency = TodoUrgency.LOW,
            isDone = false,
            createdAt = LocalDateTime.now(),
            deadline = LocalDateTime.now().plusDays(1),
            editedAt = LocalDateTime.now().plusHours(8),
        ),

        TodoItem(
            id = "14",
            text = "Купить хлеб",
            urgency = TodoUrgency.STANDARD,
            isDone = true,
            createdAt = LocalDateTime.now(),
            deadline = LocalDateTime.now().plusDays(1),
            editedAt = LocalDateTime.now().plusHours(8)
        ),

        TodoItem(
            id = "15",
            text = "Купить масло",
            urgency = TodoUrgency.HIGH,
            isDone = false,
            createdAt = LocalDateTime.now(),
            deadline = LocalDateTime.now().plusDays(1),
            editedAt = LocalDateTime.now().plusHours(8)
        ),

        TodoItem(
            id = "16",
            text = "Купить which is the same as saying through shrinking from toil and pain. These cases are perfectly simple and easy to distinguish. In a free hour, when our",
            urgency = TodoUrgency.LOW,
            isDone = true,
            createdAt = LocalDateTime.now(),
            deadline = LocalDateTime.now().plusDays(1),
            editedAt = LocalDateTime.now().plusHours(8)
        ),

        TodoItem(
            id = "17",
            text = "Купить Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip",
            urgency = TodoUrgency.STANDARD,
            isDone = true,
            createdAt = LocalDateTime.now(),
            deadline = LocalDateTime.now().plusDays(1),
            editedAt = LocalDateTime.now().plusHours(8)
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