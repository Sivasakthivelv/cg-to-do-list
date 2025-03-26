package com.example.todolist.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.data.TodoList
import com.example.todolist.databinding.TodoListItemBinding

class TodoAdapter(
    private val context: Context,
    private val listener: OnItemClickListener/*private val onTodoClick: (TodoList) -> Unit*/
) : ListAdapter<TodoList, TodoAdapter.ViewHolder>((TasksDiffUtil())) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            TodoListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    interface OnItemClickListener {
        //fun onDeleteClick(todo: TodoList, position: Int)
        fun onTaskCompletionChanged(todo: TodoList/*,iscompleted: Int*/)
        fun onEdit(todo: TodoList/*,iscompleted: Int*/)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taskData = getItem(position)
        with(holder.binding) {
            txtTaskTitle.text = taskData.todoName
            txtTaskDetails.text = taskData.todoDetails
            checkbox.isChecked = taskData.isCompleted

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                taskData.isCompleted = isChecked
               // listener.onTaskCompletionChanged(taskData)
//                if (isChecked){
//                    listener.onTaskCompletionChanged(taskData.id,1)
//                }else{
//                    listener.onTaskCompletionChanged(taskData.id,0)
//                }
            }
            checkbox.setOnClickListener {
                listener.onTaskCompletionChanged(taskData)
            }

            btnEdit.setOnClickListener {
               listener.onEdit(taskData)
            }

        }

    }

    inner class ViewHolder(val binding: TodoListItemBinding) : RecyclerView.ViewHolder(binding.root)

    // DiffCallback to optimize list updates
    class TasksDiffUtil : DiffUtil.ItemCallback<TodoList>() {
        override fun areItemsTheSame(oldItem: TodoList, newItem: TodoList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TodoList, newItem: TodoList): Boolean {
            return oldItem == newItem
        }
    }
}