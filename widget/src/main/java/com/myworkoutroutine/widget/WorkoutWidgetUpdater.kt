package com.myworkoutroutine.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.util.Log
import com.myworkoutroutine.core.domain.util.WidgetUpdater
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of WidgetUpdater that updates all RemoteViews-based widget instances.
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
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, WorkoutAppWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            Log.d("WorkoutWidgetUpdater", "Found ${appWidgetIds.size} widget instances")

            if (appWidgetIds.isEmpty()) {
                Log.d("WorkoutWidgetUpdater", "No widget instances found, skipping update")
                return
            }

            WorkoutAppWidgetProvider.updateWidget(context)
            Log.d("WorkoutWidgetUpdater", "Widget update completed successfully for ${appWidgetIds.size} instances")
        } catch (e: Exception) {
            Log.e("WorkoutWidgetUpdater", "Error updating widget", e)
        }
    }
}
