package com.myworkoutroutine.widget

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import com.myworkoutroutine.core.domain.util.WidgetUpdater
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of WidgetUpdater that updates all WorkoutWidget instances.
 * This is injected into ViewModels to allow them to trigger widget updates
 * when app data changes.
 */
@Singleton
class WorkoutWidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context
) : WidgetUpdater {

    override suspend fun updateWidgets() {
        Log.d("WorkoutWidgetUpdater", "updateWidgets() called - triggering widget refresh")
        try {
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(WorkoutWidget::class.java)

            Log.d("WorkoutWidgetUpdater", "Found ${glanceIds.size} widget instances")

            if (glanceIds.isEmpty()) {
                Log.d("WorkoutWidgetUpdater", "No widget instances found, skipping update")
                return
            }

            WorkoutWidget().updateAll(context)
            Log.d("WorkoutWidgetUpdater", "Widget update completed successfully for ${glanceIds.size} instances")
        } catch (e: Exception) {
            Log.e("WorkoutWidgetUpdater", "Error updating widget", e)
        }
    }
}
