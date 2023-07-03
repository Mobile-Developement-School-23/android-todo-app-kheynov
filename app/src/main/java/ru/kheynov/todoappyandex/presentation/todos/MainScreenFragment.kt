package ru.kheynov.todoappyandex.presentation.todos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.databinding.FragmentMainScreenBinding
import ru.kheynov.todoappyandex.presentation.editor.TodoFragment
import ru.kheynov.todoappyandex.presentation.todos.stateHolders.MainScreenAction
import ru.kheynov.todoappyandex.presentation.todos.stateHolders.MainScreenState

@AndroidEntryPoint
class MainScreenFragment : Fragment() {
    private val viewModel: MainScreenViewModel by viewModels()
    private lateinit var navController: NavController
    
    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var recyclerView: RecyclerView
    
    private val rvAdapter = TodoListAdapter(
        onTodoCheckboxClick = { todoItem, state ->
            viewModel.setTodoState(todoItem, state)
        },
        onTodoDetailsClick = { todoItem ->
            viewModel.editTodo(todoItem)
        }
    )
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        
        with(binding) {
            fabAddTodo.setOnClickListener {
                viewModel.addTodo()
            }
            recyclerView = rvTodoList
            toggleDoneTasks.setOnClickListener {
                viewModel.toggleDoneTasks()
            }
            updateButton.setOnClickListener { viewModel.updateTodos() }
        }
        
        with(recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = rvAdapter
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.state.collect(::updateUI)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actions.collect(::handleActions)
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        viewModel.fetchTodos()
    }
    
    private fun handleActions(action: MainScreenAction) {
        when (action) {
            MainScreenAction.NavigateToAdding -> {
                navController.navigate(R.id.action_todosFragment_to_todoDetailFragment)
            }
            
            is MainScreenAction.NavigateToEditing -> {
                navController.navigate(
                    R.id.action_todosFragment_to_todoDetailFragment,
                    TodoFragment.createArgumentsForEditing(action.id)
                )
            }
            
            is MainScreenAction.ToggleDoneTasks -> {
                with(binding.toggleDoneTasks) {
                    setImageDrawable(
                        if (action.state) {
                            AppCompatResources.getDrawable(context, R.drawable.ic_opened_eye)
                        } else {
                            AppCompatResources.getDrawable(context, R.drawable.ic_closed_eye)
                        }
                    )
                }
                val data = (viewModel.state.value as? MainScreenState.Loaded)?.data ?: return
                (recyclerView.adapter as TodoListAdapter)
                    .submitList(if (action.state) data else data.filter { !it.isDone })
            }
            
            is MainScreenAction.ShowError ->
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
        }
    }
    
    private fun updateUI(state: MainScreenState) {
        with(binding) {
            progressCircular.visibility =
                if (state is MainScreenState.Loading) View.VISIBLE else View.GONE
            rvTodoList.visibility = if (state is MainScreenState.Empty) View.GONE else View.VISIBLE
            noDataImage.visibility = if (state is MainScreenState.Empty) View.VISIBLE else View.GONE
            doneCountText.visibility =
                if (state is MainScreenState.Loaded) View.VISIBLE else View.GONE
            if (state is MainScreenState.Loaded) {
                doneCountText.text = getString(R.string.tasks_done, state.data.count { it.isDone })
                (rvTodoList.adapter as TodoListAdapter).submitList(state.data)
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
