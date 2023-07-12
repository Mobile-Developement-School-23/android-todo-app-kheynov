package ru.kheynov.todoappyandex.featureTodosList.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import ru.kheynov.todoappyandex.core.domain.repositories.TodoItemsRepository
import ru.kheynov.todoappyandex.core.utils.BadRequestException
import ru.kheynov.todoappyandex.core.utils.DuplicateItemException
import ru.kheynov.todoappyandex.core.utils.NetworkException
import ru.kheynov.todoappyandex.core.utils.OperationHandlerWithFallback
import ru.kheynov.todoappyandex.core.utils.Resource
import ru.kheynov.todoappyandex.core.utils.ServerSideException
import ru.kheynov.todoappyandex.core.utils.TodoItemNotFoundException
import ru.kheynov.todoappyandex.core.utils.UiText
import ru.kheynov.todoappyandex.core.utils.UnableToPerformOperation
import ru.kheynov.todoappyandex.featureTodosList.presentation.stateHolders.MainScreenAction
import ru.kheynov.todoappyandex.featureTodosList.presentation.stateHolders.MainScreenState
import javax.inject.Inject

class MainScreenViewModel @Inject constructor(
    private val repository: TodoItemsRepository,
) : ViewModel() {
    private val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        Log.e("Coroutine", "Error: ", throwable)
        CoroutineScope(context).launch { handleException(throwable) }
    }

    private val _state = MutableStateFlow<MainScreenState>(MainScreenState.Loading)
    val state: StateFlow<MainScreenState> = _state.asStateFlow()

    private val operationHandler = OperationHandlerWithFallback(fallbackAction = {
        repository.syncTodos()
    })

    private val _actions: Channel<MainScreenAction> = Channel(Channel.BUFFERED)
    val actions: Flow<MainScreenAction> = _actions.receiveAsFlow()

    var isShowingDoneTasks = true
        private set

    private var lastOperation: (suspend () -> Unit)? = null

    private val todos = repository.todos

    init {
        viewModelScope.launch {
            repository.syncTodos()
            todos.collect { todos ->
                _state.update {
                    if (todos.isNotEmpty()) MainScreenState.Loaded(todos)
                    else MainScreenState.Empty
                }
            }
        }
    }

    fun setTodoState(todoItem: TodoItem, state: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            _state.update { MainScreenState.Loading }
            lastOperation = {
                val res = operationHandler.executeOperation {
                    repository.setTodoState(todoItem, state)
                }
                when (res) {
                    is Resource.Failure -> handleException(res.exception)
                    is Resource.Success -> Unit
                }
            }
            lastOperation?.invoke()
        }
    }

    fun updateTodos() {
        viewModelScope.launch(exceptionHandler) {
            _state.update { MainScreenState.Loading }
            repository.syncTodos()
        }
    }


    fun editTodo(todoItem: TodoItem) {
        viewModelScope.launch(exceptionHandler) {
            _actions.send(MainScreenAction.NavigateToEditing(todoItem.id))
        }
    }

    fun addTodo() {
        viewModelScope.launch(exceptionHandler) {
            _actions.send(MainScreenAction.NavigateToAdding)
        }
    }

    fun toggleDoneTasks() {
        viewModelScope.launch(exceptionHandler) {
            isShowingDoneTasks = !isShowingDoneTasks
            _actions.send(MainScreenAction.ToggleDoneTasks(isShowingDoneTasks))
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
        val errorText = when (e) {
            is HttpException, is NetworkException -> UiText.StringResource(R.string.connection_error)

            is ServerSideException,
            is BadRequestException,
            is TodoItemNotFoundException,
            is DuplicateItemException,
            -> UiText.StringResource(R.string.server_error)

            is UnableToPerformOperation -> UiText.StringResource(R.string.unable_to_perform)
            else -> UiText.PlainText(e.localizedMessage?.toString() ?: "Unknown error")
        }

        _actions.send(MainScreenAction.ShowError(errorText))
    }
}
