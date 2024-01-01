package com.timejar.app.sensing.user_activity

import android.util.Log
import com.google.android.gms.location.DetectedActivity

object ActivityTracker {
    val activityCounts = mutableMapOf<Int, Int>()

    fun incrementActivityCount(type: Int) {
        val currentCount = activityCounts.getOrDefault(type, 0)
        activityCounts[type] = currentCount + 1
    }

    fun getMostFrequentActivity(): Int {
        // Log.d("getMostFrequentActivity", "activityCounts ${activityCounts.toString()}")

        val maxEntry = activityCounts.maxByOrNull { it.value }

        Log.d("getMostFrequentActivity", "maxEntry ${maxEntry.toString()}")

        // Reset logged activity for the next period
        // Remove all entries except the last one
        if (activityCounts.isNotEmpty()) {
            val lastKey = activityCounts.keys.last()
            activityCounts.keys.retainAll { it == lastKey }
        }

        return maxEntry?.value ?: DetectedActivity.UNKNOWN
    }
}
