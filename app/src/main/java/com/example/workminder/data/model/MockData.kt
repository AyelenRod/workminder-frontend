package com.example.workminder.data.model

object MockData {
    const val userName = "Moisés"

    val subjects = androidx.compose.runtime.mutableStateListOf(
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
            complexity = 5,
            extra_note = "Entregar antes del viernes.",
            subtasks = listOf(
                Subtask("s1", "1", "Redactar introducción"),
                Subtask("s2", "1", "Agregar diagramas")
            )
        ),
        Task(
            id = "2",
            task_title = "Diseños en Figma",
            subject_id = "1",
            due_date = "01/03/2026",
            status = TaskStatus.PENDING,
            urgency = 0.5,
            complexity = 3,
            subtasks = listOf(
                Subtask("s3", "2", "Pantalla de login"),
                Subtask("s4", "2", "Pantalla de dashboard")
            )
        ),
        Task(
            id = "3",
            task_title = "Póster",
            subject_id = "1",
            due_date = "01/03/2026",
            status = TaskStatus.PENDING,
            urgency = 0.2,
            complexity = 1,
            extra_note = "Usar plantilla corporativa."
        ),
        Task(
            id = "4",
            task_title = "Práctica de laboratorio",
            subject_id = "2",
            due_date = "05/03/2026",
            status = TaskStatus.PENDING,
            urgency = 0.5,
            complexity = 3
        ),
        Task(
            id = "5",
            task_title = "Presentación final",
            subject_id = "3",
            due_date = "08/03/2026",
            status = TaskStatus.PENDING,
            urgency = 0.8,
            complexity = 5
        ),
        Task(
            id = "6",
            task_title = "Reporte de resultados",
            subject_id = "4",
            due_date = "12/03/2026",
            status = TaskStatus.PENDING,
            urgency = 0.3,
            complexity = 1
        ),
        Task(
            id = "7",
            task_title = "Examen parcial",
            subject_id = "5",
            due_date = "15/03/2026",
            status = TaskStatus.PENDING,
            urgency = 0.6,
            complexity = 5
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

    fun removeSubject(subjectId: String) {
        subjects.removeAll { it.id == subjectId }
    }

    fun updateSubject(updatedSubject: Subject) {
        val index = subjects.indexOfFirst { it.id == updatedSubject.id }
        if (index != -1) {
            subjects[index] = updatedSubject
        }
    }

    val pendingCount get() = tasks.count { it.status == TaskStatus.PENDING || it.status == TaskStatus.PENDING }

    val lateCount get() = tasks.count { it.status == TaskStatus.LATE }

    val suggestedTasks get() = tasks
        .filter { it.status != TaskStatus.DONE }
        .sortedByDescending { it.urgency }
        .take(3)

    val thisWeekTasks get() = tasks.take(3)

    val nextWeekTasks get() = tasks.drop(3).take(2)

    val laterTasks get() = tasks.drop(5)
}
