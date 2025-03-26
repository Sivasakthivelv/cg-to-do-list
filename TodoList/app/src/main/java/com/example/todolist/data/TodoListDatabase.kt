package com.example.todolist.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TodoList::class], version = 2, exportSchema = true)
abstract class TodoListDatabase : RoomDatabase() {
    abstract fun todoListDao(): TodoListDao

    companion object {
        @Volatile
        private var INSTANCE: TodoListDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // SQL command to rename columns or add new columns
                database.execSQL("ALTER TABLE todo_list_table ADD COLUMN todoDetails TEXT")  // Adding new column
                database.execSQL("ALTER TABLE todo_list_table RENAME COLUMN taskName TO todoName")  // Renaming column
            }
        }

        fun getDatabase(context: Context): TodoListDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoListDatabase::class.java,
                    "todo_list_table"
                ).addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}