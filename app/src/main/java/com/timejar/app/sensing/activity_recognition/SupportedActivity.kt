package com.timejar.app

import com.google.android.gms.location.DetectedActivity
import java.lang.IllegalArgumentException

enum class SupportedActivity {

  STILL,
  WALKING,
  RUNNING;
  companion object {
    fun fromActivityType(type: Int): SupportedActivity = when (type) {
      DetectedActivity.STILL -> STILL
      DetectedActivity.WALKING -> WALKING
      DetectedActivity.RUNNING -> RUNNING
      else -> throw IllegalArgumentException("activity $type not supported")
    }
  }
}