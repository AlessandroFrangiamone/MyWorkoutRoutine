package com.myworkoutroutine.core.ui.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

/**
 * Centralized handler for all app permissions.
 * Provides a clean interface for requesting and checking permissions.
 */
class PermissionHandler(
    private val context: Context
) {

    /**
     * Request notification permission if needed (Android 13+).
     * On older Android versions, notifications are enabled by default.
     */
    fun requestNotificationPermissionIfNeeded(
        launcher: ActivityResultLauncher<String>
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!hasNotificationPermission()) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    /**
     * Check if notification permission is granted.
     * Returns true on Android < 13 (permission not required).
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission not required on older versions
        }
    }

    companion object {
        /**
         * Create a PermissionHandler instance.
         */
        fun create(context: Context): PermissionHandler {
            return PermissionHandler(context.applicationContext)
        }
    }
}
