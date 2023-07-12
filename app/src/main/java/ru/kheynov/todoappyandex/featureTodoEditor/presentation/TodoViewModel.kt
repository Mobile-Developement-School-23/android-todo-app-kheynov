package ru.kheynov.todoappyandex.featureTodoEditor.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.core.domain.entities.TodoItem
import ru.kheynov.todoappyandex.core.domain.entities.TodoUrgency
import ru.kheynov.todoappyandex.core.domain.repositories.TodoItemsRepository
import ru.kheynov.todoappyandex.core.ui.UiText
import ru.kheynov.todoappyandex.core.utils.BadRequestException
import ru.kheynov.todoappyandex.core.utils.DuplicateItemException
import ru.kheynov.todoappyandex.core.utils.EmptyFieldException
import ru.kheynov.todoappyandex.core.utils.NetworkException
import ru.kheynov.todoappyandex.core.utils.OperationHandlerWithFallback
import ru.kheynov.todoappyandex.core.utils.Resource
import ru.kheynov.todoappyandex.core.utils.ServerSideException
import ru.kheynov.todoappyandex.core.utils.TodoItemNotFoundException
import ru.kheynov.todoappyandex.core.utils.UnableToPerformOperation
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders.AddEditAction
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders.AddEditState
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders.AddEditUiEvent
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class TodoViewModel @Inject constructor(
    private val repository: TodoItemsRepository,
) : ViewModel() {
    private val _actions: Channel<AddEditAction> = Channel(Channel.BUFFERED)
    val actions: Flow<AddEditAction> = _actions.receiveAsFlow()

    private val _todoItem = MutableStateFlow(
        TodoItem(
            id = "",
            text = "",
            urgency = TodoUrgency.STANDARD,
            deadline = null,
            isDone = false,
            createdAt = LocalDateTime.now()
        )
    )

    private val _isEditing = MutableStateFlow(false)

    val state: StateFlow<AddEditState> = combine(_todoItem, _isEditing) { todo, editing ->
        AddEditState(
            todo, editing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = AddEditState(
            _todoItem.value, _isEditing.value
        )
    )

    private val handler = OperationHandlerWithFallback(fallbackAction = { repository.syncTodos() })

    private val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        Log.e("Coroutine", "Error: ", throwable)
        CoroutineScope(context).launch { handleException(throwable) }
    }

    private var lastOperation: (suspend () -> Unit)? = null

    fun fetchTodo(id: String) {
        viewModelScope.launch(exceptionHandler) {
            _isEditing.update { true } // if we got a todo id, we are in edit mode
            lastOperation = {
                val todo = repository.getTodoById(id)
                if (todo is Resource.Failure) {
                    _actions.send(AddEditAction.ShowError(UiText.StringResource(R.string.todo_not_found)))
                    _actions.send(AddEditAction.NavigateBack)
                } else {
                    _todoItem.update { (todo as Resource.Success).result }
                }
            }
            lastOperation?.invoke()
        }
    }

    fun handleEvent(event: AddEditUiEvent) {
        when (event) {
            is AddEditUiEvent.ChangeTitle -> changeTitle(event.text)
            is AddEditUiEvent.ChangeUrgency -> changeUrgency(event.urgency)
            is AddEditUiEvent.ChangeDeadline -> changeDeadline(event.deadline)
            AddEditUiEvent.SaveTodo -> saveTodo()
            AddEditUiEvent.DeleteTodo -> deleteTodo()
        }
    }

    private fun changeTitle(text: String) {
        _todoItem.update { _todoItem.value.copy(text = text) }
    }

    private fun changeUrgency(urgency: TodoUrgency) {
        _todoItem.update { _todoItem.value.copy(urgency = urgency) }
    }

    private fun changeDeadline(deadline: LocalDate?) {
        _todoItem.update { _todoItem.value.copy(deadline = deadline) }
    }

    private fun saveTodo() {
        viewModelScope.launch(exceptionHandler) {
            lastOperation = {
                val handleResult = handler.executeOperation {
                    if (_todoItem.value.text.isBlank()) {
                        _actions.send(
                            AddEditAction.ShowError(UiText.StringResource(R.string.title_cannot_be_empty))
                        )
                        return@executeOperation Resource.Failure(EmptyFieldException())
                    } else if (state.value.todo.id.isBlank()) {
                        return@executeOperation repository.addTodo(
                            _todoItem.value.copy(
                                id = UUID.randomUUID().toString(), createdAt = LocalDateTime.now()
                            )
                        )
                    } else {
                        return@executeOperation repository
                            .editTodo(_todoItem.value.copy(editedAt = LocalDateTime.now()))
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

    private fun deleteTodo() {
        viewModelScope.launch(exceptionHandler) {
            lastOperation = {
                val handleResult = handler.executeOperation {
                    repository.deleteTodo(_todoItem.value.id)
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
        val errorText: UiText = when (e) {
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
