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
    @SerializedName("task_status") val status: TaskStatus? = TaskStatus.PENDING,
    @SerializedName("complexity") val complexity: Int = 3,
    @SerializedName("extra_note") var notes: String = "",
    @SerializedName("importance") val importance: Int = 3,
    val subtasks: List<Subtask> = emptyList(),
    val reminders: List<Int> = emptyList()
) {
    val title: String get() = task_title
    val dueDate: String get() = due_date
    
    val displayDate: String get() {
        return try {
            // ISO format is usually "YYYY-MM-DDTHH:MM:SS"
            if (due_date.contains("T")) {
                val datePart = due_date.split("T")[0] // YYYY-MM-DD
                val parts = datePart.split("-")
                "${parts[2]}/${parts[1]}/${parts[0]}"
            } else if (due_date.contains("-")) {
                val parts = due_date.split("-")
                "${parts[2]}/${parts[1]}/${parts[0]}"
            } else {
                due_date
            }
        } catch (e: Exception) {
            due_date
        }
    }
}

enum class TaskStatus(val displayName: String) {
    @SerializedName("Pendiente") PENDING("Pendiente"),
    @SerializedName("En progreso") IN_PROGRESS("En progreso"),
    @SerializedName("Completada") DONE("Terminada"),
    @SerializedName("Atrasada") LATE("Atrasada")
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
    return "Sin materia"
}

fun getTaskSubjectColor(subjectId: String?): String {
    return "#808080"
}    
