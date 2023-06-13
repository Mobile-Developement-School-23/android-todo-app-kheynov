package ru.kheynov.todoappyandex.presentation.todos

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.kheynov.todoappyandex.databinding.TodosListItemBinding
import ru.kheynov.todoappyandex.domain.entities.TodoItem
import java.time.format.DateTimeFormatter

class TodoListAdapter(
    val onTodoLongClick: (TodoItem) -> Unit,
    val onTodoCheckboxClick: (TodoItem) -> Unit,
) : ListAdapter<TodoItem, TodoListAdapter.TodoViewHolder>(DiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding =
            TodosListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
    
    override fun getItemCount(): Int = currentList.size
    
    private class DiffCallback : DiffUtil.ItemCallback<TodoItem>() {
        override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean =
            oldItem.id == newItem.id
        
        override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean =
            oldItem == newItem
    }
    
    inner class TodoViewHolder(private val binding: TodosListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: TodoItem) {
            binding.apply {
                root.setOnLongClickListener {
                    onTodoLongClick(todo)
                    true
                }
                itemText.apply {
                    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    text = todo.text
                }
                dateText.visibility = View.GONE
                todo.deadline?.let {
                    dateText.visibility = View.VISIBLE
                    dateText.text = it.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                }
                checkBox.apply {
                    isChecked = todo.isDone
                    setOnClickListener {
                        currentList[adapterPosition] = currentList[adapterPosition].copy(
                            isDone =
                            isChecked
                        )
                        onTodoCheckboxClick(todo)
                    }
                }
            }
        }
    }
}