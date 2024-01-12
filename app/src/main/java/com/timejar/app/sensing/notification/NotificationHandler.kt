package com.timejar.app.sensing.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.timejar.app.R
import java.util.UUID
import android.content.BroadcastReceiver
import android.content.Context.RECEIVER_NOT_EXPORTED
import android.content.IntentFilter
import android.os.Build
import androidx.annotation.RequiresApi

data class NotificationIds(
    val channelId: String,
    val blockDecisionNotificationId: Int,
    val acceptanceActionNotificationId_1: Int,
    val acceptanceActionNotificationId_2: Int,
    val blockDecisionAction: String,
    val acceptanceAction: String
)

class NotificationHandler(private val context: Context, private val appName: String) {
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val notificationIds: NotificationIds = NotificationIds(
        generateChannelId(),
        generateNotificationId(),
        generateNotificationId(),
        generateNotificationId(),
        generateUniqueAction(),
        generateUniqueAction()
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
        notificationManager.createNotificationChannel(channel)
    }


    private fun showBlockDecisionNotification() {
        // Intents and PendingIntents for block/unblock choices
        val blockIntent = Intent(notificationIds.blockDecisionAction).apply {putExtra("BLOCK_CHOICE", 1) } // "block"
        val unblockIntent = Intent(notificationIds.blockDecisionAction).apply {putExtra("BLOCK_CHOICE", 0) } // "unblock"
        blockIntent.setPackage(context.packageName) // required for RECEIVER_NOT_EXPORTED to make it work
        unblockIntent.setPackage(context.packageName) // required for RECEIVER_NOT_EXPORTED to make it work

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

        // Register receiver
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val blockChoice = intent.getIntExtra("BLOCK_CHOICE", -1)

                Log.i("UserDecisionReceiver", "Block choice received: $blockChoice")
                UserChoiceHandler.choiceReceived(blockChoice, null, notificationIds.blockDecisionNotificationId, notificationIds.acceptanceActionNotificationId_1, notificationIds.acceptanceActionNotificationId_2)
                // Cancel the block decision notification
                notificationManager.cancel(notificationIds.blockDecisionNotificationId)

                // Unregister this receiver
                context.unregisterReceiver(this)
            }
        }

        context.registerReceiver(receiver, IntentFilter(notificationIds.blockDecisionAction), RECEIVER_NOT_EXPORTED)
    }

    private fun showAcceptanceActionNotification() {
        // Create intents for each action
        val workIntent = Intent(notificationIds.acceptanceAction).apply { putExtra("ACCEPTANCE_CHOICE", 1) } // "work"
        val studyIntent = Intent(notificationIds.acceptanceAction).apply {putExtra("ACCEPTANCE_CHOICE", 3) } // "study/learn"
        val relaxationIntent = Intent(notificationIds.acceptanceAction).apply {putExtra("ACCEPTANCE_CHOICE", 2) } // "relaxation/free time"
        workIntent.setPackage(context.packageName) // required for RECEIVER_NOT_EXPORTED to make it work
        studyIntent.setPackage(context.packageName) // required for RECEIVER_NOT_EXPORTED to make it work
        relaxationIntent.setPackage(context.packageName) // required for RECEIVER_NOT_EXPORTED to make it work

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

        // Register receiver
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val acceptanceChoice = intent.getIntExtra("ACCEPTANCE_CHOICE", -1)

                Log.i("UserDecisionReceiver", "Acceptance choice received: $acceptanceChoice")
                UserChoiceHandler.choiceReceived(null, acceptanceChoice, notificationIds.blockDecisionNotificationId, notificationIds.acceptanceActionNotificationId_1, notificationIds.acceptanceActionNotificationId_2)
                // Cancel the acceptance action notification
                notificationManager.cancel(notificationIds.acceptanceActionNotificationId_1)
                notificationManager.cancel(notificationIds.acceptanceActionNotificationId_2)

                // Unregister this receiver
                context.unregisterReceiver(this)
            }
        }

        context.registerReceiver(receiver, IntentFilter(notificationIds.acceptanceAction), RECEIVER_NOT_EXPORTED)
    }

    private fun showWasteChoiceNotification() {
        // Intent and PendingIntent for the "Waste" choice
        val wasteIntent = Intent(notificationIds.acceptanceAction).apply {putExtra("ACCEPTANCE_CHOICE", 4) } // "waste"
        wasteIntent.setPackage(context.packageName) // required for RECEIVER_NOT_EXPORTED to make it work

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