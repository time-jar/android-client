package com.timejar.app.sensing.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.timejar.app.R
import kotlinx.coroutines.flow.first
import java.util.UUID


data class UserChoices(
    val blockChoice: Int? = null,
    val acceptanceChoice: Int? = null
)

fun generateChannelId(): String {
    return "channel_${UUID.randomUUID()}"
}

fun generateNotificationId(): Int {
    return UUID.randomUUID().hashCode() // Convert UUID to an integer for the notification ID
}

fun createUserDecisionNotificationChannel(context: Context) {
    val channelName = "User Decision Channel"
    val channelDescription = "Notifications for user decisions"
    val importance = NotificationManager.IMPORTANCE_DEFAULT

    val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
        description = channelDescription
    }

    // Register the channel with the system
    val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}


class UserDecisionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val blockChoice = intent.getIntExtra("BLOCK_CHOICE", -1)
        val acceptanceChoice = intent.getIntExtra("ACCEPTANCE_CHOICE", -1)

        if (blockChoice != -1) {
            Log.i("UserDecisionReceiver", "Block choice received: $blockChoice")
            UserChoiceHandler.choiceReceived(blockChoice = blockChoice)
            // Cancel the block decision notification
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(BLOCK_DECISION_NOTIFICATION_ID)
        }

        if (acceptanceChoice != -1) {
            Log.i("UserDecisionReceiver", "Acceptance choice received: $acceptanceChoice")
            UserChoiceHandler.choiceReceived(acceptanceChoice = acceptanceChoice)
            // Cancel the acceptance action notification
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(ACCEPTANCE_ACTION_1_NOTIFICATION_ID)
            notificationManager.cancel(ACCEPTANCE_ACTION_2_NOTIFICATION_ID)
        }
    }
}

object UserChoiceHandler {
    private var userChoicesFlow = MutableSharedFlow<UserChoices>(replay = 1)
    private var currentChoices = UserChoices()

    fun choiceReceived(blockChoice: Int? = null, acceptanceChoice: Int? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            currentChoices = currentChoices.copy(
                blockChoice = blockChoice ?: currentChoices.blockChoice,
                acceptanceChoice = acceptanceChoice ?: currentChoices.acceptanceChoice
            )
            userChoicesFlow.emit(currentChoices)
        }
    }

    suspend fun awaitUserChoices(): UserChoices {
        val choices = userChoicesFlow.first { it.blockChoice != null && it.acceptanceChoice != null }
        resetChoices()  // Reset choices after they've been handled
        return choices
    }

    private fun resetChoices() {
        currentChoices = UserChoices()  // Reset the current choices
        userChoicesFlow = MutableSharedFlow(replay = 1)  // Recreate the SharedFlow
    }
}

fun showBlockDecisionNotification(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Intents and PendingIntents for block/unblock choices
    val blockIntent = Intent(context, UserDecisionReceiver::class.java).apply { putExtra("BLOCK_CHOICE", 1) } // "block"
    val unblockIntent = Intent(context, UserDecisionReceiver::class.java).apply { putExtra("BLOCK_CHOICE", 0) } // "unblock"

    val blockPendingIntent = PendingIntent.getBroadcast(context, 10, blockIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    val unblockPendingIntent = PendingIntent.getBroadcast(context, 11, unblockIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    // Notification for blocking decision
    val blockNotification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle("Block App Next Time?")
        .setContentText("Choose whether to block the app in future usage.")
        .setSmallIcon(R.drawable.decision)
        .addAction(0, "Block Next Time", blockPendingIntent)
        .addAction(0, "Don't Block", unblockPendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(BLOCK_DECISION_NOTIFICATION_ID, blockNotification)
}

fun showAcceptanceActionNotification(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Create intents for each action
    val workIntent = Intent(context, UserDecisionReceiver::class.java).apply { putExtra("ACCEPTANCE_CHOICE", 1) } // "work"
    val studyIntent = Intent(context, UserDecisionReceiver::class.java).apply { putExtra("ACCEPTANCE_CHOICE", 3) } // "study/learn"
    val relaxationIntent = Intent(context, UserDecisionReceiver::class.java).apply { putExtra("ACCEPTANCE_CHOICE", 2) } // "relaxation/free time"

    // Create PendingIntent for each action
    val workPendingIntent = PendingIntent.getBroadcast(context, 20, workIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    val studyPendingIntent = PendingIntent.getBroadcast(context, 21, studyIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    val relaxationPendingIntent = PendingIntent.getBroadcast(context, 22, relaxationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    // Create the notification with acceptance actions
    val acceptanceNotification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle("Categorize App Usage")
        .setContentText("Select the category that best describes your recent app usage.")
        .setSmallIcon(R.drawable.acceptance)
        .addAction(0, "Work", workPendingIntent)
        .addAction(0, "Study", studyPendingIntent)
        .addAction(0, "Relax", relaxationPendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(ACCEPTANCE_ACTION_1_NOTIFICATION_ID, acceptanceNotification)
}

fun showWasteChoiceNotification(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Intent and PendingIntent for the "Waste" choice
    val wasteIntent = Intent(context, UserDecisionReceiver::class.java).apply { putExtra("ACCEPTANCE_CHOICE", 4) } // "waste"
    val wastePendingIntent = PendingIntent.getBroadcast(context, 23, wasteIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    // Notification specifically for the "Waste" choice
    val wasteNotification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.acceptance)
        .addAction(0, "Waste", wastePendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(ACCEPTANCE_ACTION_2_NOTIFICATION_ID, wasteNotification)
}

suspend fun handleUserDecisionNotification(context: Context): Pair<Int, Int> {
    // Show the notification
    showBlockDecisionNotification(context)
    showWasteChoiceNotification(context)
    showAcceptanceActionNotification(context)

    // Wait for the user's choices
    val (shouldBeBlocked, acceptance) = UserChoiceHandler.awaitUserChoices()

    // Log choices
    Log.i("handleUserDecisionNotification", "User block app: $shouldBeBlocked")
    Log.i("handleUserDecisionNotification", "User action: $acceptance")

    return Pair(shouldBeBlocked ?: 0, acceptance ?: 4) // Provide default values if null
}
