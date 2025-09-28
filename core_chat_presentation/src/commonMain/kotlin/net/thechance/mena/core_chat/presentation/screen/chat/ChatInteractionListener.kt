@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


interface ChatInteractionListener : MessageListInteractionListener {

    fun onBackClicked()

    fun onInputMessageChanged(value: String)
    fun onSendMessageClicked()
}

interface MessageListInteractionListener {
    fun onMessageClicked(messageId: Uuid)

    fun onFailedMessageClicked(message: MessageUiState)

    fun onDeleteFailedMessageClicked()

    fun onResendMessageClicked()
    fun onResendMessageDialogDismissed()
}