package com.myworkoutroutine.core.domain.util

/**
 * Interface for updating widgets when app data changes.
 * This allows the domain/feature layers to trigger widget updates
 * without directly depending on the widget module.
 */
interface WidgetUpdater {
    suspend fun updateWidgets()
}
