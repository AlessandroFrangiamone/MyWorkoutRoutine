package com.myworkoutroutine.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.View
import android.widget.RemoteViews
import com.myworkoutroutine.core.domain.model.TrainingPlan
import com.myworkoutroutine.core.domain.model.WorkoutCard
import com.myworkoutroutine.core.domain.repository.WorkoutRepository
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * RemoteViews-based widget provider for professional, real-time timer updates.
 * Replaces the Glance-based WorkoutWidget to enable high-frequency updates.
 */
class WorkoutAppWidgetProvider : AppWidgetProvider() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_SELECT_TIMER -> {
                val seconds = intent.getIntExtra(EXTRA_TIMER_SECONDS, 0)
                handleSelectTimer(context, seconds)
            }
            ACTION_START_TIMER -> handleStartTimer(context)
            ACTION_PAUSE_TIMER -> handlePauseTimer(context)
            ACTION_RESET_TIMER -> handleResetTimer(context)
            ACTION_PREVIOUS_CARD -> handlePreviousCard(context)
            ACTION_NEXT_CARD -> handleNextCard(context)
            ACTION_UPDATE_WIDGET -> updateAllWidgets(context)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
        scope.cancel()
    }

    private fun handleSelectTimer(context: Context, seconds: Int) {
        val prefs = context.getSharedPreferences(WIDGET_PREFS, Context.MODE_PRIVATE)

        prefs.edit()
            .putInt(KEY_SELECTED_TIMER, seconds)
            .putInt(KEY_REMAINING_SECONDS, seconds)
            .apply()

        updateAllWidgets(context)
    }

    private fun handleStartTimer(context: Context) {
        val prefs = context.getSharedPreferences(WIDGET_PREFS, Context.MODE_PRIVATE)
        val selectedTimer = prefs.getInt(KEY_SELECTED_TIMER, 0)

        if (selectedTimer <= 0) {
            return
        }

        val remainingSeconds = prefs.getInt(KEY_REMAINING_SECONDS, selectedTimer)

        prefs.edit()
            .putBoolean(KEY_IS_RUNNING, true)
            .putInt(KEY_REMAINING_SECONDS, remainingSeconds)
            .putLong(KEY_TIMER_START_TIME, System.currentTimeMillis())
            .apply()


        // Start WorkManager for background countdown
        val workRequest = androidx.work.OneTimeWorkRequestBuilder<TimerWorker>().build()
        androidx.work.WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            androidx.work.ExistingWorkPolicy.REPLACE,
            workRequest
        )

        updateAllWidgets(context)
    }

    private fun handlePauseTimer(context: Context) {
        val prefs = context.getSharedPreferences(WIDGET_PREFS, Context.MODE_PRIVATE)

        prefs.edit()
            .putBoolean(KEY_IS_RUNNING, false)
            .apply()

        // Cancel WorkManager
        androidx.work.WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)

        updateAllWidgets(context)
    }

    private fun handleResetTimer(context: Context) {
        val prefs = context.getSharedPreferences(WIDGET_PREFS, Context.MODE_PRIVATE)
        val selectedTimer = prefs.getInt(KEY_SELECTED_TIMER, 0)

        prefs.edit()
            .putBoolean(KEY_IS_RUNNING, false)
            .putInt(KEY_REMAINING_SECONDS, selectedTimer)
            .apply()

        // Cancel WorkManager
        androidx.work.WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)

        updateAllWidgets(context)
    }

    private fun handlePreviousCard(context: Context) {
        val prefs = context.getSharedPreferences(WIDGET_PREFS, Context.MODE_PRIVATE)
        val currentIndex = prefs.getInt(KEY_CURRENT_CARD_INDEX, 0)

        prefs.edit()
            .putInt(KEY_CURRENT_CARD_INDEX, (currentIndex - 1).coerceAtLeast(0))
            .putBoolean(KEY_IS_RUNNING, false)
            .putInt(KEY_REMAINING_SECONDS, 0)
            .putInt(KEY_SELECTED_TIMER, 0)
            .apply()

        updateAllWidgets(context)
    }

    private fun handleNextCard(context: Context) {
        val prefs = context.getSharedPreferences(WIDGET_PREFS, Context.MODE_PRIVATE)
        val currentIndex = prefs.getInt(KEY_CURRENT_CARD_INDEX, 0)

        prefs.edit()
            .putInt(KEY_CURRENT_CARD_INDEX, currentIndex + 1)
            .putBoolean(KEY_IS_RUNNING, false)
            .putInt(KEY_REMAINING_SECONDS, 0)
            .putInt(KEY_SELECTED_TIMER, 0)
            .apply()

        updateAllWidgets(context)
    }

    private fun updateAllWidgets(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, WorkoutAppWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        scope.launch {
            try {
                val repository = getRepository(context)
                val prefs = context.getSharedPreferences(WIDGET_PREFS, Context.MODE_PRIVATE)

                val currentTraining = repository.getCurrentTrainingPlan().first()
                val cards = if (currentTraining != null) {
                    currentTraining.workoutCardIds.mapNotNull { cardId ->
                        repository.getWorkoutCardById(cardId).first()
                    }
                } else {
                    emptyList()
                }

                val currentCardIndex = prefs.getInt(KEY_CURRENT_CARD_INDEX, 0)
                    .coerceIn(0, (cards.size - 1).coerceAtLeast(0))
                val isRunning = prefs.getBoolean(KEY_IS_RUNNING, false)
                val remainingSeconds = prefs.getInt(KEY_REMAINING_SECONDS, 0)

                val views = buildRemoteViews(
                    context,
                    currentTraining,
                    cards,
                    currentCardIndex,
                    isRunning,
                    remainingSeconds
                )

                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
            }
        }
    }

    private fun buildRemoteViews(
        context: Context,
        training: TrainingPlan?,
        cards: List<WorkoutCard>,
        currentCardIndex: Int,
        isRunning: Boolean,
        remainingSeconds: Int
    ): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_workout)

        if (training == null || cards.isEmpty()) {
            // Show empty state
            views.setViewVisibility(R.id.empty_state_container, View.VISIBLE)
            views.setViewVisibility(R.id.workout_content_container, View.GONE)
            views.setViewVisibility(R.id.navigation_container, View.GONE)
        } else {
            // Show workout content
            views.setViewVisibility(R.id.empty_state_container, View.GONE)
            views.setViewVisibility(R.id.workout_content_container, View.VISIBLE)

            val currentCard = cards[currentCardIndex]

            // Set training name
            views.setTextViewText(R.id.training_name, training.name)

            // Navigation arrows
            if (cards.size > 1 && !isRunning) {
                views.setViewVisibility(R.id.navigation_container, View.VISIBLE)

                if (currentCardIndex > 0) {
                    views.setOnClickPendingIntent(
                        R.id.btn_previous,
                        createActionPendingIntent(context, ACTION_PREVIOUS_CARD)
                    )
                }

                if (currentCardIndex < cards.size - 1) {
                    views.setOnClickPendingIntent(
                        R.id.btn_next,
                        createActionPendingIntent(context, ACTION_NEXT_CARD)
                    )
                }
            } else {
                views.setViewVisibility(R.id.navigation_container, View.GONE)
            }

            // Card info
            views.setTextViewText(R.id.card_name, currentCard.name)
            views.setTextViewText(R.id.card_description, currentCard.description)

            // Timer display with Chronometer
            setupChronometer(views, remainingSeconds, isRunning)

            // Timer status
            if (isRunning) {
                views.setViewVisibility(R.id.timer_status, View.VISIBLE)
                views.setTextColor(R.id.timer_chronometer, context.getColor(R.color.widget_fitness_red))
            } else {
                views.setViewVisibility(R.id.timer_status, View.GONE)
                views.setTextColor(R.id.timer_chronometer, context.getColor(R.color.widget_off_white))
            }

            // Timer chips and buttons
            setupTimerButtons(context, views, currentCard, isRunning)
        }

        return views
    }

    private fun setupChronometer(views: RemoteViews, remainingSeconds: Int, isRunning: Boolean) {
        if (isRunning && remainingSeconds > 0) {
            // Set chronometer to countdown mode
            val elapsedTime = SystemClock.elapsedRealtime()
            val futureTime = elapsedTime + (remainingSeconds * 1000L)

            views.setChronometerCountDown(R.id.timer_chronometer, true)
            views.setChronometer(
                R.id.timer_chronometer,
                futureTime,
                "%s",
                true // Start counting
            )
        } else {
            // Show static time when not running
            views.setChronometer(
                R.id.timer_chronometer,
                SystemClock.elapsedRealtime(),
                null,
                false // Don't count
            )
            views.setTextViewText(R.id.timer_chronometer, formatTime(remainingSeconds))
        }
    }

    private fun setupTimerButtons(
        context: Context,
        views: RemoteViews,
        card: WorkoutCard,
        isRunning: Boolean
    ) {
        if (card.timers.isEmpty()) {
            views.setViewVisibility(R.id.timer_buttons_container, View.GONE)
            return
        }

        views.setViewVisibility(R.id.timer_buttons_container, View.VISIBLE)

        if (!isRunning) {
            // Show timer selection chips
            views.setViewVisibility(R.id.timer_chips_container, View.VISIBLE)

            // Hide all chips first
            views.setViewVisibility(R.id.chip_timer_1, View.GONE)
            views.setViewVisibility(R.id.chip_timer_2, View.GONE)
            views.setViewVisibility(R.id.chip_timer_3, View.GONE)
            views.setViewVisibility(R.id.chip_timer_4, View.GONE)

            // Show and configure available timers
            card.timers.forEachIndexed { index, seconds ->
                val chipId = when (index) {
                    0 -> R.id.chip_timer_1
                    1 -> R.id.chip_timer_2
                    2 -> R.id.chip_timer_3
                    3 -> R.id.chip_timer_4
                    else -> return@forEachIndexed
                }

                views.setViewVisibility(chipId, View.VISIBLE)
                views.setTextViewText(chipId, "${seconds}s")
                views.setOnClickPendingIntent(
                    chipId,
                    createTimerSelectionPendingIntent(context, seconds)
                )
            }

            // Show START button
            views.setViewVisibility(R.id.btn_start, View.VISIBLE)
            views.setViewVisibility(R.id.btn_pause, View.GONE)
            views.setViewVisibility(R.id.btn_reset, View.GONE)

            views.setOnClickPendingIntent(
                R.id.btn_start,
                createActionPendingIntent(context, ACTION_START_TIMER)
            )
        } else {
            // Hide timer selection chips
            views.setViewVisibility(R.id.timer_chips_container, View.GONE)

            // Show PAUSE and RESET buttons
            views.setViewVisibility(R.id.btn_start, View.GONE)
            views.setViewVisibility(R.id.btn_pause, View.VISIBLE)
            views.setViewVisibility(R.id.btn_reset, View.VISIBLE)

            views.setOnClickPendingIntent(
                R.id.btn_pause,
                createActionPendingIntent(context, ACTION_PAUSE_TIMER)
            )
            views.setOnClickPendingIntent(
                R.id.btn_reset,
                createActionPendingIntent(context, ACTION_RESET_TIMER)
            )
        }
    }

    private fun createActionPendingIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, WorkoutAppWidgetProvider::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createTimerSelectionPendingIntent(context: Context, seconds: Int): PendingIntent {
        val intent = Intent(context, WorkoutAppWidgetProvider::class.java).apply {
            action = ACTION_SELECT_TIMER
            putExtra(EXTRA_TIMER_SECONDS, seconds)
        }
        return PendingIntent.getBroadcast(
            context,
            seconds,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
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
        private const val TAG = "WorkoutAppWidget"

        const val WIDGET_PREFS = "workout_widget_prefs"
        const val KEY_IS_RUNNING = "is_running"
        const val KEY_REMAINING_SECONDS = "remaining_seconds"
        const val KEY_SELECTED_TIMER = "selected_timer"
        const val KEY_CURRENT_CARD_INDEX = "current_card_index"
        const val KEY_TIMER_START_TIME = "timer_start_time"
        const val WORK_NAME = "workout_timer_work"

        const val ACTION_SELECT_TIMER = "com.myworkoutroutine.widget.ACTION_SELECT_TIMER"
        const val ACTION_START_TIMER = "com.myworkoutroutine.widget.ACTION_START_TIMER"
        const val ACTION_PAUSE_TIMER = "com.myworkoutroutine.widget.ACTION_PAUSE_TIMER"
        const val ACTION_RESET_TIMER = "com.myworkoutroutine.widget.ACTION_RESET_TIMER"
        const val ACTION_PREVIOUS_CARD = "com.myworkoutroutine.widget.ACTION_PREVIOUS_CARD"
        const val ACTION_NEXT_CARD = "com.myworkoutroutine.widget.ACTION_NEXT_CARD"
        const val ACTION_UPDATE_WIDGET = "com.myworkoutroutine.widget.ACTION_UPDATE_WIDGET"

        const val EXTRA_TIMER_SECONDS = "extra_timer_seconds"

        /**
         * Trigger widget update from external code (e.g., ViewModels)
         */
        fun updateWidget(context: Context) {
            val intent = Intent(context, WorkoutAppWidgetProvider::class.java).apply {
                action = ACTION_UPDATE_WIDGET
            }
            context.sendBroadcast(intent)
        }
    }
}
