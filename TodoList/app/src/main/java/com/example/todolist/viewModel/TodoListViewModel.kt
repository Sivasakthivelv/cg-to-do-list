package com.example.todolist.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.todolist.repository.TodoListRepository
import com.example.todolist.data.TodoList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoListViewModel(application: Application, private val repository: TodoListRepository) :
    AndroidViewModel(application) {


   // val allTasks: LiveData<List<TodoList>> = repository.allTasks

    private val _tasks = MutableLiveData<List<TodoList>>()
    val tasks: LiveData<List<TodoList>> get() = _tasks

    init {
        loadTodo()
    }

    fun loadTodo() {
        viewModelScope.launch {
            _tasks.value = repository.getAllTasks().value
        }
    }


    fun addTask(todoList: TodoList) {
        viewModelScope.launch {
            repository.insert(todoList)
            loadTodo()
        }
    }

    fun updateTask(todoList: TodoList) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(todoList)
        }
    }

    fun updateTaskById(id:Int,isCompleted: Int) {
        viewModelScope.launch {
            repository.updateTaskById(id,isCompleted)
            loadTodo()
        }
    }

    fun deleteTask(todoList: TodoList) {
        viewModelScope.launch {
            repository.delete(todoList)
        }
    }
    fun getAllTasks(): LiveData<List<TodoList>> {
        return repository.getAllTasks()
    }

    fun getTasksByStatus(completed: Int): LiveData<MutableList<TodoList>> {
        return repository.getTasksByStatus(completed)
    }

    fun searchTasks(query: String): LiveData<MutableList<TodoList>> {
        return repository.searchTasks(query)
    }


}


