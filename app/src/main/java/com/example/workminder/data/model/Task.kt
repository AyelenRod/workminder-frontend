package com.example.workminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String,
    @SerializedName("task_title") val task_title: String,
    @SerializedName("due_date") val due_date: String,
    @SerializedName("urgency") val urgency: Double,
    @SerializedName("completed_at") val completed_at: String? = null,
    @SerializedName("subject_id") val subject_id: String? = null,
    // Add dummy values to preserve legacy code for now
    var status: TaskStatus = TaskStatus.PENDING,
    val complexity: String = "Media",
    var notes: String = "",
    val subtasks: List<String> = emptyList(),
) {
    // Legacy support
    val taskTitle get() = task_title
    val dueDate get() = due_date
}

enum class TaskStatus(val displayName: String) {
    PENDING("Pendiente"),
    IN_PROGRESS("En progreso"),
    DONE("Terminada"),
    LATE("Atrasada")
}
