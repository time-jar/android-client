package com.timejar.app.sensing.user_activity

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient

const val CUSTOM_INTENT_USER_ACTION = "USER-ACTIVITY-DETECTION-INTENT-ACTION"
const val CUSTOM_REQUEST_CODE_USER_ACTION = 1000

class UserActivityRecognitionService(private val context: Context) {
    private val activityRecognitionClient: ActivityRecognitionClient = ActivityRecognition.getClient(context)

    private val detectionIntervalMillis: Long = 30 * 1000 // Adjust as needed

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, ActivityRecognitionReceiver::class.java)
        intent.action = CUSTOM_INTENT_USER_ACTION
        PendingIntent.getBroadcast(context, CUSTOM_REQUEST_CODE_USER_ACTION, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun startTracking() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            activityRecognitionClient.requestActivityUpdates(detectionIntervalMillis, pendingIntent)
                .addOnSuccessListener {
                    Log.i("UserActivityRecognitionService", "Registered activityRecognitionClient")
                }
                .addOnFailureListener {
                    Log.e("UserActivityRecognitionService", "Failed to register activityRecognitionClient")
                }
        } else {
            Log.e("UserActivityRecognitionService", "Permissions not granted for activityRecognitionClient")
        }
    }

    fun stopTrackingAndReturnMostFrequentActivity(): Int {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            activityRecognitionClient.removeActivityUpdates(pendingIntent)
                .addOnSuccessListener {
                    Log.i("UserActivityRecognitionService", "Successfully unregistered activityRecognitionClient")
                }
                .addOnFailureListener {
                    Log.e("UserActivityRecognitionService", "Failed to unregister activityRecognitionClient")
                }

        } else {
            Log.e("UserActivityRecognitionService", "Permissions not granted for activityRecognitionClient")
        }

        return ActivityTracker.getMostFrequentActivity()
    }
}
