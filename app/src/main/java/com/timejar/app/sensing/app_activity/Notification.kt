package com.timejar.app.sensing.app_activity

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.timejar.app.R

val notificationId = 65785685
val notificationChannel = "AppActivityAccessibilityService notifications"

fun createNotificationChannel(context: Context) {
        val channelName = "My Channel Name"
        val channelDescription = "My Channel Description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(notificationChannel, channelName, importance).apply {
            description = channelDescription
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
}

@SuppressLint("PrivateResource")
fun showNotification(context: Context, title: String, content: String) {
    val notificationBuilder = NotificationCompat.Builder(context, notificationChannel).apply {
        setSmallIcon(com.google.android.material.R.drawable.mtrl_ic_error)
        setContentTitle(title)
        setContentText(content)
        setStyle(NotificationCompat.BigTextStyle().bigText(content)) // Use BigTextStyle for longer content
        priority = NotificationCompat.PRIORITY_DEFAULT
    }

    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(notificationId, notificationBuilder.build())
}
