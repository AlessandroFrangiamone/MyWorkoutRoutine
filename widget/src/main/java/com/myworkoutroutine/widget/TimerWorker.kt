package com.myworkoutroutine.widget

import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class TimerWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        android.util.Log.d("TimerWorker", "doWork() started")
        val prefs = context.getSharedPreferences(WIDGET_PREFS, Context.MODE_PRIVATE)
        var secondsCounter = 0

        while (true) {
            val isRunning = prefs.getBoolean(KEY_IS_RUNNING, false)
            android.util.Log.d("TimerWorker", "Loop iteration - isRunning: $isRunning")

            if (!isRunning) {
                android.util.Log.d("TimerWorker", "Timer stopped, exiting worker")
                return Result.success()
            }

            val remaining = prefs.getInt(KEY_REMAINING_SECONDS, 0)
            android.util.Log.d("TimerWorker", "Remaining seconds: $remaining")

            if (remaining <= 0) {
                android.util.Log.d("TimerWorker", "Timer finished, sending notification")
                // Timer finished - send notification
                sendNotification()

                // Stop timer
                prefs.edit()
                    .putBoolean(KEY_IS_RUNNING, false)
                    .putInt(KEY_REMAINING_SECONDS, 0)
                    .apply()

                // Update widget
                forceWidgetUpdate()

                return Result.success()
            }

            // Decrement timer
            prefs.edit()
                .putInt(KEY_REMAINING_SECONDS, remaining - 1)
                .apply()

            // Update widget only every 5 seconds (or when timer finishes/is low)
            // Glance widgets don't support high-frequency updates
            secondsCounter++
            if (secondsCounter % 5 == 0 || remaining <= 5) {
                android.util.Log.d("TimerWorker", "Updating widget at $remaining seconds")
                forceWidgetUpdate()
            }

            delay(1000)
        }
    }

    private suspend fun forceWidgetUpdate() {
        try {
            val glanceManager = GlanceAppWidgetManager(context)
            val glanceIds = glanceManager.getGlanceIds(WorkoutWidget::class.java)

            android.util.Log.d("TimerWorker", "Forcing update for ${glanceIds.size} Glance widgets")

            glanceIds.forEach { glanceId ->
                android.util.Log.d("TimerWorker", "Calling update() for glanceId: $glanceId")
                WorkoutWidget().update(context, glanceId)
            }
        } catch (e: Exception) {
            android.util.Log.e("TimerWorker", "Error forcing widget update", e)
        }
    }

    private fun sendNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Workout Timer",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Workout timer notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Timer Finished!")
            .setContentText("Your workout timer has completed")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val WIDGET_PREFS = "workout_widget_prefs"
        const val KEY_IS_RUNNING = "is_running"
        const val KEY_REMAINING_SECONDS = "remaining_seconds"
        const val KEY_SELECTED_TIMER = "selected_timer"
        const val KEY_CURRENT_CARD_INDEX = "current_card_index"
        const val NOTIFICATION_CHANNEL_ID = "workout_timer_channel"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME = "workout_timer_work"
    }
}
