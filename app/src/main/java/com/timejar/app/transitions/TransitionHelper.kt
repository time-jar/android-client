package com.timejar.app.transitions

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import com.timejar.app.R

@SuppressLint("MissingPermission")
fun Activity.requestActivityTransitionUpdates() {
    val request = ActivityTransitionRequest(getActivitiesToTrack())
    val client = ActivityRecognition.getClient(this)
    val task = client.requestActivityTransitionUpdates(request, TransitionsReceiver.getPendingIntent(this))

  task.run {
    addOnSuccessListener {
      Log.d("TransitionUpdate", getString(R.string.transition_update_request_success))
    }
    addOnFailureListener {
      Log.d("TransitionUpdate", getString(R.string.transition_update_request_failed))
    }
  }
}

@SuppressLint("MissingPermission")
fun Activity.removeActivityTransitionUpdates() {
    val client = ActivityRecognition.getClient(this)
    val task = client.removeActivityTransitionUpdates(TransitionsReceiver.getPendingIntent(this))

  task.run {
    addOnSuccessListener {
      Log.d("TransitionUpdate", getString(R.string.transition_update_remove_success))
    }
    addOnFailureListener {
      Log.d("TransitionUpdate", getString(R.string.transition_update_remove_failed))
    }
  }
}

private fun getActivitiesToTrack(): List<ActivityTransition> =
    mutableListOf<ActivityTransition>()
        .apply {
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.STILL)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
              .build())
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.STILL)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
              .build())
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.WALKING)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
              .build())
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.WALKING)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
              .build())
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.RUNNING)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
              .build())
          add(ActivityTransition.Builder()
              .setActivityType(DetectedActivity.RUNNING)
              .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
              .build())
        }