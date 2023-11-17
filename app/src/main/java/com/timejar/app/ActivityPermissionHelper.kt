package com.timejar.app

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat

const val PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 1000

fun Activity.requestPermission() {
  if (ActivityCompat.shouldShowRequestPermissionRationale(this,
          Manifest.permission.ACTIVITY_RECOGNITION).not()) {
    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
        PERMISSION_REQUEST_ACTIVITY_RECOGNITION)
  } else {
    showRationalDialog(this)
  }
}

fun Activity.isPermissionGranted(): Boolean {
  val isAndroidQOrLater: Boolean =
      android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q

  return if (isAndroidQOrLater.not()) {
    true
  } else {
    PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACTIVITY_RECOGNITION
    )
  }
}

private fun showRationalDialog(activity: Activity) {
  AlertDialog.Builder(activity).apply {
    setTitle(R.string.permission_rational_dialog_title)
    setMessage(R.string.permission_rational_dialog_message)
    setPositiveButton(R.string.permission_rational_dialog_positive_button_text) { _, _ ->
      ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
          PERMISSION_REQUEST_ACTIVITY_RECOGNITION)
    }
    setNegativeButton(R.string.permission_rational_dialog_negative_button_text){ dialog, _ ->
      dialog.dismiss()
    }
  }.run {
    create()
    show()
  }
}

fun showSettingsDialog(activity: Activity){
  AlertDialog.Builder(activity).apply {
    setTitle(R.string.settings_dialog_title)
    setMessage(R.string.settings_dialog_message)
    setPositiveButton(R.string.settings_dialog_positive_button_text) { _, _ ->
      startAppSettings(activity)
    }
    setNegativeButton(R.string.settings_dialog_negative_button_text){ dialog, _ ->
      dialog.dismiss()
    }
  }.run {
    create()
    show()
  }
}

private fun startAppSettings(context: Context) {
  val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
  val uri: Uri = Uri.fromParts("package", context.packageName, null)
  intent.data = uri
  context.startActivity(intent)
}