@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.domain.entity.MessageContent
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
    val isConfirmDeleteChatDialogVisible: Boolean = false,
    val currentImageIndexForPreview: Int = 0,
    val isResendMessageDialogVisible: Boolean = false,
    val isAttachmentsOverlayVisible: Boolean = false,
    val isCameraOpen: Boolean = false,
    val failedMessageToReSend: MessageUiState? = null,
    val userData: UserData = UserData(),
    val selectedMessage: MessageUiState? = null,
    val isImagePagerVisible: Boolean = false,
    val paginationError: Boolean = false,
    val selectedImageMessages: List<MessageUiState> = emptyList(),
    val isReactionDialogVisible: Boolean = false,
    val messageToReactTo: MessageUiState? = null,
    val isRecordingVoice : Boolean = false
)

data class UserData(
    val firstName: String = "",
    val lastName: String = "",
    val imageUrl: String = ""
)

sealed interface ChatListItem {
    data class DateSeparator(val label: UiText) : ChatListItem
    data class TextMessage(val data: MessageUiState) : ChatListItem
    data class ImageMessages(val data: List<MessageUiState>) : ChatListItem
    data class VoiceMessage(
        val data: MessageUiState,
        val isPlaying: Boolean,
        val isLoading: Boolean,
        val progress: Float,
        val duration: Long,
        val waveformData: List<Float> = emptyList()
    ) : ChatListItem
}

data class MessageUiState(
    val id: Uuid = Uuid.random(),
    val senderId: Uuid = Uuid.random(),
    val chatId: Uuid = Uuid.random(),
    val sendTime: LocalDateTime = LocalDateTime.now(),
    val status: MessageStatus = MessageStatus.LOADING,
    val isMine: Boolean = true,
    val isLastInSeries: Boolean = false,
    val isVisibleMessageInfo: Boolean = false,
    val content: MessageContent,
    val reactions: List<MessageReaction> = emptyList(),
    val waveformData: List<Float>? = null
)