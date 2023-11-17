package com.timejar.app.detectedactivity

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.ActivityRecognition
import com.timejar.app.*

const val ACTIVITY_UPDATES_INTERVAL = 1000L

class DetectedActivityService : Service() {

  inner class LocalBinder : Binder() {

    val serverInstance: DetectedActivityService
      get() = this@DetectedActivityService
  }

  override fun onBind(p0: Intent?): IBinder = LocalBinder()

  override fun onCreate() {
    super.onCreate()
    requestActivityUpdates()
  }

  @SuppressLint("MissingPermission")
  private fun requestActivityUpdates() {
    val client = ActivityRecognition.getClient(this)
    val task = client.requestActivityUpdates(ACTIVITY_UPDATES_INTERVAL, DetectedActivityReceiver.getPendingIntent(this))

    task.run {
      addOnSuccessListener {
        Log.d("ActivityUpdate", getString(R.string.activity_update_request_success))
      }
      addOnFailureListener {
        Log.d("ActivityUpdate", getString(R.string.activity_update_request_failed))
      }
    }
  }

  @SuppressLint("MissingPermission")
  private fun removeActivityUpdates() {
    val client = ActivityRecognition.getClient(this)
    val task = client.removeActivityUpdates(DetectedActivityReceiver.getPendingIntent(this))

    task.run {
      addOnSuccessListener {
        Log.d("ActivityUpdate", getString(R.string.activity_update_remove_success))
      }
      addOnFailureListener {
        Log.d("ActivityUpdate", getString(R.string.activity_update_remove_failed))
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    removeActivityUpdates()
    NotificationManagerCompat.from(this).cancel(DETECTED_ACTIVITY_NOTIFICATION_ID)
  }
}