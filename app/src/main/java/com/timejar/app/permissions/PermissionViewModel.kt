package com.timejar.app.permissions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class PermissionViewModel : ViewModel() {
    private val _notificationPermissionEnabled = mutableStateOf(false)
    val notificationPermissionEnabled: State<Boolean> get() = _notificationPermissionEnabled

    private val _locationPermissionEnabled = mutableStateOf(false)
    val locationPermissionEnabled: State<Boolean> get() = _locationPermissionEnabled

    private val _activityRecognitionPermissionEnabled = mutableStateOf(false)
    val activityRecognitionPermissionEnabled: State<Boolean> get() = _activityRecognitionPermissionEnabled

    fun onNotificationPermissionResult(isGranted: Boolean) {
        _notificationPermissionEnabled.value = isGranted
    }

    fun onLocationPermissionResult(isGranted: Boolean) {
        _locationPermissionEnabled.value = isGranted
    }

    fun onActivityRecognitionPermissionResult(isGranted: Boolean) {
        _activityRecognitionPermissionEnabled.value = isGranted
    }
}
