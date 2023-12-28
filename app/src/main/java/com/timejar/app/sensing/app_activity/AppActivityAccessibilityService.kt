package com.timejar.app.sensing.app_activity

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.timejar.app.api.supabase.Supabase
import com.timejar.app.sensing.notification.handleUserDecisionNotification
import com.timejar.app.sensing.user_activity.UserActivityRecognitionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val blacklistedApps = listOf<String>("com.google.android.apps.nexuslauncher", "com.android.settings")

class AppActivityAccessibilityService : AccessibilityService() {

    private var lastPackageName: String? = null
    private var activityRecognitionManager: UserActivityRecognitionService? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        activityRecognitionManager = UserActivityRecognitionService(this)

        Log.e("AppActivityAccessibilityService onServiceConnected", "SUCCESS")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!Supabase.isLoggedIn()) {
            Log.i("AppActivityAccessibilityService onAccessibilityEvent", "not logged in")
            return
        }

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val currentPackageName = event.packageName.toString()

            if (currentPackageName != lastPackageName) {
                val currentTime = System.currentTimeMillis()

                if (lastPackageName != null) {
                    // An app was switched or closed
                    handleAppClosedOrSwitched(lastPackageName!!, currentTime)
                }

                // A new app was opened
                if (blacklistedApps.contains(currentPackageName)) {
                    Log.i("AppActivityAccessibilityService onAccessibilityEvent", "Prevented tracking $currentPackageName")
                    return
                }

                handleAppOpened(currentPackageName, currentTime)
                lastPackageName = currentPackageName

                Log.i("AppActivityAccessibilityService onAccessibilityEvent", "Tracking $currentPackageName")
            }
        }
    }

    private fun handleAppOpened(packageName: String, eventTime: Long) {
        Log.i("AppActivityAccessibilityService handleAppOpened", "App opened: $packageName")
        activityRecognitionManager?.startTracking()

        /*
        // send to Supabase
        Supabase.initialAppActivity(packageName, eventTime, "", onSuccess = {
            Log.i("AppActivityAccessibilityService handleAppOpened", "SUCCESS")
        }, onFailure = {
            it.printStackTrace()
            Log.e("AppActivityAccessibilityService handleAppOpened", "${it.message}")
        })
        */
    }

    private fun handleAppClosedOrSwitched(packageName: String, eventTime: Long) {
        CoroutineScope(Dispatchers.Main).launch {
            Log.i("AppActivityAccessibilityService handleAppClosedOrSwitched", "App closed or switched: $packageName")
            val mostFrequentActivity = activityRecognitionManager?.stopTrackingAndReturnMostFrequentActivity()
            Log.i("AppActivityAccessibilityService handleAppClosedOrSwitched", "Most Frequent Activity during this period: ${mostFrequentActivity.toString()}")

            val (shouldBeBlocked, acceptance) = handleUserDecisionNotification(this@AppActivityAccessibilityService)

            /*
            Supabase.endAppActivity(acceptance, shouldBeBlocked, mostFrequentActivity!!.type, eventTime, onSuccess = {
                Log.i("AppActivityAccessibilityService handleAppClosedOrSwitched", "SUCCESS")
            }, onFailure = {
                it.printStackTrace()
                Log.e("AppActivityAccessibilityService handleAppClosedOrSwitched", "${it.message}")
            })
            */
        }
    }

    override fun onInterrupt() {
        // Handle interruptions
    }
}
