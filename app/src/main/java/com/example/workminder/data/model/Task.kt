package com.example.workminder.data.model

data class Task(
    val id: Int = 0,
    val title: String,
    val subject: String,
    val dueDate: String,
    val status: TaskStatus = TaskStatus.PENDING,
    val urgency: TaskUrgency = TaskUrgency.MEDIUM,
    val complexity: String = "Media",
    val notes: String = "",
    val subtasks: List<String> = emptyList()
)

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
