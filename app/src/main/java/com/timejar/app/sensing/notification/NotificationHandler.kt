package com.timejar.app.sensing.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.timejar.app.R
import java.util.UUID

data class NotificationIds(
    val channelId: String,
    val blockDecisionNotificationId: Int,
    val acceptanceActionNotificationId_1: Int,
    val acceptanceActionNotificationId_2: Int
)

class NotificationHandler(private val context: Context, private val appName: String) {
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val notificationIds: NotificationIds = NotificationIds(
        generateChannelId(),
        generateNotificationId(),
        generateNotificationId(),
        generateNotificationId()
    )

    init {
        createUserDecisionNotificationChannel()
    }
    private fun generateChannelId(): String {
        return "channel_${UUID.randomUUID()}"
    }

    private fun generateNotificationId(): Int {
        return UUID.randomUUID().hashCode() // Convert UUID to an integer for the notification ID
    }

    private fun generateUniqueAction() = "com.timejar.app.ACTION_NOTIFICATION_${UUID.randomUUID()}"

    private fun createUserDecisionNotificationChannel() {
        val channelName = "User Decision Channel"
        val channelDescription = "Notifications for user decisions"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(notificationIds.channelId, channelName, importance).apply {
            description = channelDescription
        }

        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    private fun showBlockDecisionNotification() {
        // Intents and PendingIntents for block/unblock choices
        val blockIntent = Intent(context, UserDecisionReceiver::class.java).apply {
            putExtra("BLOCK_CHOICE", 1)
            putExtras(getNotificationIdsIntentExtras())
        } // "block"
        val unblockIntent = Intent(context, UserDecisionReceiver::class.java).apply {
            putExtra("BLOCK_CHOICE", 0)
            putExtras(getNotificationIdsIntentExtras())
        } // "unblock"

        val blockPendingIntent = PendingIntent.getBroadcast(context, 10, blockIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val unblockPendingIntent = PendingIntent.getBroadcast(context, 11, unblockIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        // Notification for blocking decision
        val blockNotification = NotificationCompat.Builder(context, notificationIds.channelId)
            .setContentTitle("Block ${appName} Next Time?")
            .setContentText("Choose whether to block the app in future usage.")
            .setSmallIcon(R.drawable.decision)
            .addAction(0, "Block Next Time", blockPendingIntent)
            .addAction(0, "Don't Block", unblockPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationIds.blockDecisionNotificationId, blockNotification)
    }

    private fun showAcceptanceActionNotification() {
        // Create intents for each action
        val workIntent = Intent(context, UserDecisionReceiver::class.java).apply {
            putExtra("ACCEPTANCE_CHOICE", 1)
            putExtras(getNotificationIdsIntentExtras())
        } // "work"
        val studyIntent = Intent(context, UserDecisionReceiver::class.java).apply {
            putExtra("ACCEPTANCE_CHOICE", 3)
            putExtras(getNotificationIdsIntentExtras())
        } // "study/learn"
        val relaxationIntent = Intent(context, UserDecisionReceiver::class.java).apply {
            putExtra("ACCEPTANCE_CHOICE", 2)
            putExtras(getNotificationIdsIntentExtras())
        } // "relaxation/free time"

        // Create PendingIntent for each action
        val workPendingIntent = PendingIntent.getBroadcast(context, 20, workIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val studyPendingIntent = PendingIntent.getBroadcast(context, 21, studyIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val relaxationPendingIntent = PendingIntent.getBroadcast(context, 22, relaxationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        // Create the notification with acceptance actions
        val acceptanceNotification = NotificationCompat.Builder(context, notificationIds.channelId)
            .setContentTitle("Categorize $appName usage 1")
            .setContentText("Select the category that best describes your recent app usage.")
            .setSmallIcon(R.drawable.acceptance)
            .addAction(0, "Work", workPendingIntent)
            .addAction(0, "Study", studyPendingIntent)
            .addAction(0, "Relax", relaxationPendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationIds.acceptanceActionNotificationId_1, acceptanceNotification)
    }

    private fun showWasteChoiceNotification() {
        // Intent and PendingIntent for the "Waste" choice
        val wasteIntent = Intent(context, UserDecisionReceiver::class.java).apply {
            putExtra("ACCEPTANCE_CHOICE", 4)
            putExtras(getNotificationIdsIntentExtras())
        } // "waste"

        val wastePendingIntent = PendingIntent.getBroadcast(context, 23, wasteIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        // Notification specifically for the "Waste" choice
        val wasteNotification = NotificationCompat.Builder(context, notificationIds.channelId)
            .setContentTitle("Categorize $appName usage 2")
            .setSmallIcon(R.drawable.acceptance)
            .addAction(0, "Waste", wastePendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationIds.acceptanceActionNotificationId_2, wasteNotification)
    }

    // Method to pass the NotificationIds to the UserDecisionReceiver
    private fun getNotificationIdsIntentExtras(): Bundle {
        return bundleOf(
            "CHANNEL_ID" to notificationIds.channelId,
            "BLOCK_DECISION_ID" to notificationIds.blockDecisionNotificationId,
            "ACCEPTANCE_ACTION_1_ID" to notificationIds.acceptanceActionNotificationId_1,
            "ACCEPTANCE_ACTION_2_ID" to notificationIds.acceptanceActionNotificationId_2
        )
    }

    suspend fun handleUserDecisionNotification(): Pair<Int, Int> {
        // Show the notification
        showBlockDecisionNotification()
        showWasteChoiceNotification()
        showAcceptanceActionNotification()

        // Wait for the user's choices
        val (shouldBeBlocked, acceptance) = UserChoiceHandler.awaitUserChoices(
            notificationIds.blockDecisionNotificationId,
            notificationIds.acceptanceActionNotificationId_1,
            notificationIds.acceptanceActionNotificationId_2
        )

        // Log choices
        Log.i("handleUserDecisionNotification", "User block app: $shouldBeBlocked")
        Log.i("handleUserDecisionNotification", "User action: $acceptance")

        return Pair(shouldBeBlocked ?: 0, acceptance ?: 4) // Provide default values if null
    }
}