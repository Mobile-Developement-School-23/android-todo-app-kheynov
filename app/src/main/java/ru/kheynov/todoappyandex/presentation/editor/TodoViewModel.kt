package ru.kheynov.todoappyandex.presentation.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.core.Resource
import ru.kheynov.todoappyandex.core.UiText
import ru.kheynov.todoappyandex.domain.entities.TodoItem
import ru.kheynov.todoappyandex.domain.entities.TodoUrgency
import ru.kheynov.todoappyandex.domain.repositories.TodoItemsRepository
import ru.kheynov.todoappyandex.presentation.editor.stateHolders.AddEditAction
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoItemsRepository,
) : ViewModel() {
    private val _actions: Channel<AddEditAction> = Channel(Channel.BUFFERED)
    val actions: Flow<AddEditAction> = _actions.receiveAsFlow()
    
    private val _state = MutableStateFlow(
        TodoItem(
            id = "",
            text = "",
            urgency = TodoUrgency.STANDARD,
            deadline = null,
            isDone = false,
            createdAt = LocalDateTime.now()
        )
    )
    val state: StateFlow<TodoItem> = _state.asStateFlow()
    
    fun fetchTodo(id: String) {
        viewModelScope.launch {
            val todo = repository.getTodoById(id)
            if (todo is Resource.Failure) {
                _actions.send(AddEditAction.ShowError(UiText.StringResource(R.string.todo_not_found)))
                _actions.send(AddEditAction.NavigateBack)
                return@launch
            }
            _state.update { (todo as Resource.Success).result }
        }
    }
    
    fun changeTitle(text: String) {
        _state.update { _state.value.copy(text = text) }
    }
    
    fun changeUrgency(urgency: TodoUrgency) {
        _state.update { _state.value.copy(urgency = urgency) }
    }
    
    fun onDeadlineSwitchChecked(checked: Boolean) {
        if (checked) {
            if (_state.value.deadline != null) return
            viewModelScope.launch {
                _actions.send(AddEditAction.ShowDatePicker)
            }
        } else {
            _state.update { _state.value.copy(deadline = null) }
        }
    }
    
    fun changeDeadline(deadline: LocalDate?) {
        _state.update { _state.value.copy(deadline = deadline) }
    }
    
    fun saveTodo() {
        viewModelScope.launch {
            if (_state.value.text.isBlank()) {
                _actions.send(AddEditAction.ShowError(UiText.StringResource(R.string.title_cannot_be_empty)))
                return@launch
            }
            if (state.value.id.isBlank()) {
                repository.addTodo(_state.value.copy(id = UUID.randomUUID().toString()))
            } else {
                repository.editTodo(_state.value.copy(editedAt = LocalDateTime.now()))
            }
            _actions.send(AddEditAction.NavigateBack)
        }
    }
    
    fun deleteTodo() {
        viewModelScope.launch {
            repository.deleteTodo(_state.value.id)
            _actions.send(AddEditAction.NavigateBack)
        }
    }
}
