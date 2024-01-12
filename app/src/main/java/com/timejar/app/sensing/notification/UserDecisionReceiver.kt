package com.timejar.app.sensing.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class UserDecisionReceiver() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val channelId = intent.getStringExtra("CHANNEL_ID") ?: return
        val blockDecisionNotificationId = intent.getIntExtra("BLOCK_DECISION_ID", -1)
        val acceptanceActionNotificationId_1 = intent.getIntExtra("ACCEPTANCE_ACTION_1_ID", -1)
        val acceptanceActionNotificationId_2 = intent.getIntExtra("ACCEPTANCE_ACTION_2_ID", -1)

        val blockChoice = intent.getIntExtra("BLOCK_CHOICE", -1)
        val acceptanceChoice = intent.getIntExtra("ACCEPTANCE_CHOICE", -1)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (blockChoice != -1) {
            Log.i("UserDecisionReceiver", "Block choice received: $blockChoice")
            UserChoiceHandler.choiceReceived(blockChoice, null, blockDecisionNotificationId, acceptanceActionNotificationId_1, acceptanceActionNotificationId_2)
            // Cancel the block decision notification
            notificationManager.cancel(blockDecisionNotificationId)
        }

        if (acceptanceChoice != -1) {
            Log.i("UserDecisionReceiver", "Acceptance choice received: $acceptanceChoice")
            UserChoiceHandler.choiceReceived(null, acceptanceChoice, blockDecisionNotificationId, acceptanceActionNotificationId_1, acceptanceActionNotificationId_2)
            // Cancel the acceptance action notification
            notificationManager.cancel(acceptanceActionNotificationId_1)
            notificationManager.cancel(acceptanceActionNotificationId_2)
        }
    }
}
