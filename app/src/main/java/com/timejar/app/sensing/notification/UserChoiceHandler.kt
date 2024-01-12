package com.timejar.app.sensing.notification

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