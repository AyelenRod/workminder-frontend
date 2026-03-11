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
     * @param reminders lista de fechas/horas en formato "yyyy-MM-dd HH:mm"
     */
    fun schedule(
        taskId: String,
        taskTitle: String,
        dueDateStr: String,
        reminders: List<com.example.workminder.data.model.Reminder>
    ) {
        if (!com.example.workminder.data.remote.AuthManager.pushNotificationsEnabled) {
            return
        }

        reminders.forEachIndexed { index, reminder ->
            val reminderStr = reminder.reminderDate
            try {
                val notifyAt = if (reminderStr.contains("T")) {
                    java.time.ZonedDateTime.parse(reminderStr).toLocalDateTime()
                } else {
                    val normalizedStr = reminderStr.replace("T", " ")
                    val formatter = if (normalizedStr.contains(":")) {
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    } else {
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    }
                    if (normalizedStr.contains(":")) {
                        LocalDateTime.parse(normalizedStr, formatter)
                    } else {
                        LocalDate.parse(normalizedStr, formatter).atStartOfDay()
                    }
                }

                val now = LocalDateTime.now()
                android.util.Log.d("ReminderScheduler", "Programando $taskTitle para $notifyAt")

                if (notifyAt.isAfter(now)) {
                    val delayMs = notifyAt
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli() - System.currentTimeMillis()

                    val data = workDataOf(
                        ReminderWorker.KEY_TASK_ID    to taskId,
                        ReminderWorker.KEY_TASK_TITLE to taskTitle,
                        ReminderWorker.KEY_DAYS_BEFORE to index // Usamos el índice como diferenciador
                    )

                    val tag = "reminder_${taskId}_$index"
                    val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                        .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                        .setInputData(data)
                        .addTag(tag)
                        .addTag("task_$taskId") // Tag general para la tarea
                        .build()

                    workManager.enqueueUniqueWork(
                        tag,
                        ExistingWorkPolicy.REPLACE,
                        request
                    )
                }
            } catch (e: Exception) {
                println("ReminderScheduler: Error programando recordatorio '$reminderStr': ${e.message}")
            }
        }
    }

    /**
     * Cancela TODOS los recordatorios de una tarea.
     */
    fun cancelAll(taskId: String) {
        workManager.cancelAllWorkByTag("task_$taskId")
    }
}
