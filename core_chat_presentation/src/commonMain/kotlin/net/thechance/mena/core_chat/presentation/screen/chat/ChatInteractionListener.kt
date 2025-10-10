@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


interface ChatInteractionListener : MessageListInteractionListener, FullImageViewInteractionListener {

    fun onBackClicked()

    fun onInputMessageChanged(value: String)
    fun onSendMessageClicked()

    fun onMessageImageClicked(message: MessageUiState, initialImageIndex: Int)
}

interface MessageListInteractionListener {
    fun onMessageClicked(messageId: Uuid)

    fun onFailedMessageClicked(message: MessageUiState)

    fun onDeleteFailedMessageClicked()

    fun onResendMessageClicked()
    fun onResendMessageDialogDismissed()
}

interface AttachmentsInteractionListener {
    fun onPhotoClicked()
    fun onCameraClicked()
    fun onCancelClicked()
}

interface FullImageViewInteractionListener {
    fun onDownloadImageClicked(url: String)
    fun onCloseClicked()
}