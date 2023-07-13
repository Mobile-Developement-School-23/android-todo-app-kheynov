package ru.kheynov.todoappyandex.featureTodoEditor.presentation

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.appComponent
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders.AddEditAction
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders.AddEditUiEvent
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

class TodoFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: TodoViewModel by viewModels { viewModelFactory }

    private lateinit var navController: NavController

    private val todoId: String?
        get() = arguments?.getString(TODO_ID_KEY)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent
            .todoEditorComponent()
            .create()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_todo, container, false).apply {
            findViewById<ComposeView>(R.id.composeView).setContent {
                val state = viewModel.state.collectAsStateWithLifecycle()
                AddEditScreen(
                    state = state,
                    onEvent = viewModel::handleEvent
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        todoId?.let {
            viewModel.fetchTodo(it)
        } // fetch task if editing

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.actions.collect(::handleActions)
            }
        }
    }

    private fun handleActions(action: AddEditAction) {
        when (action) {
            AddEditAction.NavigateBack -> navController.popBackStack()
            is AddEditAction.ShowError ->
                with(
                    Snackbar.make(
                        requireView(),
                        action.text.asString(requireContext()),
                        Snackbar.LENGTH_SHORT
                    )
                ) {
                    setAction(R.string.retry) {
                        viewModel.retryLastOperation()
                    }
                    show()
                }

            AddEditAction.ShowDatePicker -> showDatePicker()
            AddEditAction.ShowUrgencySelector -> TODO()
        }
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePicker = DatePickerDialog(
            requireContext(),
            R.style.DatePickerStyle,

            { _, yearPicker, monthPicker, dayPicker ->
                viewModel.handleEvent(
                    AddEditUiEvent.ChangeDeadline(
                        LocalDate.of(
                            yearPicker,
                            monthPicker + 1,
                            dayPicker
                        )
                    )
                )
            },
            year,
            month,
            day
        )
        datePicker.show()
    }

    companion object {
        private const val TODO_ID_KEY = "todo_id"

        fun createArgumentsForEditing(todoId: String): Bundle {
            return bundleOf(TODO_ID_KEY to todoId)
        }
    }
}
