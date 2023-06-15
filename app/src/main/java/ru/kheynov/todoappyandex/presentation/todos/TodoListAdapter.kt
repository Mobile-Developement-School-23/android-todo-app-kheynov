package ru.kheynov.todoappyandex.presentation.todos

import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.databinding.TodosListItemBinding
import ru.kheynov.todoappyandex.domain.entities.TodoItem
import ru.kheynov.todoappyandex.domain.entities.TodoUrgency
import java.time.format.DateTimeFormatter

class TodoListAdapter(
    val onTodoLongClick: (TodoItem) -> Unit = {},
    val onTodoDetailsClick: (TodoItem) -> Unit = {},
    val onTodoCheckboxClick: (item: TodoItem, isChecked: Boolean) -> Unit = { _, _ -> },
) : ListAdapter<TodoItem, TodoListAdapter.TodoViewHolder>(DiffCallback()) {
    
    private class DiffCallback : DiffUtil.ItemCallback<TodoItem>() {
        override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean =
            oldItem.id == newItem.id
        
        override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean =
            oldItem == newItem
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding =
            TodosListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
    
    override fun getItemCount(): Int = currentList.size
    
    inner class TodoViewHolder(private val binding: TodosListItemBinding) :
        RecyclerView.ViewHolder(binding.root), CompoundButton.OnCheckedChangeListener {
        private var onBind = false
        
        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            if (!onBind) {
                notifyItemChanged(adapterPosition)
                onTodoCheckboxClick(currentList[adapterPosition], isChecked)
            }
        }
        
        fun bind(todo: TodoItem) {
            binding.apply {
                root.visibility = View.VISIBLE
                root.setOnLongClickListener {
                    onTodoLongClick(todo)
                    true
                }
                itemText.apply {
                    println(todo.isDone)
                    paintFlags = if (todo.isDone)
                        paintFlags or STRIKE_THRU_TEXT_FLAG
                    else
                        paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
                    text = todo.text
                }
                
                dateText.visibility = View.GONE
                todo.deadline?.let { dateTime ->
                    dateText.visibility = View.VISIBLE
                    dateText.text =
                        itemView.context.getString(
                            R.string.make_until_placeholder,
                            dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                        )
                }
                
                checkBox.apply {
                    onBind = true
                    isChecked = todo.isDone
                    onBind = false
                    setOnCheckedChangeListener(this@TodoViewHolder)
                }
                
                infoIcon.setOnClickListener {
                    onTodoDetailsClick(todo)
                }
                
                urgencyIndicator.apply {
                    when (todo.urgency) {
                        TodoUrgency.LOW -> {
                            visibility = View.VISIBLE
                            setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.baseline_arrow_downward_24
                                )
                            )
                            setColorFilter(ContextCompat.getColor(context, R.color.gray))
                        }
                        
                        TodoUrgency.STANDARD -> visibility = View.INVISIBLE
                        TodoUrgency.HIGH -> {
                            visibility = View.VISIBLE
                            setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.ic_high_urgency
                                )
                            )
                            setColorFilter(ContextCompat.getColor(context, R.color.red))
                        }
                    }
                }
            }
        }
        
    }
}