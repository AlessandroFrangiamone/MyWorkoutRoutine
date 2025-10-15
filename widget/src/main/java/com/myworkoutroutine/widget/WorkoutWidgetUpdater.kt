package com.myworkoutroutine.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
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
        try {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, WorkoutAppWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)

            if (appWidgetIds.isEmpty()) {
                return
            }

            WorkoutAppWidgetProvider.updateWidget(context)
        } catch (e: Exception) {
            // Silent fail
        }
    }
}
