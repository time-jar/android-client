package com.timejar.app.sensing.app_activity

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.timejar.app.api.supabase.Supabase
import com.timejar.app.screens.BlockedActivityScreen
import com.timejar.app.sensing.geofence.MapsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
val blacklistedApps = listOf<String>(
    "com.google.android.",
    "com.android.settings",
    "com.android.systemui",
    "com.timejar.app",
)
*/

class AppActivityAccessibilityService : AccessibilityService() {
    private val appSessions = mutableMapOf<String, AppSessionHandler>()

    private var lastPackageName: String? = null

    private lateinit var blacklistedApps: List<String>
    private lateinit var nonSwitchingApps: List<String>

    private var mapsActivity: MapsActivity? = null

    private var lastToastTime: Long = 0

    private fun showToastIfAllowed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastToastTime > 30_000) { // 30 seconds
            lastToastTime = currentTime
            Toast.makeText(this, "Please log in to use Time-Jar", Toast.LENGTH_LONG).show()
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        createNotificationChannel(this)

        nonSwitchingApps = getNonSwitchingApps(this)
        blacklistedApps = getBlacklistedApps(this)

        mapsActivity = MapsActivity()

        Log.i("AppActivityAccessibilityService onServiceConnected", "SUCCESS")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            return
        }

        val currentPackageName = event.packageName.toString()
        if (currentPackageName == lastPackageName) {
            return
        }

        if (containsStringOrPrefix(nonSwitchingApps, currentPackageName)) {
            // User is checking notifications or writing using keyboard, not switching apps
            Log.i("AppActivityAccessibilityService onAccessibilityEvent", "notSwitchingActivityApp $currentPackageName")
            return
        }

        if (lastPackageName != null) {
            if (!containsStringOrPrefix(blacklistedApps, lastPackageName!!)) {
                Log.i("AppActivityAccessibilityService onAccessibilityEvent", "Prevented switch on $currentPackageName with prev $lastPackageName as it was a system app")
                // An app was switched or closed
                handleAppClosed(lastPackageName)
            }
        }

        if (containsStringOrPrefix(blacklistedApps, currentPackageName)) {
            lastPackageName = currentPackageName
            Log.i("AppActivityAccessibilityService onAccessibilityEvent", "Prevented tracking $currentPackageName")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            if (!Supabase.isLoggedInWithRefresh()) {
                Log.i("AppActivityAccessibilityService onAccessibilityEvent", "not logged in")
                showToastIfAllowed()
                return@launch
            }

            // A new app was opened
            handleAppOpened(currentPackageName)
        }
    }

    private suspend fun handleAppOpened(packageName: String) {
        val handler = AppSessionHandler(this)
        appSessions[packageName] = handler
        val supabaseData = handler.startSession(packageName)

        var shouldBlock = false

        // send to Supabase
        Supabase.predict(packageName, supabaseData.locationId, supabaseData.startTime, onSuccess = {
            shouldBlock = it
            Log.i("AppActivityAccessibilityService", "handleAppOpened predict SUCCESS")
        }, onFailure = {
            it.printStackTrace()
            Log.e("AppActivityAccessibilityService", "handleAppOpened error: ${it.message}")
            CoroutineScope(Dispatchers.Main).launch {
                showNotification(this@AppActivityAccessibilityService, "Time-Jar error", "Error when requesting prediction: ${it.message}")
            }
        })

        Log.i("AppActivityAccessibilityService", "handleAppOpened shouldBlock: $shouldBlock")

        if (shouldBlock) {
            handleAppClosed(packageName)

            // Block app
            val intent = Intent(this@AppActivityAccessibilityService, BlockedActivityScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

            return
        }

        lastPackageName = packageName
    }

    private fun handleAppClosed(packageName: String?) {
        appSessions[packageName]?.endSession()
        appSessions.remove(packageName)
    }

    override fun onInterrupt() {
        Log.e("AppActivityAccessibilityService onInterrupt", "ERROR")
    }
}

fun containsStringOrPrefix(list: List<String>, query: String): Boolean {
    return list.any { query.startsWith(it) }
}
