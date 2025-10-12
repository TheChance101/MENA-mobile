@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


interface ChatInteractionListener : MessageListInteractionListener, AttachmentsInteractionListener {
    fun onBackClicked()
    fun onAttachmentClicked()
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

interface AttachmentsInteractionListener {
    fun onSendImageClicked(imageByteArrays: List<ByteArray>)
    fun onGalleryClicked()
    fun onCameraClicked()
    fun onCloseAttachmentClicked()
}
