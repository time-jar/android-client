package com.timejar.app.permissions

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel

class PermissionViewModel : ViewModel() {
    private val _notificationPermissionEnabled = mutableStateOf(false)
    private val _locationPermissionEnabled = mutableStateOf(false)
    private val _activityRecognitionPermissionEnabled = mutableStateOf(false)
    private val _accessibilityPermissionEnabled = mutableStateOf(false)

    fun onNotificationPermissionResult(isGranted: Boolean) {
        _notificationPermissionEnabled.value = isGranted
    }

    fun onLocationPermissionResult(isGranted: Boolean) {
        _locationPermissionEnabled.value = isGranted
    }

    fun onActivityRecognitionPermissionResult(isGranted: Boolean) {
        _activityRecognitionPermissionEnabled.value = isGranted
    }

    fun onAccessibilityPermissionResult(isServiceEnabled: Boolean) {
        _accessibilityPermissionEnabled.value = isServiceEnabled
    }

    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isAccessibilityPermissionGranted(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun isAccessibilityServiceEnabled(context: Context, serviceComponentName: ComponentName): Boolean {
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains(serviceComponentName.flattenToString()) == true
    }
}
