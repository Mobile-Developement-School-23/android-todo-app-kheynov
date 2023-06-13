package ru.kheynov.todoappyandex.presentation.todos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.kheynov.todoappyandex.databinding.FragmentMainScreenBinding


class MainScreenFragment : Fragment() {
    private val viewModel: MainScreenViewModel by viewModels()
    
    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabAddTodo.setOnClickListener {
            //TODO
        }
        binding.rvTodoList.adapter = TodoListAdapter(
            onTodoCheckboxClick = { todoItem ->
                //TODO
            },
            onTodoLongClick = { todoItem ->
                //TODO
            }
        )
        binding.rvTodoList.layoutManager = LinearLayoutManager(requireContext())
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    
}