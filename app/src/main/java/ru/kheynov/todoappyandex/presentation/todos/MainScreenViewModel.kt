package ru.kheynov.todoappyandex.presentation.todos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.core.OperationRepeatHandler
import ru.kheynov.todoappyandex.core.Resource
import ru.kheynov.todoappyandex.core.UiText
import ru.kheynov.todoappyandex.core.UnableToPerformOperation
import ru.kheynov.todoappyandex.domain.entities.TodoItem
import ru.kheynov.todoappyandex.domain.repositories.TodoItemsRepository
import ru.kheynov.todoappyandex.presentation.todos.stateHolders.MainScreenAction
import ru.kheynov.todoappyandex.presentation.todos.stateHolders.MainScreenState
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repository: TodoItemsRepository,
) : ViewModel() {
    
    private val _state = MutableStateFlow<MainScreenState>(MainScreenState.Loading)
    val state: StateFlow<MainScreenState> = _state.asStateFlow()
    
    private val _actions: Channel<MainScreenAction> = Channel(Channel.BUFFERED)
    val actions: Flow<MainScreenAction> = _actions.receiveAsFlow()
    
    private var isShowingDoneTasks = true
    
    private val todos = repository.todos
    
    init {
        viewModelScope.launch {
            repository.syncTodos()
        }
        fetchTodos()
    }
    
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("Coroutine", "Error: ", throwable)
    }
    
    private suspend fun <T> retryOperation(
        block: suspend () -> Resource<T>,
    ): Resource<T> {
        repository.syncTodos()
        for (i in 0..3) {
            val res = block()
            if (res is Resource.Success) {
                return res
            }
        }
        return Resource.Failure(UnableToPerformOperation())
    }
    
    fun setTodoState(todoItem: TodoItem, state: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            var res = repository.setTodoState(todoItem, state)
            if (res is Resource.Failure) {
                res = retryOperation {
                    repository.setTodoState(todoItem, state)
                }
                if (res is Resource.Failure) {
                    _actions.send(MainScreenAction.ShowError(UiText.StringResource(R.string.unable_to_perform)))
                }
            }
        }
        fetchTodos()
    }
    
    fun fetchTodos() {
        _state.update { (MainScreenState.Loading) }
        viewModelScope.launch {
            todos.collect { todos ->
                _state.update { (MainScreenState.Loaded(todos)) }
            }
        }
    }
    
    fun editTodo(todoItem: TodoItem) {
        viewModelScope.launch {
            _actions.send(MainScreenAction.NavigateToEditing(todoItem.id))
        }
    }
    
    fun addTodo() {
        viewModelScope.launch {
            _actions.send(MainScreenAction.NavigateToAdding)
        }
    }
    
    fun toggleDoneTasks() {
        viewModelScope.launch {
            isShowingDoneTasks = !isShowingDoneTasks
            _actions.send(MainScreenAction.ToggleDoneTasks(isShowingDoneTasks))
        }
    }
}
