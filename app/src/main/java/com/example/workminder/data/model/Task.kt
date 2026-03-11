package com.example.workminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id: String,
    @SerializedName("task_title") val task_title: String,
    @SerializedName("due_date") val due_date: String, // String ISO format "YYYY-MM-DD"
    @SerializedName("urgency") var urgency: Double = 0.0,
    @SerializedName("completed_at") val completed_at: String? = null,
    @SerializedName("subject_id") val subject_id: String? = null,
    @SerializedName("task_status") val status: TaskStatus? = TaskStatus.PENDING,
    @SerializedName("complexity") val complexity: Int = 3,
    @SerializedName("importance") val importance: Int = 3,
    @SerializedName("extra_note") var extra_note: String? = null,
    @SerializedName("subtasks") val subtasks: List<Subtask> = emptyList(),
    @SerializedName("reminders") val reminders: List<Reminder> = emptyList(), // Dates in String ISO format
    var is_synced: Boolean = true
) {
    val title: String get() = task_title
    val dueDate: String get() = due_date
    val notes: String get() = extra_note ?: ""
    
    val displayDate: String get() {
        return try {
            if (due_date.contains("T")) {
                val datePart = due_date.split("T")[0]
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

enum class TaskLevel(val value: Int, val displayName: String) {
    VERY_LOW(1, "Muy baja"),
    LOW(2, "Baja"),
    MEDIUM(3, "Media"),
    HIGH(4, "Alta"),
    VERY_HIGH(5, "Muy alta");

    companion object {
        fun fromInt(value: Int): TaskLevel = entries.find { it.value == value } ?: MEDIUM
        fun fromDisplayName(name: String): TaskLevel = entries.find { it.displayName == name } ?: MEDIUM
    }
}

enum class TaskStatus(val displayName: String) {
    @SerializedName("Pendiente") PENDING("Pendiente"),
    @SerializedName("Completada") DONE("Completada"),
    @SerializedName("Atrasada") LATE("Atrasada")
}

enum class TaskUrgency(val displayName: String) {
    VERY_HIGH("Muy urgente"),
    HIGH("Urgente"),
    MEDIUM("Algo urgente"),
    LOW("Poco urgente"),
    VERY_LOW("No muy urgente")
}

fun calculateUrgency(importance: Int, complexity: Int, dueDateStr: String): Double {
    try {
        val dateFormat = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
        val dueDate = java.time.LocalDate.parse(dueDateStr.split("T")[0], dateFormat)
        val today = java.time.LocalDate.now()
        val daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, dueDate).coerceAtLeast(0)
        
        return ((importance * 3.0) + (complexity * 2.0)) / (daysUntil + 1.0)
    } catch (e: Exception) {
        return 1.0
    }
}

fun getUrgencyLevel(urgencyVal: Double): TaskUrgency {
    return when {
        urgencyVal >= 5.0 -> TaskUrgency.VERY_HIGH
        urgencyVal >= 4.0 -> TaskUrgency.HIGH
        urgencyVal >= 3.0 -> TaskUrgency.MEDIUM
        urgencyVal >= 2.0 -> TaskUrgency.LOW
        else -> TaskUrgency.VERY_LOW
    }
}

fun getWeightColor(weight: Int): String {
    return when (weight) {
        5 -> "#E53935" // Rojo
        4 -> "#FF9800" // Naranja
        3 -> "#FFD700" // Amarillo
        2 -> "#4CAF50" // Verde
        1 -> "#03A9F4" // Celeste
        else -> "#6B6B7B"
    }
}

fun getUrgencyColor(urgencyVal: Double): String {
    return when {
        urgencyVal >= 5.0 -> "#E53935"
        urgencyVal >= 4.0 -> "#FF9800"
        urgencyVal >= 3.0 -> "#FFD700"
        urgencyVal >= 2.0 -> "#4CAF50"
        else -> "#03A9F4"
    }
}
