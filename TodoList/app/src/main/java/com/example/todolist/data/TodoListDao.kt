package com.example.todolist.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TodoListDao {
    @Insert
    suspend fun insert(todoList: TodoList)

    @Update
    suspend fun update(todoList: TodoList)

    @Delete
    suspend fun delete(todoList: TodoList)

    @Query("SELECT * FROM todo_list_table ORDER BY todoName COLLATE NOCASE ASC")
    fun getAllTasks(): LiveData<List<TodoList>>

    @Query("SELECT * FROM todo_list_table WHERE isCompleted = :isCompleted ORDER BY todoName COLLATE NOCASE ASC")
    fun getTasksByCompletion(isCompleted: Int): LiveData<MutableList<TodoList>>

    @Query("SELECT * FROM todo_list_table WHERE todoName LIKE :query ORDER BY todoName COLLATE NOCASE ASC")
    fun searchTasks(query: String): LiveData<MutableList<TodoList>>

    @Query("UPDATE todo_list_table SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskCompletion(taskId: Int,isCompleted: Int)

}