package com.timejar.app.sensing.notification

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class UserChoices(
    val blockChoice: Int? = null,
    val acceptanceChoice: Int? = null
)

object UserChoiceHandler {
    private val choiceMap = mutableMapOf<Int, MutableSharedFlow<UserChoices>>()

    fun choiceReceived(blockChoice: Int?, acceptanceChoice: Int?, blockDecisionId: Int, acceptanceActionId1: Int, acceptanceActionId2: Int) {
        val key = getKey(blockDecisionId, acceptanceActionId1, acceptanceActionId2)
        val flow = choiceMap.getOrPut(key) { MutableSharedFlow(replay = 1) }

        Log.d("UserChoiceHandler", "choiceReceived for key: $key")

        CoroutineScope(Dispatchers.Main).launch {
            val currentChoices = flow.replayCache.lastOrNull() ?: UserChoices()
            val newChoices = currentChoices.copy(
                blockChoice = blockChoice ?: currentChoices.blockChoice,
                acceptanceChoice = acceptanceChoice ?: currentChoices.acceptanceChoice
            )
            flow.emit(newChoices)

            Log.d("UserChoiceHandler", "emit $newChoices")


            var choice = "undefined"
            var value = -1
            if (blockChoice != null) {
                choice = "blockChoice"
                value = blockChoice
            }
            if (acceptanceChoice != null) {
                choice = "acceptanceChoice"
                value = acceptanceChoice
            }

            Log.d("UserChoiceHandler", "choiceReceived $choice: $value")
        }
    }

    suspend fun awaitUserChoices(blockDecisionId: Int, acceptanceActionId1: Int, acceptanceActionId2: Int): UserChoices {
        val key = getKey(blockDecisionId, acceptanceActionId1, acceptanceActionId2)
        val flow = choiceMap.getOrPut(key) { MutableSharedFlow(replay = 1) }

        Log.d("UserChoiceHandler", "Awaiting choices for key: $key")
        val choices = flow.first { it.blockChoice != null && it.acceptanceChoice != null }
        Log.d("UserChoiceHandler", "Choices received: $choices")

        choiceMap.remove(key)

        return choices
    }

    private fun getKey(blockDecisionId: Int, acceptanceActionId1: Int, acceptanceActionId2: Int): Int {
        // Implement a method to generate a unique key based on the IDs
        return blockDecisionId.hashCode() xor acceptanceActionId1.hashCode() xor acceptanceActionId2.hashCode()
    }
}