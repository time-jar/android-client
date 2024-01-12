package com.timejar.app.sensing.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class UserDecisionReceiver(private val notificationIds: NotificationIds) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val blockChoice = intent.getIntExtra("BLOCK_CHOICE", -1)
        val acceptanceChoice = intent.getIntExtra("ACCEPTANCE_CHOICE", -1)

        if (blockChoice != -1) {
            Log.i("UserDecisionReceiver", "Block choice received: $blockChoice")
            UserChoiceHandler.choiceReceived(blockChoice = blockChoice)
            // Cancel the block decision notification
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationIds.blockDecisionNotificationId)
        }

        if (acceptanceChoice != -1) {
            Log.i("UserDecisionReceiver", "Acceptance choice received: $acceptanceChoice")
            UserChoiceHandler.choiceReceived(acceptanceChoice = acceptanceChoice)
            // Cancel the acceptance action notification
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationIds.acceptanceActionNotificationId_1)
            notificationManager.cancel(notificationIds.acceptanceActionNotificationId_2)
        }
    }
}
