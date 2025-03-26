package com.example.todolist.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "todo_list_table")
data class TodoList(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var todoName: String="",
    var todoDetails: String?=null,
    var isCompleted: Boolean = false
)
