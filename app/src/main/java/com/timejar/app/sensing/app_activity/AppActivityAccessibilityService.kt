package com.timejar.app.sensing.app_activity

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.timejar.app.api.supabase.Supabase
import com.timejar.app.screens.BlockedActivityScreen
import com.timejar.app.sensing.notification.handleUserDecisionNotification
import com.timejar.app.sensing.user_activity.UserActivityRecognitionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val minSecondsForApp = 30
val notSwitchingActivityApps = listOf<String>(
    "com.android.systemui"
)


val blacklistedApps = listOf<String>(
    "com.google.android.apps.nexuslauncher",
    "com.android.settings",
    "com.android.systemui",
    "com.google.android.settings.intelligence",
    "com.timejar.app"
)

class AppActivityAccessibilityService : AccessibilityService() {

    private var lastPackageName: String? = null
    private var lastAppOpenTime: Long = 0
    private var activityRecognitionManager: UserActivityRecognitionService? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        activityRecognitionManager = UserActivityRecognitionService(this)

        Log.i("AppActivityAccessibilityService onServiceConnected", "SUCCESS")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        if (!Supabase.isLoggedIn()) {
            Log.i("AppActivityAccessibilityService onAccessibilityEvent", "not logged in")
            return
        }

        val currentPackageName = event.packageName.toString()
        if (currentPackageName == lastPackageName) {
            return
        }

        if (notSwitchingActivityApps.contains(currentPackageName)) {
            // User is checking notifications and not switching apps
            Log.i("AppActivityAccessibilityService onAccessibilityEvent", "notSwitchingActivityApp $currentPackageName")
            return
        }

        val currentTime = System.currentTimeMillis()
        if (lastPackageName != null) {
            if (!blacklistedApps.contains(lastPackageName)) {
                Log.i("AppActivityAccessibilityService onAccessibilityEvent", "Prevented switch on $currentPackageName with prev $lastPackageName as it was a system app")
                // An app was switched or closed
                handleAppClosedOrSwitched(lastPackageName!!, currentTime)
            }
        }

        if (blacklistedApps.contains(currentPackageName)) {
            lastPackageName = currentPackageName
            Log.i("AppActivityAccessibilityService onAccessibilityEvent", "Prevented tracking $currentPackageName")
            return
        }

        // A new app was opened
        handleAppOpened(currentPackageName, currentTime)
        lastPackageName = currentPackageName

        Log.i("AppActivityAccessibilityService onAccessibilityEvent", "Tracking $currentPackageName")
    }

    private fun handleAppOpened(packageName: String, eventTime: Long) {
        lastAppOpenTime = eventTime  // Record the time the app was opened

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

        if (false) {
            // Block app
            val intent = Intent(this@AppActivityAccessibilityService, BlockedActivityScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

            return
        }
    }

    private fun handleAppClosedOrSwitched(packageName: String, eventTime: Long) {
        var timeUsed = eventTime - lastAppOpenTime  // Calculate the duration the app was used
        timeUsed /= 1000 // miliseconds to seconds
        if (timeUsed < minSecondsForApp) {
            Log.i("AppActivityAccessibilityService handleAppClosedOrSwitched", "App $packageName was used for less than $minSecondsForApp seconds (${timeUsed}s).")
            return  // Exit the function early if the app was used for less than 30 seconds
        }

        CoroutineScope(Dispatchers.Main).launch {
            Log.i("AppActivityAccessibilityService handleAppClosedOrSwitched", "App closed or switched: $packageName")
            val mostFrequentActivity = activityRecognitionManager?.stopTrackingAndReturnMostFrequentActivity()
            Log.i("AppActivityAccessibilityService handleAppClosedOrSwitched", "Most Frequent Activity during this period: $mostFrequentActivity")

            val (shouldBeBlocked, acceptance) = handleUserDecisionNotification(this@AppActivityAccessibilityService)

            Log.i("AppActivityAccessibilityService handleAppClosedOrSwitched", "shouldBeBlocked: $shouldBeBlocked, acceptance: $acceptance")

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
        Log.e("AppActivityAccessibilityService onInterrupt", "ERROR")
    }
}
