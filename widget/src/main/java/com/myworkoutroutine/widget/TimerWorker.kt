package com.myworkoutroutine.widget

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.myworkoutroutine.core.domain.repository.WorkoutRepository
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

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
                // Timer finished - send notification with card name
                val cardName = getCurrentCardName()
                sendNotification(cardName)

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

    private fun forceWidgetUpdate() {
        try {
            android.util.Log.d("TimerWorker", "Triggering RemoteViews widget update")
            WorkoutAppWidgetProvider.updateWidget(context)
        } catch (e: Exception) {
            android.util.Log.e("TimerWorker", "Error forcing widget update", e)
        }
    }

    private suspend fun getCurrentCardName(): String {
        return try {
            val repository = getRepository(context)
            val prefs = context.getSharedPreferences(WIDGET_PREFS, Context.MODE_PRIVATE)

            val currentTraining = repository.getCurrentTrainingPlan().first()
            if (currentTraining != null) {
                val currentCardIndex = prefs.getInt(KEY_CURRENT_CARD_INDEX, 0)
                val cardIds = currentTraining.workoutCardIds

                if (currentCardIndex in cardIds.indices) {
                    val cardId = cardIds[currentCardIndex]
                    val card = repository.getWorkoutCardById(cardId).first()
                    card?.name ?: "Workout"
                } else {
                    "Workout"
                }
            } else {
                "Workout"
            }
        } catch (e: Exception) {
            android.util.Log.e("TimerWorker", "Error getting card name", e)
            "Workout"
        }
    }

    private fun sendNotification(cardName: String) {
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
            .setContentTitle(context.getString(R.string.notification_title_card_completed))
            .setContentText(context.getString(R.string.notification_text_completed_card, cardName))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun getRepository(context: Context): WorkoutRepository {
        val appContext = context.applicationContext
        val entryPoint = EntryPointAccessors.fromApplication(
            appContext,
            WidgetEntryPoint::class.java
        )
        return entryPoint.workoutRepository()
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
