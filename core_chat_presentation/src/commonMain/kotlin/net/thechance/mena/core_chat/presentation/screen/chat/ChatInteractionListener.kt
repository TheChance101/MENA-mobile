@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import androidx.compose.ui.graphics.ImageBitmap
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


interface ChatInteractionListener : MessageListInteractionListener,
    FullImageViewInteractionListener, AttachmentsInteractionListener, ActionsMenuInteractionListener {

    fun onBackClicked()
    fun onAttachmentClicked()
    fun onInputMessageChanged(value: String)
    fun onSendTextMessageClicked()
    fun onMessageImageClicked(messages: List<ImageMessageUiState>, initialImageIndex: Int)
    fun onMessageLongClicked(message: MessageUiState)
    fun onReactionDialogDismissed()
    fun onReactionSelected(messageId: Uuid, reaction: String)

    fun onMessageVoiceClicked(messageId: Uuid)

    fun onRecordClicked()
    fun onCancelRecordClicked()
    fun onSendRecordClicked()

    fun onStopAudioPlayback()

    fun onLinkClicked(url: String)
}

interface MessageListInteractionListener {
    fun onMessageClicked(messageId: Uuid)
    fun onFailedMessageClicked(message: MessageUiState)
    fun onDeleteFailedMessageClicked()
    fun onResendMessageClicked()
    fun onResendMessageDialogDismissed()

    fun onMessagesScrolled()
}

interface AttachmentsInteractionListener {
    fun onSendImageClicked(imageByteArrays: List<ByteArray>)
    fun onGalleryClicked()
    fun onCameraClicked()
    fun onCameraResult(image: ImageBitmap?)
    fun onCloseAttachmentClicked()
}

interface FullImageViewInteractionListener {
    fun onDownloadImageClicked(url: String)
    fun onCloseImageViewClicked()
}

interface ActionsMenuInteractionListener {
    fun onChatActionsMenuClicked()
    fun onChatActionsMenuDialogDismissed()
    fun onConfirmDeleteChatDialogDismissed()
    fun onDeleteChatClicked()
    fun onConfirmDeleteChatClicked()
}