package com.timejar.app

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.timejar.app.detectedactivity.DetectedActivityService
import com.timejar.app.transitions.TRANSITIONS_RECEIVER_ACTION
import com.timejar.app.transitions.TransitionsReceiver
import com.timejar.app.transitions.removeActivityTransitionUpdates
import com.timejar.app.transitions.requestActivityTransitionUpdates

class MainActivity : AppCompatActivity() {

    private lateinit var startBtn: Button
    private lateinit var stopBtn: Button
    private lateinit var resetBtn: Button
    private lateinit var activityImage: ImageView
    private lateinit var activityTitle: TextView

    private var isTrackingStarted = false
        set(value) {
            resetBtn.visibility = if(value) View.VISIBLE else View.GONE
            field = value
        }

    private val transitionBroadcastReceiver: TransitionsReceiver = TransitionsReceiver().apply {
        action = { setDetectedActivity(it) }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra(SUPPORTED_ACTIVITY_KEY)) {
            val supportedActivity = intent.getSerializableExtra(SUPPORTED_ACTIVITY_KEY) as? SupportedActivity
            supportedActivity?.let {
                setDetectedActivity(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startBtn = findViewById(R.id.startBtn)
        stopBtn = findViewById(R.id.stopBtn)
        resetBtn = findViewById(R.id.resetBtn)
        activityImage = findViewById(R.id.activityImage)
        activityTitle = findViewById(R.id.activityTitle)

        startBtn.setOnClickListener {
            if (isPermissionGranted()) {
                startService(Intent(this, DetectedActivityService::class.java))
                requestActivityTransitionUpdates()
                isTrackingStarted = true
                Toast.makeText(this@MainActivity, "You've started activity tracking",
                    Toast.LENGTH_SHORT).show()
            } else {
                requestPermission()
            }
        }
        stopBtn.setOnClickListener {
            stopService(Intent(this, DetectedActivityService::class.java))
            removeActivityTransitionUpdates()

            Toast.makeText(this, "You've stopped tracking your activity", Toast.LENGTH_SHORT).show()
        }
        resetBtn.setOnClickListener {
            resetTracking()
        }
    }

    private fun resetTracking() {
        isTrackingStarted = false
        setDetectedActivity(SupportedActivity.NOT_STARTED)
        removeActivityTransitionUpdates()
        stopService(Intent(this, DetectedActivityService::class.java))
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(transitionBroadcastReceiver, IntentFilter(TRANSITIONS_RECEIVER_ACTION))
    }

    override fun onPause() {
        unregisterReceiver(transitionBroadcastReceiver)
        super.onPause()
    }

    override fun onDestroy() {
        removeActivityTransitionUpdates()
        stopService(Intent(this, DetectedActivityService::class.java))
        super.onDestroy()
    }

    private fun setDetectedActivity(supportedActivity: SupportedActivity) {
        activityImage.setImageDrawable(ContextCompat.getDrawable(this, supportedActivity.activityImage))
        activityTitle.text = getString(supportedActivity.activityText)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACTIVITY_RECOGNITION).not() &&
            grantResults.size == 1 &&
            grantResults[0] == PackageManager.PERMISSION_DENIED) {
            showSettingsDialog(this)
        } else if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION &&
            permissions.contains(Manifest.permission.ACTIVITY_RECOGNITION) &&
            grantResults.size == 1 &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("permission_result", "permission granted")
            startService(Intent(this, DetectedActivityService::class.java))
            requestActivityTransitionUpdates()
            isTrackingStarted = true
        }
    }
}