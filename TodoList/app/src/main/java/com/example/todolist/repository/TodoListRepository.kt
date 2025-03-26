package com.example.todolist.repository

import androidx.lifecycle.LiveData
import com.example.todolist.data.TodoList
import com.example.todolist.data.TodoListDao

class TodoListRepository(private val todoListDao: TodoListDao) {

    suspend fun insert(todoList: TodoList) {
        todoListDao.insert(todoList)
    }

    suspend fun update(todoList: TodoList) {
        todoListDao.update(todoList)
    }

    suspend fun delete(todoList: TodoList) {
        todoListDao.delete(todoList)
    }

    fun getAllTasks(): LiveData<List<TodoList>> {
        return todoListDao.getAllTasks()
    }

    fun getTasksByStatus(completed: Int): LiveData<MutableList<TodoList>> {
        return todoListDao.getTasksByCompletion(completed)
    }

    suspend fun updateTaskById(id: Int,isCompleted: Int) {
        return todoListDao.updateTaskCompletion(id,isCompleted)
    }

    fun searchTasks(query: String): LiveData<MutableList<TodoList>> {
        return todoListDao.searchTasks("%$query%")
    }

}