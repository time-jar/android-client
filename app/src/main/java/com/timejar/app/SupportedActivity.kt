package com.timejar.app

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.gms.location.DetectedActivity
import java.lang.IllegalArgumentException

const val SUPPORTED_ACTIVITY_KEY = "activity_key"

enum class SupportedActivity(
    @DrawableRes val activityImage: Int,
    @StringRes val activityText: Int
) {

  NOT_STARTED(R.drawable.activity_recognition, R.string.activity_recognition),
  STILL(R.drawable.standing, R.string.still_text),
  WALKING(R.drawable.walking, R.string.walking_text),
  RUNNING(R.drawable.running, R.string.running_text);

  companion object {

    fun fromActivityType(type: Int): SupportedActivity = when (type) {
      DetectedActivity.STILL -> STILL
      DetectedActivity.WALKING -> WALKING
      DetectedActivity.RUNNING -> RUNNING
      else -> throw IllegalArgumentException("activity $type not supported")
    }
  }
}