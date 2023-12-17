package com.timejar.app.sensing.app_activity

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.timejar.app.api.supabase.Supabase
import com.timejar.app.sensing.user_activity.UserActivityRecognitionService

class AppActivityAccessibilityService : AccessibilityService() {

    private var lastPackageName: String? = null
    private var activityRecognitionManager: UserActivityRecognitionService? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        activityRecognitionManager = UserActivityRecognitionService(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val currentPackageName = event.packageName.toString()

            if (currentPackageName != lastPackageName) {
                val currentTime = System.currentTimeMillis()

                if (lastPackageName != null) {
                    // An app was switched or closed
                    handleAppClosedOrSwitched(lastPackageName!!, currentTime)
                }

                // A new app was opened
                handleAppOpened(currentPackageName, currentTime)
                lastPackageName = currentPackageName
            }
        }
    }

    private fun handleAppOpened(packageName: String, eventTime: Long) {
        Log.i("AppActivity", "App opened: $packageName")
        activityRecognitionManager?.startTracking()

        // send to Supabase
        Supabase.initialAppActivity(packageName, eventTime, "", onSuccess = {
            //
        }, onFailure = {
            it.printStackTrace()
            // loginAlert.value = "There was an error while logging in. Check your credentials and try again."
        })
    }

    private fun handleAppClosedOrSwitched(packageName: String, eventTime: Long) {
        Log.i("AppActivity", "App closed or switched: $packageName")
        val mostFrequentActivity = activityRecognitionManager?.stopTrackingAndReturnMostFrequentActivity()
        Log.i("AppActivity", "Most Frequent Activity during this period: ${mostFrequentActivity.toString()}")

        // TODO: These 2 should be acquired from notification
        val acceptance = 1
        val shouldBeBlocked = false

        Supabase.endAppActivity(acceptance, shouldBeBlocked, mostFrequentActivity!!.type, eventTime, onSuccess = {
            //
        }, onFailure = {
            it.printStackTrace()
            // loginAlert.value = "There was an error."
        })
    }

    override fun onInterrupt() {
        // Handle interruptions
    }
}
