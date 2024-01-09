package com.timejar.app.sensing.geofence

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.gms.location.GeofencingEvent

class GeofenceJobIntentService : JobIntentService() {

    companion object {

        const val TAG = "GeofenceJobIS"
        private const val JOB_ID = 123

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, GeofenceJobIntentService::class.java, JOB_ID, intent)
        }

        private var currentGeofencingEvent: GeofencingEvent? = null

        fun setCurrentGeofencingEvent(geofencingEvent: GeofencingEvent) {
            currentGeofencingEvent = geofencingEvent
        }

        fun getCurrentPlace(): Int {
            val triggeringGeofences = currentGeofencingEvent?.triggeringGeofences

            if (!triggeringGeofences.isNullOrEmpty()) {
                for (geofence in triggeringGeofences) {
                    when (geofence.requestId) {
                        "Home" -> return 4
                        "Work" -> return 2
                        "School" -> return 3
                    }
                }
            }
            return 1
        }

    }

    override fun onHandleWork(intent: Intent) {

        Log.d(TAG, "onHandleWork")

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        setCurrentGeofencingEvent(geofencingEvent!!)

    }
}
