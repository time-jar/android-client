package com.timejar.app.sensing.user_activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityRecognitionResult

class ActivityRecognitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (CUSTOM_INTENT_USER_ACTION == intent.action) {
            val result = ActivityRecognitionResult.extractResult(intent)

            if (result != null) {
                val detectedActivity = result.mostProbableActivity

                Log.d("ActivityRecognition", "Activity: ${detectedActivity.toString()}, ID: ${detectedActivity.type}")

                ActivityTracker.incrementActivityCount(detectedActivity.type)
            } else {
                Log.d("ActivityRecognition", "No activity detected.")
            }
        }
    }
}
