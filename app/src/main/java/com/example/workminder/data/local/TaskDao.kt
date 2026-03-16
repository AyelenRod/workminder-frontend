package com.example.workminder.data.local

import androidx.room.*
import com.example.workminder.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<Task>>

    // Reemplaza o añade en TaskDao.kt
    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY due_date ASC")
    fun getTasksByUser(userId: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    @Update
    suspend fun updateTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)
    
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}
