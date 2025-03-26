package com.example.todolist.activity

import android.annotation.SuppressLint

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.repository.TodoListRepository
import com.example.todolist.viewModel.TodoListViewModelFactory
import com.example.todolist.adapter.TodoAdapter
import com.example.todolist.data.TodoList
import com.example.todolist.data.TodoListDatabase
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.viewModel.TodoListViewModel

class MainActivity : AppCompatActivity(), TodoAdapter.OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TodoAdapter
    private lateinit var todoListViewModel: TodoListViewModel
    private lateinit var todoListRepository: TodoListRepository
    private var tasksSelection: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvData.layoutManager = LinearLayoutManager(this)

        setSupportActionBar(binding.toolbar)
        todoListRepository =
            TodoListRepository(TodoListDatabase.getDatabase(application).todoListDao())
        val factory = TodoListViewModelFactory(application, todoListRepository)
        todoListViewModel = ViewModelProvider(this, factory)[TodoListViewModel::class.java]
        adapter = TodoAdapter(this, this)
        dataLoading()
        binding.rvData.adapter = adapter

        binding.addTaskButton.setOnClickListener {
            callAddEditDialog(2, TodoList())
        }



        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                p0?.let {
                    val newText = it.toString().trim()
                    if (newText.isNotEmpty()) {
                        todoListViewModel.searchTasks(newText)
                            .observe(this@MainActivity, Observer { tasks ->
                                adapter.submitList(tasks)
                            })
                    } else {
                        dataLoading()
                    }
                }
            }

        })
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    showAlertDialogBeforeDelete(position)
                }

            }
        // Attach ItemTouchHelper to RecyclerView
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvData)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun callAddEditDialog(addOrEdit: Int, todo: TodoList) {
        // Inflate the custom layout
        val customLayout = layoutInflater.inflate(R.layout.add_edit_todo, null)
        val customDialogTitle: EditText = customLayout.findViewById(R.id.edt_todo_name)
        val customDialogDescription: EditText = customLayout.findViewById(R.id.edt_todo_detail)
        val customDialogOkButton: Button = customLayout.findViewById(R.id.btn_submit)
        if (addOrEdit == 1) {
            customDialogTitle.setText(todo.todoName)
            customDialogDescription.setText(todo.todoDetails)
            customDialogOkButton.text = resources.getString(R.string.edit)
        }
        val builder = AlertDialog.Builder(this).setView(customLayout).setCancelable(true)
        val dialog = builder.create()
        customDialogOkButton.setOnClickListener {
            if (customDialogTitle.text.isNotEmpty() && customDialogDescription.text.isNotEmpty()) {
                val newTodo = TodoList(
                    todoName = customDialogTitle.text.toString(),
                    todoDetails = customDialogDescription.text.toString()
                )
                if (addOrEdit == 1) {
                    todo.todoName = customDialogTitle.text.toString()
                    todo.todoDetails = customDialogDescription.text.toString()
                    todoListViewModel.updateTask(todo)
                    adapter.notifyDataSetChanged()
                } else {
                    todoListViewModel.addTask(newTodo)
                }
                Toast.makeText(this, resources.getString(R.string.todo_added), Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, resources.getString(R.string.please_enter), Toast.LENGTH_SHORT)
                    .show()

            }
        }
        // Create and show the dialog
        dialog.show()
    }

    fun showAlertDialogBeforeDelete(position: Int) {
        val alterDialog = AlertDialog.Builder(this@MainActivity)
        alterDialog.setMessage(resources.getString(R.string.delete_alert))
        alterDialog.setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
            val toDo = adapter.currentList[position]
            // Delete the user from ViewModel (and thus from the database)
            todoListViewModel.deleteTask(toDo)
            binding.txtEmptyList.visibility = View.GONE
        }
        alterDialog.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
            adapter.notifyItemChanged(position)
            dialog.cancel()
        }
        // Create and show the dialog
        val dialog = alterDialog.create()
        dialog.show()
    }


    override fun onEdit(todo: TodoList) {
        callAddEditDialog(1, todo)
    }

    // When a task's completion status changes, update it in the database
    override fun onTaskCompletionChanged(todo: TodoList) {
        todoListViewModel.updateTask(todo)
        when (tasksSelection) {
            1 -> {
                onCompletedTasksLoading()
            }

            2 -> {
                onInCompletedTasksLoading()
            }

            else -> {
                dataLoading()
            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)  // Inflate the menu resource
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.all_todo -> {
                tasksSelection = 0
                dataLoading()
            }

            R.id.completed_todo -> {
                tasksSelection = 1
                onCompletedTasksLoading()
            }

            R.id.inCompleted_todo -> {
                tasksSelection = 2
                onInCompletedTasksLoading()
            }


        }
        return super.onOptionsItemSelected(item)
    }

    fun dataLoading() {
        todoListViewModel.getAllTasks().observe(this, Observer { tasks ->
            tasks?.let {
                if (tasks.isEmpty()) {
                    binding.txtEmptyList.visibility = View.VISIBLE
                } else {
                    binding.txtEmptyList.visibility = View.GONE
                }
                adapter.submitList(it)
            }
        })
    }

    private fun onCompletedTasksLoading() {
        todoListViewModel.getTasksByStatus(1).observe(this@MainActivity, Observer { tasks ->
            tasks?.let {
                if (tasks.isEmpty()) {
                    binding.txtEmptyList.visibility = View.VISIBLE
                } else {
                    binding.txtEmptyList.visibility = View.GONE
                }
                adapter.submitList(tasks)
            }
        })
    }

    private fun onInCompletedTasksLoading() {
        todoListViewModel.getTasksByStatus(0).observe(this@MainActivity, Observer { tasks ->
            tasks?.let {
                adapter.submitList(tasks)
                if (tasks.isEmpty()) {
                    binding.txtEmptyList.visibility = View.VISIBLE
                } else {
                    binding.txtEmptyList.visibility = View.GONE
                }
            }
        })
    }
}

