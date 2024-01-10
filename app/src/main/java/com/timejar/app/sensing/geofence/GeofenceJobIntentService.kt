package com.timejar.app.sensing.geofence

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
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
                        "Home" -> {
                            return when (currentGeofencingEvent?.geofenceTransition) {
                                Geofence.GEOFENCE_TRANSITION_ENTER,
                                Geofence.GEOFENCE_TRANSITION_DWELL -> {
                                    Log.d("handleAppOpened", "home enter/dwell")
                                    4
                                }
                                Geofence.GEOFENCE_TRANSITION_EXIT -> {
                                    Log.d("handleAppOpened", "home exit")
                                    1
                                }
                                else -> 1
                            }
                        }
                        "Work" -> {
                            return when (currentGeofencingEvent?.geofenceTransition) {
                                Geofence.GEOFENCE_TRANSITION_ENTER,
                                Geofence.GEOFENCE_TRANSITION_DWELL -> {
                                    Log.d("handleAppOpened", "work enter/dwell")
                                    2
                                }
                                Geofence.GEOFENCE_TRANSITION_EXIT -> {
                                    Log.d("handleAppOpened", "work exit")
                                    1
                                }
                                else -> 1
                            }
                        }
                        "School" -> {
                            return when (currentGeofencingEvent?.geofenceTransition) {
                                Geofence.GEOFENCE_TRANSITION_ENTER,
                                Geofence.GEOFENCE_TRANSITION_DWELL -> {
                                    Log.d("handleAppOpened", "school enter/dwell")
                                    3
                                }
                                Geofence.GEOFENCE_TRANSITION_EXIT -> {
                                    Log.d("handleAppOpened", "school exit")
                                    1
                                }
                                else -> 1
                            }
                        }
                    }
                }
            }

            Log.d("handleAppOpened", "no transition")
            return 1
        }




    }

    override fun onHandleWork(intent: Intent) {

        Log.d(TAG, "onHandleWork")

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                val errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.errorCode)
                Log.e(TAG, errorMessage)
                return
            }
        }

        setCurrentGeofencingEvent(geofencingEvent!!)

    }
}
