package ru.kheynov.todoappyandex.featureTodoEditor.presentation

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
import ru.kheynov.todoappyandex.featureTodoEditor.components.AddEditScreen
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders.AddEditAction
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
        return ComposeView(requireContext()).apply {
            setContent {
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
        }
    }

    companion object {
        private const val TODO_ID_KEY = "todo_id"

        fun createArgumentsForEditing(todoId: String): Bundle {
            return bundleOf(TODO_ID_KEY to todoId)
        }
    }
}
