package com.timejar.app.map

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.timejar.app.R

class GeofenceJobIntentService : JobIntentService() {

    companion object {

        const val TAG = "GeofenceJobIS"
        const val JOB_ID = 123
        const val CHANNELID = "si.uni_lj.fri.lrk.geofencingapp.GEOFENCING_EVENTS"
        const val NOTIFICATIONID = 101

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, GeofenceJobIntentService::class.java, JOB_ID, intent)
        }
    }

    override fun onCreate() {
        super.onCreate()

        createChannel(CHANNELID, "Geofencing", "Reporting geofencing events")
    }

    override fun onHandleWork(intent: Intent) {

        Log.d(TAG, "onHandleWork")

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                val errorMessage = geofencingEvent?.let {
                    GeofenceStatusCodes
                        .getStatusCodeString(it.errorCode)
                }
                if (errorMessage != null) {
                    Log.d(TAG, errorMessage)
                }
                return
            }
        }

        val geofenceTransition = geofencingEvent?.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){

            Log.d(TAG, "geofenceTransition + $geofenceTransition")

            val triggeringGeofences = geofencingEvent?.triggeringGeofences

            val geofenceTransitionTitle = triggeringGeofences?.let {
                getGeofenceTitle(geofenceTransition,
                    it
                )
            }

            val geofenceTransitionDetails = triggeringGeofences?.let { getToDoString(it) }

            if (geofenceTransitionTitle != null) {
                if (geofenceTransitionDetails != null) {
                    sendNotification(geofenceTransitionTitle, geofenceTransitionDetails)
                }
            }

        }
    }

    private fun getGeofenceTitle(geofenceTransition: Int, triggeringGeofences: List<Geofence>): String {

        val geofenceTransitionString = when(geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> getString(R.string.geofence_transition_entered)
            Geofence.GEOFENCE_TRANSITION_DWELL -> getString(R.string.geofence_transition_dwelled)
            Geofence.GEOFENCE_TRANSITION_EXIT -> getString(R.string.geofence_transition_exited)
            else -> getString(R.string.unknown_geofence_transition)
        }

        val triggeringGeofencesIdsList = ArrayList<String>()
        for (geofence in triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.requestId)
        }

        val triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList)

        return "$geofenceTransitionString: $triggeringGeofencesIdsString"
    }

    private fun getToDoString(triggeringGeofences: List<Geofence>): String {

        var todoString = ""
        for (geofence in triggeringGeofences) {
            if (geofence.requestId.equals(getString(R.string.map_marker_home))) {
                todoString += getString(R.string.todo_home)
            }
            if (geofence.requestId.equals(getString(R.string.map_marker_work))) {
                todoString += getString(R.string.todo_work)
            }
            if (geofence.requestId.equals(getString(R.string.map_marker_fitness))) {
                todoString += getString(R.string.todo_fitness)
            }
        }
        return todoString
    }

    private fun createChannel(id: String, name: String, desc: String) {
        if(Build.VERSION.SDK_INT>=26){
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance)
            with(channel) {
                description = desc
                lightColor = Color.RED
                enableLights(true)
            }

            with(NotificationManagerCompat.from(this)) {
                createNotificationChannel(channel)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendNotification(notificationTitle: String, notificationDetails: String) {

        val notificationIntent = Intent(applicationContext, MapsActivity::class.java)
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val notifPendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val newNotification = NotificationCompat.Builder(this, CHANNELID)
            .setColor(ContextCompat.getColor(this, R.color.design_default_color_primary))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(notificationTitle)
            .setContentText(notificationDetails)
            .setContentIntent(notifPendingIntent)
            .setAutoCancel(true)
            .build();

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATIONID, newNotification)
        }
    }
}