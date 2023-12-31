package com.timejar.app.sensing.app_activity

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.timejar.app.MainActivity
import com.timejar.app.api.supabase.Supabase
import com.timejar.app.screens.BlockedActivityScreen
import com.timejar.app.sensing.geofence.GeofenceJobIntentService
import com.timejar.app.sensing.geofence.MapsActivity
import com.timejar.app.sensing.notification.handleUserDecisionNotification
import com.timejar.app.sensing.user_activity.UserActivityRecognitionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.descriptors.StructureKind

val minSecondsForApp = 30
val notSwitchingActivityApps = listOf<String>(
    "com.android.systemui"
)

/*
val blacklistedApps = listOf<String>(
    "com.google.android.",
    "com.android.settings",
    "com.android.systemui",
    "com.timejar.app",
)
*/

class AppActivityAccessibilityService : AccessibilityService() {
    private var decisionJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var lastPackageName: String? = null
    private var lastAppOpenTime: Long = 0
    private var activityRecognitionManager: UserActivityRecognitionService? = null

    private lateinit var blacklistedApps: List<String>

    private var mapsActivity: MapsActivity? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        activityRecognitionManager = UserActivityRecognitionService(this)

        blacklistedApps = getBlacklistedApps(this)

        mapsActivity = MapsActivity()

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

        if (containsStringOrPrefix(notSwitchingActivityApps, currentPackageName)) {
            // User is checking notifications and not switching apps
            Log.i("AppActivityAccessibilityService onAccessibilityEvent", "notSwitchingActivityApp $currentPackageName")
            return
        }

        val currentTime = System.currentTimeMillis()
        if (lastPackageName != null) {
            if (!containsStringOrPrefix(blacklistedApps, lastPackageName!!)) {
                Log.i("AppActivityAccessibilityService onAccessibilityEvent", "Prevented switch on $currentPackageName with prev $lastPackageName as it was a system app")
                // An app was switched or closed
                handleAppClosedOrSwitched(lastPackageName!!, currentTime)
            }
        }

        if (containsStringOrPrefix(blacklistedApps, currentPackageName)) {
            lastPackageName = currentPackageName
            Log.i("AppActivityAccessibilityService onAccessibilityEvent", "Prevented tracking $currentPackageName")
            return
        }

        // A new app was opened
        coroutineScope.launch {
            handleAppOpened(currentPackageName, currentTime)
        }

        lastPackageName = currentPackageName

        Log.i("AppActivityAccessibilityService onAccessibilityEvent", "Tracking $currentPackageName")
    }

    private suspend fun handleAppOpened(packageName: String, eventTime: Long) {
        lastAppOpenTime = eventTime  // Record the time the app was opened

        Log.i("AppActivityAccessibilityService handleAppOpened", "App opened: $packageName")
        activityRecognitionManager?.startTracking()

        val location = GeofenceJobIntentService.getCurrentPlace()

        Log.i("AppActivityAccessibilityService handleAppOpened", "Location: $location")

        var shouldBlock = false

        // send to Supabase
        Supabase.initialAppActivity(packageName, eventTime, location, onSuccess = {
            shouldBlock = it
            Log.i("AppActivityAccessibilityService handleAppOpened", "initial-app-activity SUCCESS")
        }, onFailure = {
            it.printStackTrace()
            Log.e("AppActivityAccessibilityService handleAppOpened", "${it.message}")
        })

        Log.i("AppActivityAccessibilityService handleAppOpened", "initial-app-activity shouldBlock: $shouldBlock")

        if (shouldBlock) {
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
            activityRecognitionManager?.stopTrackingAndReturnMostFrequentActivity()
            return  // Exit the function early if the app was used for less than 30 seconds
        }

        // Cancel any existing decision job before starting a new one
        decisionJob?.cancel()

        decisionJob = CoroutineScope(Dispatchers.Main).launch {
            Log.i("AppActivityAccessibilityService handleAppClosedOrSwitched", "App closed or switched: $packageName")
            val mostFrequentActivity = activityRecognitionManager!!.stopTrackingAndReturnMostFrequentActivity()
            Log.i("AppActivityAccessibilityService handleAppClosedOrSwitched", "Most Frequent Activity during this period: $mostFrequentActivity")

            val (shouldBeBlocked, acceptance) = handleUserDecisionNotification(this@AppActivityAccessibilityService)

            Log.i("AppActivityAccessibilityService handleAppClosedOrSwitched", "shouldBeBlocked: $shouldBeBlocked, acceptance: $acceptance")

            // send to Supabase
            Supabase.endAppActivity(acceptance, shouldBeBlocked, mostFrequentActivity, eventTime, onSuccess = {
                Log.i("AppActivityAccessibilityService handleAppClosedOrSwitched", "end-app-activity SUCCESS")
            }, onFailure = {
                it.printStackTrace()
                Log.e("AppActivityAccessibilityService handleAppClosedOrSwitched", "${it.message}")
            })
        }
    }

    override fun onInterrupt() {
        Log.e("AppActivityAccessibilityService onInterrupt", "ERROR")
    }
}

fun containsStringOrPrefix(list: List<String>, query: String): Boolean {
    return list.any { query.startsWith(it) }
}
