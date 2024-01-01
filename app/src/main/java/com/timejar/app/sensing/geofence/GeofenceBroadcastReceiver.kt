package com.timejar.app.sensing.geofence


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "GeofenceBR"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive")
        if (context != null && intent != null) {
            GeofenceJobIntentService.enqueueWork(context, intent)
        }
    }
}