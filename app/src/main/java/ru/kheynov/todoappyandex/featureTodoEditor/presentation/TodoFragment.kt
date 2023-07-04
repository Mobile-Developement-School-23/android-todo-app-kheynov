package ru.kheynov.todoappyandex.featureTodoEditor.presentation

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.databinding.FragmentTodoBinding
import ru.kheynov.todoappyandex.core.domain.entities.TodoItem
import ru.kheynov.todoappyandex.core.domain.entities.TodoUrgency
import ru.kheynov.todoappyandex.featureTodoEditor.presentation.stateHolders.AddEditAction
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Calendar

@AndroidEntryPoint
class TodoFragment : Fragment() {
    private val viewModel: TodoViewModel by viewModels()
    private lateinit var navController: NavController
    
    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!
    
    private val todoId: String?
        get() = arguments?.getString(TODO_ID_KEY)
    
    private val isEditing: Boolean get() = todoId != null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        
        todoId?.let { viewModel.fetchTodo(it) } // fetch task if editing
        
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.actions.collect(::handleActions)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect(::updateUI)
            }
        }
        
        binding.apply {
            with(deleteButton) {
                isEnabled = isEditing
                setTextColor(
                    if (isEditing) {
                        ContextCompat.getColor(requireContext(), R.color.red)
                    } else {
                        ContextCompat.getColor(requireContext(), R.color.colorDisable)
                    }
                )
                setIconTintResource(if (isEditing) R.color.red else R.color.colorDisable)
                setOnClickListener {
                    viewModel.deleteTodo()
                }
            }
            
            closeButton.setOnClickListener {
                navController.popBackStack()
            }
            saveButton.setOnClickListener {
                viewModel.saveTodo()
            }
            
            with(titleEditText) {
                setText(viewModel.state.value.text)
                addTextChangedListener {
                    viewModel.changeTitle(it.toString())
                }
            }
            urgencyLayout.setOnClickListener {
                showUrgencyMenu(it)
            }
            deadlineSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.onDeadlineSwitchChecked(isChecked)
            }
        }
    }
    
    private fun handleActions(action: AddEditAction) {
        when (action) {
            AddEditAction.NavigateBack -> navController.popBackStack()
            is AddEditAction.ShowError ->
                with(
                    Snackbar.make(
                        binding.root,
                        action.text.toString(requireContext()),
                        Snackbar.LENGTH_SHORT
                    )
                ) {
                    setAction(R.string.retry) {
                        viewModel.retryLastOperation()
                    }
                    show()
                }
            
            AddEditAction.ShowDatePicker -> showDatePicker()
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
            
            { _, year_picker, month_picker, day_picker ->
                viewModel.changeDeadline(
                    LocalDate.of(
                        year_picker,
                        month_picker + 1,
                        day_picker
                    )
                )
            },
            year,
            month,
            day
        )
        
        datePicker.setOnCancelListener {
            binding.deadlineSwitch.isChecked = viewModel.state.value.deadline != null
        }
        
        datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(R.string.cancel)) { _, _ ->
            binding.deadlineSwitch.isChecked = viewModel.state.value.deadline != null
        }
        datePicker.show()
    }
    
    private fun showUrgencyMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.inflate(R.menu.urgency_menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.urgencyLowOption -> {
                    viewModel.changeUrgency(TodoUrgency.LOW)
                    true
                }
                
                R.id.urgencyStandardOption -> {
                    viewModel.changeUrgency(TodoUrgency.STANDARD)
                    true
                }
                
                R.id.urgencyHighOption -> {
                    viewModel.changeUrgency(TodoUrgency.HIGH)
                    true
                }
                
                else -> false
            }
        }
        popup.show()
    }
    
    private fun updateUI(state: TodoItem) {
        with(binding) {
            saveButton.isEnabled = state.text.isNotBlank()
            if (state.deadline != null) {
                deadlineSwitch.isChecked = true
                with(makeUntilDate) {
                    visibility = View.VISIBLE
                    text =
                        state.deadline!!.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
                }
            } else {
                deadlineSwitch.isChecked = false
                makeUntilDate.visibility = View.INVISIBLE
            }
            if (titleEditText.text?.isEmpty() == true) {
                // чтобы задать текст только при первом получении тудушки
                titleEditText.setText(state.text)
            }
            
            urgencyState.text = when (viewModel.state.value.urgency) {
                TodoUrgency.LOW -> getString(R.string.low_urgency)
                TodoUrgency.STANDARD -> getString(R.string.standard_urgency)
                TodoUrgency.HIGH -> getString(R.string.high_urgency)
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val TODO_ID_KEY = "todo_id"
        
        fun createArgumentsForEditing(todoId: String): Bundle {
            return bundleOf(TODO_ID_KEY to todoId)
        }
    }
}
