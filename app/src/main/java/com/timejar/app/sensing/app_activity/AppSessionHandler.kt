package com.timejar.app.sensing.app_activity

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import com.timejar.app.api.supabase.Supabase
import com.timejar.app.sensing.geofence.GeofenceJobIntentService
import com.timejar.app.sensing.notification.NotificationHandler
import com.timejar.app.sensing.screen_handler.ScreenStateHandler
import com.timejar.app.sensing.screen_handler.ScreenStateListener
import com.timejar.app.sensing.user_activity.UserActivityRecognitionService
import kotlinx.coroutines.*

val minMiliSecondsForApp = 30 * 1000 // 30 seconds
data class SupabaseData(
    val packageName: String,
    val startTime: Long,
    var appUsageTime: Long,
    val locationId: Int,
)

class AppSessionHandler(private val context: Context) : ScreenStateListener {
    private var job: Job? = null
    private var lastCheckpoint: Long = 0

    private var activityRecognitionManager: UserActivityRecognitionService? = null
    private var screenStateHandler: ScreenStateHandler? = null

    private lateinit var supabaseData: SupabaseData

    fun startSession(packageName: String): SupabaseData {
        lastCheckpoint = System.currentTimeMillis()

        Log.i("AppSessionHandler", "startSession Tracking $packageName")

        // Start tracking here (e.g., start activity recognition)
        activityRecognitionManager = UserActivityRecognitionService(context)
        activityRecognitionManager?.startTracking()

        screenStateHandler = ScreenStateHandler(context, this@AppSessionHandler)
        screenStateHandler?.registerScreenStateReceiver()

        val locationId = GeofenceJobIntentService.getCurrentPlace()
        Log.i("AppSessionHandler", "startSession Location: $locationId")

        // Fill data
        supabaseData = SupabaseData(packageName, lastCheckpoint, 0, locationId)

        job = CoroutineScope(Dispatchers.Main).launch {
            // Keep the coroutine alive until it's cancelled when the app session ends
            try {
                delay(12 * 60 * 60 * 1000) // 12 hours
            } catch (e: CancellationException) {
                handleSessionEnd()
            }
        }

        return supabaseData
    }

    fun endSession() {
        job?.cancel()
    }

    override fun suspendSession() {
        supabaseData.appUsageTime = System.currentTimeMillis() - lastCheckpoint
        lastCheckpoint = 0

        Log.d("AppSessionHandler", "Suspended session on ${supabaseData.packageName}")
    }

    override fun continueSession() {
        if (lastCheckpoint.toInt() != 0) {
            Log.d("AppSessionHandler", "continueSession lastCheckpoint is not 0, but $lastCheckpoint")
            return
        }

        lastCheckpoint = System.currentTimeMillis()
        Log.d("AppSessionHandler", "Continuing session on ${supabaseData.packageName}")
    }

    private suspend fun handleSessionEnd() {
        suspendSession()
        screenStateHandler?.unregisterScreenStateReceiver()

        if (supabaseData.appUsageTime < minMiliSecondsForApp) {
            Log.i("AppSessionHandler", "handleSessionEnd: App ${supabaseData.packageName} was used for less than ${minMiliSecondsForApp/1000} seconds (${supabaseData.appUsageTime/1000}s).")
            return  // Exit the function early if the app was used for less than 30 seconds
        }

        Log.i("AppSessionHandler", "handleSessionEnd: App closed: ${supabaseData.packageName}")

        CoroutineScope(Dispatchers.Main).launch {
            val actionId = activityRecognitionManager!!.stopTrackingAndReturnMostFrequentActivity()
            Log.i("AppSessionHandler", "handleSessionEnd: Most Frequent Activity during this period: $actionId")

            val notificationHandler = NotificationHandler(context, getAppNameFromPackageName(context, supabaseData.packageName))
            val (shouldBeBlocked, acceptanceId) = notificationHandler.handleUserDecisionNotification()

            Log.i("AppSessionHandler", "handleSessionEnd: shouldBeBlocked: $shouldBeBlocked, acceptance: $acceptanceId")

            if (!Supabase.isLoggedInWithRefresh()) {
                Log.i("AppSessionHandler", "handleSessionEnd: not logged in")
            }

            // send to Supabase
            // appUsageTime is in milliseconds, but server requests them in seconds!
            Supabase.reportActivity(supabaseData.packageName,  acceptanceId, shouldBeBlocked, actionId, supabaseData.appUsageTime/1000, supabaseData.locationId, supabaseData.startTime, onSuccess = {
                Log.i("AppSessionHandler", "handleSessionEnd reportActivity SUCCESS")
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Feedback successfully submitted", Toast.LENGTH_LONG).show()
                }
            }, onFailure = {
                it.printStackTrace()
                Log.e("AppSessionHandler", "handleSessionEnd: error: ${it.message}")
                CoroutineScope(Dispatchers.Main).launch {
                    showNotification(context, "Time-Jar error", "Error when submitting your feedback: ${it.message}")
                }
            })
        }
    }
}

fun getAppNameFromPackageName(context: Context, packageName: String): String {
    val packageManager = context.packageManager
    return try {
        val appInfo = packageManager.getApplicationInfo(packageName, 0)
        packageManager.getApplicationLabel(appInfo).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        // If the app is not found, you might want to return the package name itself or a default string
        "App not found"
    }
}

