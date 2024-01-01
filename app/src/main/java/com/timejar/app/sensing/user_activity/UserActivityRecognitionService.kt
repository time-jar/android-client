package com.timejar.app.sensing.user_activity

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity

object ActivityTracker {
    val activityCounts = mutableMapOf<Int, Int>()

    fun incrementActivityCount(type: Int) {
        val currentCount = activityCounts.getOrDefault(type, 0)
        activityCounts[type] = currentCount + 1
    }

    fun getMostFrequentActivity(): DetectedActivity? {
        Log.d("getMostFrequentActivity activityCounts", activityCounts.toString())

        val maxEntry = activityCounts.maxByOrNull { it.value }

        Log.d("getMostFrequentActivity maxEntry", maxEntry.toString())

        activityCounts.clear()  // Reset activity counts for the next period

        return maxEntry?.let { DetectedActivity(it.key, it.value) }
    }
}


class UserActivityRecognitionService(private val context: Context) {

    private val activityRecognitionClient: ActivityRecognitionClient = ActivityRecognition.getClient(context)
    private val detectionIntervalMillis: Long = 10 * 1000 // 10 seconds

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, ActivityRecognitionReceiver::class.java)
        intent.action = "com.timejar.app.sensing.user_activity.ACTIVITY_RECOGNITION_ACTION"
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun startTracking() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            activityRecognitionClient.requestActivityUpdates(detectionIntervalMillis, pendingIntent)
                .addOnSuccessListener {
                    Log.i("UserActivityRecognitionService startTracking", "Registered activityRecognitionClient")
                }
                .addOnFailureListener {
                    Log.e("UserActivityRecognitionService startTracking", "Failed to register activityRecognitionClient")
                }
        } else {
            Log.e("UserActivityRecognitionService startTracking", "Permissions not granted for activityRecognitionClient")
        }
    }

    fun stopTrackingAndReturnMostFrequentActivity(): DetectedActivity? {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            activityRecognitionClient.removeActivityUpdates(pendingIntent)
                .addOnSuccessListener {
                    Log.i("UserActivityRecognitionService stopTrackingAndReturnMostFrequentActivity", "Successfully unregistered activityRecognitionClient")
                }
                .addOnFailureListener {
                    Log.e("UserActivityRecognitionService stopTrackingAndReturnMostFrequentActivity", "Failed to unregister activityRecognitionClient")
                }
            val mostFrequentActivity = ActivityTracker.getMostFrequentActivity()

            return mostFrequentActivity
        } else {
            Log.e("UserActivityRecognitionService stopTrackingAndReturnMostFrequentActivity", "Permissions not granted for activityRecognitionClient")

            return null
        }
    }
}

class ActivityRecognitionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val result = ActivityRecognitionResult.extractResult(intent)

        Log.i("UserActivityRecognitionService ActivityRecognitionReceiver", result.toString())

        result?.let {
            val detectedActivity = it.mostProbableActivity
            ActivityTracker.incrementActivityCount(detectedActivity.type)
        }
    }
}