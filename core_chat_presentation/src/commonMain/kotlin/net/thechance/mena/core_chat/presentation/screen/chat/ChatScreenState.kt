@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.domain.entity.AudioData
import net.thechance.mena.core_chat.domain.entity.ImageData
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.entity.MessageReaction
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.core_chat.presentation.utils.now
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ChatScreenState(
    val chatId: Uuid? = null,
    val chatName: String = "",
    val chatAvatarUrl: String = "",
    val chatRequesterId: Uuid? = null,
    val inputMessage: String = "",
    val chatListItems: List<ChatListItem> = emptyList(),
    val isChatActionsDialogVisible: Boolean = false,
    val isSendMoneyDialogVisible: Boolean = false,
    val amountToTransfer: String = "",
    val receiverId: Uuid? = null,
    val isLoadingSendMoneyButton: Boolean = false,
    val isConfirmDeleteChatDialogVisible: Boolean = false,
    val currentImageIndexForPreview: Int = 0,
    val isResendMessageDialogVisible: Boolean = false,
    val isAttachmentsOverlayVisible: Boolean = false,
    val isCameraOpen: Boolean = false,
    val failedMessageToReSend: MessageUiState? = null,
    val userData: UserData = UserData(),
    val selectedMessage: MessageUiState? = null,
    val isImagePagerVisible: Boolean = false,
    val selectedImageMessages: List<ImageMessageUiState> = emptyList(),
    val isReactionDialogVisible: Boolean = false,
    val messageToReactTo: MessageUiState? = null,
    val isRecordingVoice: Boolean = false,
) {
    val isSendMoneyButtonEnabled: Boolean
        get() = amountToTransfer.isNotBlank() &&
                amountToTransfer.toDoubleOrNull()?.let { it > 0 } == true
}

data class UserData(
    val firstName: String = "",
    val lastName: String = "",
    val imageUrl: String = ""
)

sealed interface ChatListItem

data class DateSeparator(val label: UiText) : ChatListItem

sealed class MessageUiState(open val messageDetails: MessageDetailsUiState) : ChatListItem {
    fun copyMessage(messageDetails: MessageDetailsUiState): MessageUiState = when (this) {
        is AudioMessageUiState -> copy(messageDetails = messageDetails)
        is ImageMessageUiState -> copy(messageDetails = messageDetails)
        is TextMessageUiState -> copy(messageDetails = messageDetails)
        is AyahMessageUiState -> copy(messageDetails = messageDetails)
        is MoneyMessageUiState -> copy(messageDetails = messageDetails)
        is OrderMessageUiState -> copy(messageDetails = messageDetails)
    }
}

data class MessageDetailsUiState(
    val id: Uuid = Uuid.random(),
    val senderId: Uuid = Uuid.random(),
    val chatId: Uuid = Uuid.random(),
    val sendTime: LocalDateTime = LocalDateTime.now(),
    val status: MessageStatus = MessageStatus.LOADING,
    val isMine: Boolean = true,
    val isLastInSeries: Boolean = false,
    val isVisibleMessageInfo: Boolean = false,
    val reactions: List<MessageReaction> = emptyList(),
)

data class TextMessageUiState(
    val text: String,
    override val messageDetails: MessageDetailsUiState,
) : MessageUiState(messageDetails)

data class ImagesGroupChatItem(
    val imagesUiState: List<ImageMessageUiState>
) : ChatListItem

data class ImageMessageUiState(
    val imageDate: ImageData,
    override val messageDetails: MessageDetailsUiState,
) : MessageUiState(messageDetails)

data class AyahMessageUiState(
    val surahId: Int,
    val ayahContent: String,
    val ayahNumber: Int,
    val surahName: String,
    override val messageDetails: MessageDetailsUiState
) : MessageUiState(messageDetails)

data class AudioMessageUiState(
    val data: AudioData,
    val isPlaying: Boolean,
    val isLoading: Boolean,
    val progress: Float,
    val duration: Long,
    val waveformData: List<Float> = emptyList(),
    override val messageDetails: MessageDetailsUiState
) : MessageUiState(messageDetails)

data class MoneyMessageUiState(
    val amount: Double,
    override val messageDetails: MessageDetailsUiState
) : MessageUiState(messageDetails)

data class OrderMessageUiState(
    val orderId: Uuid,
    val numberOfItems: Int,
    val deliverTo: String,
    val totalPrice: Double,
    override val messageDetails: MessageDetailsUiState
) : MessageUiState(messageDetails)