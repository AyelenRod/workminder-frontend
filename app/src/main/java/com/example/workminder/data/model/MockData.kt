package com.example.workminder.data.model

object MockData {
    const val userName = "Moisés"

    val subjects = mutableListOf(
        Subject(id = 1, name = "Proyecto Integrador 2", colorHex = "#FF5722"),
        Subject(id = 2, name = "Sistemas Operativos", colorHex = "#3F51B5"),
        Subject(id = 3, name = "Cálculo Diferencial", colorHex = "#009688"),
        Subject(id = 4, name = "Química", colorHex = "#E91E63"),
        Subject(id = 5, name = "Física", colorHex = "#9C27B0")
    )

    val tasks = listOf(
        Task(
            id = 1,
            title = "Documentación",
            subject = subjects.first { it.id == 1 },
            dueDate = "01/03/2026",
            status = TaskStatus.PENDING,
            urgency = TaskUrgency.HIGH,
            complexity = "Alta",
            notes = "Entregar antes del viernes.",
            subtasks = listOf("Redactar introducción", "Agregar diagramas")
        ),
        Task(
            id = 2,
            title = "Diseños en Figma",
            subject = subjects.first { it.id == 1 },
            dueDate = "01/03/2026",
            status = TaskStatus.IN_PROGRESS,
            urgency = TaskUrgency.MEDIUM,
            complexity = "Media",
            subtasks = listOf("Pantalla de login", "Pantalla de dashboard")
        ),
        Task(
            id = 3,
            title = "Póster",
            subject = subjects.first { it.id == 1 },
            dueDate = "01/03/2026",
            status = TaskStatus.IN_PROGRESS,
            urgency = TaskUrgency.LOW,
            complexity = "Baja",
            notes = "Usar plantilla corporativa."
        ),
        Task(
            id = 4,
            title = "Práctica de laboratorio",
            subject = subjects.first { it.id == 2 },
            dueDate = "05/03/2026",
            status = TaskStatus.PENDING,
            urgency = TaskUrgency.MEDIUM,
            complexity = "Media"
        ),
        Task(
            id = 5,
            title = "Presentación final",
            subject = subjects.first { it.id == 3 },
            dueDate = "08/03/2026",
            status = TaskStatus.PENDING,
            urgency = TaskUrgency.HIGH,
            complexity = "Alta"
        ),
        Task(
            id = 6,
            title = "Reporte de resultados",
            subject = subjects.first { it.id == 4 },
            dueDate = "12/03/2026",
            status = TaskStatus.PENDING,
            urgency = TaskUrgency.LOW,
            complexity = "Baja"
        ),
        Task(
            id = 7,
            title = "Examen parcial",
            subject = subjects.first { it.id == 5 },
            dueDate = "15/03/2026",
            status = TaskStatus.PENDING,
            urgency = TaskUrgency.MEDIUM,
            complexity = "Alta"
        )
    )

    // Pending tasks count
    val pendingCount get() = tasks.count { it.status == TaskStatus.PENDING || it.status == TaskStatus.IN_PROGRESS }

    // Tasks considered late (none for now)
    val lateCount get() = tasks.count { it.status == TaskStatus.LATE }

    // Suggested tasks sorted by urgency (HIGH first)
    val suggestedTasks get() = tasks
        .filter { it.status != TaskStatus.DONE }
        .sortedBy { it.urgency.ordinal }
        .take(3)

    // Tasks for "Esta semana" (first 3)
    val thisWeekTasks get() = tasks.take(3)

    // Tasks for "Siguiente semana" (next 2)
    val nextWeekTasks get() = tasks.drop(3).take(2)

    // Tasks for "Más tarde" (rest)
    val laterTasks get() = tasks.drop(5)
}
