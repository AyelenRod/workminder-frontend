package com.example.workminder.notifications

import android.content.Context
import androidx.work.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * ReminderScheduler — helper de alto nivel para programar/cancelar
 * recordatorios de tareas usando WorkManager.
 *
 * Uso desde cualquier Composable o ViewModel:
 *   val scheduler = ReminderScheduler(context)
 *   scheduler.schedule(
 *       taskId    = task.id,
 *       taskTitle = task.task_title,
 *       dueDateStr = task.due_date,         // "dd/MM/yyyy"
 *       daysBefore = listOf(1, 3)           // días de anticipación
 *   )
 */
class ReminderScheduler(private val context: Context) {

    private val workManager = WorkManager.getInstance(context)

    /**
     * Programa una o más notificaciones para una tarea.
     * @param dueDateStr fecha en formato "dd/MM/yyyy"
     * @param daysBefore lista de días de antelación, ej. [1, 3, 7]
     */
    fun schedule(
        taskId: String,
        taskTitle: String,
        dueDateStr: String,
        daysBefore: List<Int>
    ) {
        // Parsear la fecha de entrega
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val dueDate = try {
            LocalDate.parse(dueDateStr, formatter).atTime(22, 0) // 10 PM del día de entrega
        } catch (e: Exception) {
            return // fecha inválida, no programar
        }

        daysBefore.forEach { days ->
            val notifyAt: LocalDateTime = dueDate.minusDays(days.toLong())
            val now = LocalDateTime.now()

            if (notifyAt.isAfter(now)) {
                val delayMs = notifyAt
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli() - System.currentTimeMillis()

                val data = workDataOf(
                    ReminderWorker.KEY_TASK_ID    to taskId,
                    ReminderWorker.KEY_TASK_TITLE to taskTitle,
                    ReminderWorker.KEY_DAYS_BEFORE to days
                )

                val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    // Tag único = taskId + días antes → permite cancelar individualmente
                    .addTag("${taskId}_${days}d")
                    .build()

                workManager.enqueueUniqueWork(
                    "${taskId}_${days}d",
                    ExistingWorkPolicy.REPLACE,
                    request
                )
            }
        }
    }

    /**
     * Cancela TODOS los recordatorios de una tarea (al editarla o eliminarla).
     */
    fun cancelAll(taskId: String, daysBefore: List<Int> = listOf(1, 2, 3, 7)) {
        daysBefore.forEach { days ->
            workManager.cancelUniqueWork("${taskId}_${days}d")
        }
    }
}
