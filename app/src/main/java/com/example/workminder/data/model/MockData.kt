package com.example.workminder.data.model

object MockData {
    const val userName = "Moisés"

    val subjects = mutableListOf(
        Subject(id = "1", subject_name = "Proyecto Integrador 2", color = "#FF5722"),
        Subject(id = "2", subject_name = "Sistemas Operativos", color = "#3F51B5"),
        Subject(id = "3", subject_name = "Cálculo Diferencial", color = "#009688"),
        Subject(id = "4", subject_name = "Química", color = "#E91E63"),
        Subject(id = "5", subject_name = "Física", color = "#9C27B0")
    )

    val tasks = androidx.compose.runtime.mutableStateListOf(
        Task(
            id = "1",
            task_title = "Documentación",
            subject_id = "1",
            due_date = "01/03/2026",
            status = TaskStatus.PENDING,
            urgency = 0.9,
            complexity = "Alta",
            notes = "Entregar antes del viernes.",
            subtasks = listOf("Redactar introducción", "Agregar diagramas")
        ),
        Task(
            id = "2",
            task_title = "Diseños en Figma",
            subject_id = "1",
            due_date = "01/03/2026",
            status = TaskStatus.IN_PROGRESS,
            urgency = 0.5,
            complexity = "Media",
            subtasks = listOf("Pantalla de login", "Pantalla de dashboard")
        ),
        Task(
            id = "3",
            task_title = "Póster",
            subject_id = "1",
            due_date = "01/03/2026",
            status = TaskStatus.IN_PROGRESS,
            urgency = 0.2,
            complexity = "Baja",
            notes = "Usar plantilla corporativa."
        ),
        Task(
            id = "4",
            task_title = "Práctica de laboratorio",
            subject_id = "2",
            due_date = "05/03/2026",
            status = TaskStatus.PENDING,
            urgency = 0.5,
            complexity = "Media"
        ),
        Task(
            id = "5",
            task_title = "Presentación final",
            subject_id = "3",
            due_date = "08/03/2026",
            status = TaskStatus.PENDING,
            urgency = 0.8,
            complexity = "Alta"
        ),
        Task(
            id = "6",
            task_title = "Reporte de resultados",
            subject_id = "4",
            due_date = "12/03/2026",
            status = TaskStatus.PENDING,
            urgency = 0.3,
            complexity = "Baja"
        ),
        Task(
            id = "7",
            task_title = "Examen parcial",
            subject_id = "5",
            due_date = "15/03/2026",
            status = TaskStatus.PENDING,
            urgency = 0.6,
            complexity = "Alta"
        )
    )

    fun removeTask(taskId: String) {
        tasks.removeAll { it.id == taskId }
    }

    fun updateTask(updatedTask: Task) {
        val index = tasks.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            tasks[index] = updatedTask
        }
    }

    val pendingCount get() = tasks.count { it.status == TaskStatus.PENDING || it.status == TaskStatus.IN_PROGRESS }

    val lateCount get() = tasks.count { it.status == TaskStatus.LATE }

    val suggestedTasks get() = tasks
        .filter { it.status != TaskStatus.DONE }
        .sortedByDescending { it.urgency }
        .take(3)

    val thisWeekTasks get() = tasks.take(3)

    val nextWeekTasks get() = tasks.drop(3).take(2)

    val laterTasks get() = tasks.drop(5)
}
