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
    @SerializedName("task_status") var status: TaskStatus = TaskStatus.PENDING,
    @SerializedName("complexity") val complexity: Int = 3,
    @SerializedName("extra_note") var notes: String = "",
    @SerializedName("importance") val importance: Int = 3,
    val subtasks: List<Subtask> = emptyList(),
    val reminders: List<Int> = emptyList()
) {
    val title: String get() = task_title
    val dueDate: String get() = due_date
}

enum class TaskStatus(val displayName: String) {
    PENDING("Pendiente"),
    IN_PROGRESS("En progreso"),
    DONE("Terminada"),
    LATE("Atrasada")
}

enum class TaskUrgency(val displayName: String) {
    HIGH("Muy urgente"),
    MEDIUM("Algo urgente"),
    LOW("Muy poco urgente")
}

fun getTaskUrgency(urgencyVal: Double): TaskUrgency {
    return when {
        urgencyVal >= 0.7 -> TaskUrgency.HIGH
        urgencyVal >= 0.4 -> TaskUrgency.MEDIUM
        else -> TaskUrgency.LOW
    }
}

fun getTaskSubjectName(subjectId: String?): String {
    return MockData.subjects.find { it.id == subjectId }?.subject_name ?: "Sin materia"
}

fun getTaskSubjectColor(subjectId: String?): String {
    return MockData.subjects.find { it.id == subjectId }?.color ?: "#808080"
}    

