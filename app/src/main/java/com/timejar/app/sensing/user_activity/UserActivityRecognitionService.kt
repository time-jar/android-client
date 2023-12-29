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

class UserActivityRecognitionService(private val context: Context) {

    private val activityRecognitionClient: ActivityRecognitionClient =
            ActivityRecognition.getClient(context)
    private val detectionIntervalMillis: Long = 10000 // 10 seconds

    private val activityCounts = mutableMapOf<Int, Int>()

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, ActivityRecognitionReceiver::class.java)
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun startTracking() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            activityRecognitionClient.requestActivityUpdates(detectionIntervalMillis, pendingIntent)
                .addOnSuccessListener {
                    Log.i("UserActivityRecognitionService startTracking", "Registered")
                }
                .addOnFailureListener {
                    Log.e("UserActivityRecognitionService startTracking", "Failed to register")
                }
        } else {
            Log.e("UserActivityRecognitionService startTracking", "Permissions not granted")
        }
    }

    fun stopTrackingAndReturnMostFrequentActivity(): DetectedActivity? {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
            activityRecognitionClient.removeActivityUpdates(pendingIntent)
                .addOnSuccessListener {
                    Log.i("UserActivityRecognitionService stopTrackingAndReturnMostFrequentActivity", "Successfully unregistered")
                }
                .addOnFailureListener {
                    Log.e("UserActivityRecognitionService stopTrackingAndReturnMostFrequentActivity", "Failed to unregister")
                }
            val mostFrequentActivity = getMostFrequentActivity()
            activityCounts.clear()  // Reset activity counts for the next period
            return mostFrequentActivity
        } else {
            Log.e("UserActivityRecognitionService stopTrackingAndReturnMostFrequentActivity", "Permissions not granted")
            return null
        }
    }
    private fun getMostFrequentActivity(): DetectedActivity? {
        val maxEntry = activityCounts.maxByOrNull { it.value }
        return maxEntry?.let { DetectedActivity(it.key, it.value) }
    }

    inner class ActivityRecognitionReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val result = ActivityRecognitionResult.extractResult(intent)

            result?.let {
                val detectedActivity = it.mostProbableActivity

                val currentCount = activityCounts.getOrDefault(detectedActivity.type, 0)
                activityCounts[detectedActivity.type] = currentCount + 1
            }
        }
    }

}
