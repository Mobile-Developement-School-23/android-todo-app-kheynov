package ru.kheynov.todoappyandex.featureTodoEditor.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.core.domain.entities.TodoItem
import ru.kheynov.todoappyandex.core.domain.entities.TodoUrgency
import ru.kheynov.todoappyandex.core.domain.repositories.TodoItemsRepository
import ru.kheynov.todoappyandex.core.utils.BadRequestException
import ru.kheynov.todoappyandex.core.utils.DuplicateItemException
import ru.kheynov.todoappyandex.core.utils.EmptyFieldException
import ru.kheynov.todoappyandex.core.utils.NetworkException
import ru.kheynov.todoappyandex.core.utils.OperationHandlerWithFallback
import ru.kheynov.todoappyandex.core.utils.Resource
import ru.kheynov.todoappyandex.core.utils.ServerSideException
import ru.kheynov.todoappyandex.core.utils.TodoItemNotFoundException
import ru.kheynov.todoappyandex.core.utils.UiText
import ru.kheynov.todoappyandex.core.utils.UnableToPerformOperation
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders.AddEditAction
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
    
    private val handler = OperationHandlerWithFallback(
        fallbackAction = { repository.syncTodos() }
    )
    
    private val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        Log.e("Coroutine", "Error: ", throwable)
        CoroutineScope(context).launch { handleException(throwable) }
    }
    
    private var lastOperation: (suspend () -> Unit)? = null
    
    fun fetchTodo(id: String) {
        viewModelScope.launch(exceptionHandler) {
            lastOperation = {
                val todo = repository.getTodoById(id)
                if (todo is Resource.Failure) {
                    _actions.send(AddEditAction.ShowError(UiText.StringResource(R.string.todo_not_found)))
                    _actions.send(AddEditAction.NavigateBack)
                } else {
                    _state.update { (todo as Resource.Success).result }
                }
            }
            lastOperation?.invoke()
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
        viewModelScope.launch(exceptionHandler) {
            lastOperation = {
                val handleResult = handler.executeOperation {
                    if (_state.value.text.isBlank()) {
                        _actions.send(
                            AddEditAction.ShowError(UiText.StringResource(R.string.title_cannot_be_empty))
                        )
                        return@executeOperation Resource.Failure(EmptyFieldException())
                    } else if (state.value.id.isBlank()) {
                        return@executeOperation repository.addTodo(
                            _state.value.copy(
                                id = UUID.randomUUID().toString(),
                                createdAt = LocalDateTime.now()
                            )
                        )
                    } else {
                        return@executeOperation repository.editTodo(_state.value.copy(editedAt = LocalDateTime.now()))
                    }
                }
                when (handleResult) {
                    is Resource.Failure -> handleException(handleResult.exception)
                    is Resource.Success -> _actions.send(AddEditAction.NavigateBack)
                }
            }
            lastOperation?.invoke()
        }
    }
    
    fun deleteTodo() {
        viewModelScope.launch(exceptionHandler) {
            lastOperation = {
                val handleResult = handler.executeOperation {
                    repository.deleteTodo(_state.value.id)
                }
                when (handleResult) {
                    is Resource.Failure -> handleException(handleResult.exception)
                    is Resource.Success -> _actions.send(AddEditAction.NavigateBack)
                }
            }
            lastOperation?.invoke()
        }
    }
    
    fun retryLastOperation() {
        viewModelScope.launch(exceptionHandler) {
            lastOperation?.invoke()
        }
    }
    
    private suspend fun handleException(
        e: Throwable,
    ) {
        val errorText: UiText =
            when (e) {
                is HttpException, is NetworkException -> UiText.StringResource(R.string.connection_error)
                
                is ServerSideException,
                is BadRequestException,
                is TodoItemNotFoundException,
                is DuplicateItemException,
                -> UiText.StringResource(R.string.server_error)
                
                is EmptyFieldException -> UiText.StringResource(R.string.title_cannot_be_empty)
                
                is UnableToPerformOperation -> UiText.StringResource(R.string.unable_to_perform)
                else -> UiText.PlainText(e.localizedMessage?.toString() ?: "Unknown error")
            }
        
        _actions.send(AddEditAction.ShowError(errorText))
    }
}
