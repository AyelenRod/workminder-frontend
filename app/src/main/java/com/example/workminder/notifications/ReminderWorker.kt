package com.example.workminder.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.workminder.R

/**
 * ReminderWorker — se ejecuta en background vía WorkManager.
 * Recibe el título y el nombre de la tarea como datos ("input data")
 * y lanza una notificación local al usuario.
 */
class ReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    companion object {
        const val CHANNEL_ID   = "workminder_reminders"
        const val CHANNEL_NAME = "Recordatorios de tareas"
        const val KEY_TASK_ID    = "task_id"
        const val KEY_TASK_TITLE = "task_title"
        const val KEY_DAYS_BEFORE = "days_before"
    }

    override fun doWork(): Result {
        val taskTitle  = inputData.getString(KEY_TASK_TITLE)  ?: "Tarea"
        val daysBefore = inputData.getInt(KEY_DAYS_BEFORE, 1)

        val message = when (daysBefore) {
            0    -> "¡Hoy vence \"$taskTitle\"! No olvides entregarla."
            1    -> "Mañana vence \"$taskTitle\". ¡Termínala hoy!"
            else -> "Faltan $daysBefore días para entregar \"$taskTitle\"."
        }

        showNotification(taskTitle, message)
        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as NotificationManager

        // Crear canal (Android 8+)
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Recordatorios automáticos de tareas pendientes en WorkMinder"
        }
        notifManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("📚 Recordatorio: $title")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Usar el taskId como notificationId para evitar duplicados
        val notifId = inputData.getString(ReminderWorker.KEY_TASK_ID)
            ?.hashCode() ?: System.currentTimeMillis().toInt()

        notifManager.notify(notifId, notification)
    }
}
