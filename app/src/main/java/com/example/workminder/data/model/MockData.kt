package com.example.workminder.data.model

object MockData {
    const val userName = "Moisés"

    val tasks = listOf(
        Task(
            id = 1,
            title = "Documentación",
            subject = "Proyecto Integrador 2",
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
            subject = "Proyecto Integrador 2",
            dueDate = "01/03/2026",
            status = TaskStatus.IN_PROGRESS,
            urgency = TaskUrgency.MEDIUM,
            complexity = "Media",
            subtasks = listOf("Pantalla de login", "Pantalla de dashboard")
        ),
        Task(
            id = 3,
            title = "Póster",
            subject = "Proyecto Integrador 2",
            dueDate = "01/03/2026",
            status = TaskStatus.IN_PROGRESS,
            urgency = TaskUrgency.LOW,
            complexity = "Baja",
            notes = "Usar plantilla corporativa."
        ),
        Task(
            id = 4,
            title = "Práctica de laboratorio",
            subject = "Sistemas Operativos",
            dueDate = "05/03/2026",
            status = TaskStatus.PENDING,
            urgency = TaskUrgency.MEDIUM,
            complexity = "Media"
        ),
        Task(
            id = 5,
            title = "Presentación final",
            subject = "Cálculo Diferencial",
            dueDate = "08/03/2026",
            status = TaskStatus.PENDING,
            urgency = TaskUrgency.HIGH,
            complexity = "Alta"
        ),
        Task(
            id = 6,
            title = "Reporte de resultados",
            subject = "Química",
            dueDate = "12/03/2026",
            status = TaskStatus.PENDING,
            urgency = TaskUrgency.LOW,
            complexity = "Baja"
        ),
        Task(
            id = 7,
            title = "Examen parcial",
            subject = "Física",
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
