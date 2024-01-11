package com.timejar.app.sensing.screen_handler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

interface ScreenStateListener {
    fun suspendSession()
    fun continueSession()
}

class ScreenStateHandler(private val context: Context, private val listener: ScreenStateListener) {
    private var screenStateReceiver: BroadcastReceiver? = null

    fun registerScreenStateReceiver() {
        screenStateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_OFF -> listener.suspendSession()
                    Intent.ACTION_SCREEN_ON -> listener.continueSession()
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        context.registerReceiver(screenStateReceiver, filter)
    }

    fun unregisterScreenStateReceiver() {
        screenStateReceiver?.let { context.unregisterReceiver(it) }
    }
}
