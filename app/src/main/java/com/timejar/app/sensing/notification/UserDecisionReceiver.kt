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

const val CHANNEL_ID = "user_decision_channel"
const val BLOCK_DECISION_NOTIFICATION_ID = 101
const val ACCEPTANCE_ACTION_NOTIFICATION_ID = 102

fun createUserDecisionNotificationChannel(context: Context) {
    // Check if the Android version is Oreo (API 26) or higher, as channels are not supported below this version.
    val channelId = "user_decision_channel"
    val channelName = "User Decision Channel"
    val channelDescription = "Notifications for user decisions"
    val importance = NotificationManager.IMPORTANCE_DEFAULT

    val channel = NotificationChannel(channelId, channelName, importance).apply {
        description = channelDescription
    }

    // Register the channel with the system
    val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}


class UserDecisionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val blockChoice = intent.getBooleanExtra("BLOCK_CHOICE", false)
        val acceptanceChoice = intent.getIntExtra("ACCEPTANCE_CHOICE", 1)

        UserChoiceHandler.shouldBeBlockedChoiceReceived(blockChoice)
        UserChoiceHandler.acceptanceChoiceReceived(acceptanceChoice)
    }
}

object UserChoiceHandler {
    private val shouldBeBlockedChoiceFlow = MutableSharedFlow<Boolean?>()
    private val acceptanceChoiceFlow = MutableSharedFlow<Int?>()

    fun shouldBeBlockedChoiceReceived(choice: Boolean?) {
        CoroutineScope(Dispatchers.Main).launch {
            shouldBeBlockedChoiceFlow.emit(choice)
        }
    }

    fun acceptanceChoiceReceived(choice: Int?) {
        CoroutineScope(Dispatchers.Main).launch {
            acceptanceChoiceFlow.emit(choice)
        }
    }

    suspend fun awaitShouldBeBlockedChoice(): Boolean? = shouldBeBlockedChoiceFlow.first()
    suspend fun awaitAcceptanceChoice(): Int? = acceptanceChoiceFlow.first()
}


fun showBlockDecisionNotification(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Intents and PendingIntents for block/unblock choices
    val blockIntent = Intent(context, UserDecisionReceiver::class.java).apply { putExtra("BLOCK_CHOICE", 1) } // "block"
    val unblockIntent = Intent(context, UserDecisionReceiver::class.java).apply { putExtra("BLOCK_CHOICE", 0) } // "unblock"
    val blockPendingIntent = PendingIntent.getBroadcast(context, 0, blockIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    val unblockPendingIntent = PendingIntent.getBroadcast(context, 1, unblockIntent, PendingIntent.FLAG_UPDATE_CURRENT)

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
    val workIntent = Intent(context, UserDecisionReceiver::class.java).apply { putExtra("USER_CHOICE", 1) } // "work"
    val studyIntent = Intent(context, UserDecisionReceiver::class.java).apply { putExtra("USER_CHOICE", 3) } // "study/learn"
    val relaxationIntent = Intent(context, UserDecisionReceiver::class.java).apply { putExtra("USER_CHOICE", 2) } // "relaxation/free time"
    val wasteIntent = Intent(context, UserDecisionReceiver::class.java).apply { putExtra("USER_CHOICE", 4) } // "waste"

    // Create PendingIntent for each action
    val workPendingIntent = PendingIntent.getBroadcast(context, 0, workIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    val studyPendingIntent = PendingIntent.getBroadcast(context, 1, studyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    val relaxationPendingIntent = PendingIntent.getBroadcast(context, 2, relaxationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    val wastePendingIntent = PendingIntent.getBroadcast(context, 3, wasteIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    // Create the notification with acceptance actions
    val acceptanceNotification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle("Categorize App Usage")
        .setContentText("Select the category that best describes your recent app usage.")
        .setSmallIcon(R.drawable.acceptance)
        .addAction(0, "Work", workPendingIntent)
        .addAction(0, "Study/Learn", studyPendingIntent)
        .addAction(0, "Relaxation", relaxationPendingIntent)
        .addAction(0, "Waste", wastePendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(ACCEPTANCE_ACTION_NOTIFICATION_ID, acceptanceNotification)
}


suspend fun handleUserDecisionNotification(context: Context): Pair<Boolean, Int> {
    // Show the notification
    showBlockDecisionNotification(context)
    showAcceptanceActionNotification(context)

    // Default values
    val defaultBlockChoice = false
    val defaultAcceptanceChoice = 4

    // Wait for the user's choice and provide default values if null
    val shouldBeBlocked = UserChoiceHandler.awaitShouldBeBlockedChoice() ?: defaultBlockChoice
    val acceptance = UserChoiceHandler.awaitAcceptanceChoice() ?: defaultAcceptanceChoice

    // Log choices
    Log.d("UserChoice", "User block app: $shouldBeBlocked")
    Log.d("UserAction", "User action: $acceptance")

    // Return non-nullable values
    return Pair(shouldBeBlocked, acceptance)
}
