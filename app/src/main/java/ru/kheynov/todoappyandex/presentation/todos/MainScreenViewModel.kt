package ru.kheynov.todoappyandex.presentation.todos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kheynov.todoappyandex.data.repositories.InMemoryTodoItemsRepositoryImpl
import ru.kheynov.todoappyandex.domain.entities.TodoItem
import ru.kheynov.todoappyandex.domain.repositories.TodoItemsRepository
import ru.kheynov.todoappyandex.presentation.todos.stateHolders.MainScreenAction
import ru.kheynov.todoappyandex.presentation.todos.stateHolders.MainScreenState

class MainScreenViewModel : ViewModel() {
    private val repository: TodoItemsRepository = InMemoryTodoItemsRepositoryImpl()
    
    private val _state = MutableStateFlow<MainScreenState>(MainScreenState.Loading)
    val state: StateFlow<MainScreenState> = _state.asStateFlow()
    
    private val _actions: Channel<MainScreenAction> = Channel(Channel.BUFFERED)
    val actions: Flow<MainScreenAction> = _actions.receiveAsFlow()
    
    private var isShowingDoneTasks = true
    
    val todos get() = repository.todos
    
    init {
        fetchTodos()
    }
    
    fun setTodoState(todoItem: TodoItem, state: Boolean) {
        viewModelScope.launch {
            repository.setTodoState(todoItem, state)
        }
        fetchTodos()
    }
    
    private fun fetchTodos() {
        _state.update { (MainScreenState.Loading) }
        viewModelScope.launch {
            repository.todos.collect { todos ->
                _state.update { (MainScreenState.Loaded(todos)) }
            }
        }
    }
    
    fun showInfo(todoItem: TodoItem) {
        viewModelScope.launch {
            _actions.send(MainScreenAction.RouteToEdit(todoItem.id))
        }
    }
    
    fun addTodo() {
        viewModelScope.launch {
            _actions.send(MainScreenAction.RouteToAdd)
        }
    }
    
    fun toggleDoneTasks() {
        viewModelScope.launch {
            isShowingDoneTasks = !isShowingDoneTasks
            _actions.send(MainScreenAction.ToggleDoneTasks(isShowingDoneTasks))
        }
    }
}