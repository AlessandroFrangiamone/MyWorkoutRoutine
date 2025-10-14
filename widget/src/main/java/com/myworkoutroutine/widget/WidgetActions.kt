package com.myworkoutroutine.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

object WidgetActions {
    fun selectTimer(seconds: Int) = when (seconds) {
        30 -> actionRunCallback<SelectTimer30Action>()
        60 -> actionRunCallback<SelectTimer60Action>()
        90 -> actionRunCallback<SelectTimer90Action>()
        120 -> actionRunCallback<SelectTimer120Action>()
        else -> actionRunCallback<SelectTimer30Action>()
    }

    fun startTimer() = actionRunCallback<StartTimerAction>()

    fun pauseTimer() = actionRunCallback<PauseTimerAction>()

    fun resetTimer() = actionRunCallback<ResetTimerAction>()

    fun previousCard() = actionRunCallback<PreviousCardAction>()

    fun nextCard() = actionRunCallback<NextCardAction>()
}

sealed class SelectTimerAction : ActionCallback {
    abstract val seconds: Int

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        android.util.Log.d("SelectTimerAction", "Timer selected: $seconds seconds")
        val prefs = context.getSharedPreferences(TimerWorker.WIDGET_PREFS, Context.MODE_PRIVATE)

        prefs.edit()
            .putInt(TimerWorker.KEY_SELECTED_TIMER, seconds)
            .putInt(TimerWorker.KEY_REMAINING_SECONDS, seconds)
            .apply()

        android.util.Log.d("SelectTimerAction", "Timer prefs saved")

        // Update the specific widget that triggered this action
        WorkoutWidget().update(context, glanceId)
    }
}

class SelectTimer30Action : SelectTimerAction() {
    override val seconds = 30
}

class SelectTimer60Action : SelectTimerAction() {
    override val seconds = 60
}

class SelectTimer90Action : SelectTimerAction() {
    override val seconds = 90
}

class SelectTimer120Action : SelectTimerAction() {
    override val seconds = 120
}

class StartTimerAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        android.util.Log.d("StartTimerAction", "START button clicked")
        val prefs = context.getSharedPreferences(TimerWorker.WIDGET_PREFS, Context.MODE_PRIVATE)
        val selectedTimer = prefs.getInt(TimerWorker.KEY_SELECTED_TIMER, 0)

        android.util.Log.d("StartTimerAction", "Selected timer: $selectedTimer seconds")

        if (selectedTimer <= 0) {
            android.util.Log.w("StartTimerAction", "No timer selected, aborting")
            return
        }

        val remainingSeconds = prefs.getInt(TimerWorker.KEY_REMAINING_SECONDS, selectedTimer)

        prefs.edit()
            .putBoolean(TimerWorker.KEY_IS_RUNNING, true)
            .putInt(TimerWorker.KEY_REMAINING_SECONDS, remainingSeconds)
            .apply()

        android.util.Log.d("StartTimerAction", "Timer started with $remainingSeconds seconds")

        // Start WorkManager
        val workRequest = OneTimeWorkRequestBuilder<TimerWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            TimerWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        android.util.Log.d("StartTimerAction", "WorkManager enqueued")

        // Update the specific widget that triggered this action
        WorkoutWidget().update(context, glanceId)
    }
}

class PauseTimerAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val prefs = context.getSharedPreferences(TimerWorker.WIDGET_PREFS, Context.MODE_PRIVATE)

        prefs.edit()
            .putBoolean(TimerWorker.KEY_IS_RUNNING, false)
            .apply()

        // Cancel WorkManager
        WorkManager.getInstance(context).cancelUniqueWork(TimerWorker.WORK_NAME)

        // Update the specific widget that triggered this action
        WorkoutWidget().update(context, glanceId)
    }
}

class ResetTimerAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val prefs = context.getSharedPreferences(TimerWorker.WIDGET_PREFS, Context.MODE_PRIVATE)
        val selectedTimer = prefs.getInt(TimerWorker.KEY_SELECTED_TIMER, 0)

        prefs.edit()
            .putBoolean(TimerWorker.KEY_IS_RUNNING, false)
            .putInt(TimerWorker.KEY_REMAINING_SECONDS, selectedTimer)
            .apply()

        // Cancel WorkManager
        WorkManager.getInstance(context).cancelUniqueWork(TimerWorker.WORK_NAME)

        // Update the specific widget that triggered this action
        WorkoutWidget().update(context, glanceId)
    }
}

class PreviousCardAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val prefs = context.getSharedPreferences(TimerWorker.WIDGET_PREFS, Context.MODE_PRIVATE)
        val currentIndex = prefs.getInt(TimerWorker.KEY_CURRENT_CARD_INDEX, 0)

        prefs.edit()
            .putInt(TimerWorker.KEY_CURRENT_CARD_INDEX, (currentIndex - 1).coerceAtLeast(0))
            .putBoolean(TimerWorker.KEY_IS_RUNNING, false)
            .putInt(TimerWorker.KEY_REMAINING_SECONDS, 0)
            .putInt(TimerWorker.KEY_SELECTED_TIMER, 0)
            .apply()

        // Update the specific widget that triggered this action
        WorkoutWidget().update(context, glanceId)
    }
}

class NextCardAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val prefs = context.getSharedPreferences(TimerWorker.WIDGET_PREFS, Context.MODE_PRIVATE)
        val currentIndex = prefs.getInt(TimerWorker.KEY_CURRENT_CARD_INDEX, 0)

        prefs.edit()
            .putInt(TimerWorker.KEY_CURRENT_CARD_INDEX, currentIndex + 1)
            .putBoolean(TimerWorker.KEY_IS_RUNNING, false)
            .putInt(TimerWorker.KEY_REMAINING_SECONDS, 0)
            .putInt(TimerWorker.KEY_SELECTED_TIMER, 0)
            .apply()

        // Update the specific widget that triggered this action
        WorkoutWidget().update(context, glanceId)
    }
}
