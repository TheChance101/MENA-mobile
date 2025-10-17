@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.error
import mena.core_chat_presentation.generated.resources.error_cant_get_messages
import mena.core_chat_presentation.generated.resources.error_cant_subscribe_to_new_messages
import mena.core_chat_presentation.generated.resources.error_failed_to_download_image
import mena.core_chat_presentation.generated.resources.image_saved_successfully
import mena.core_chat_presentation.generated.resources.success
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.ImagesSource
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.presentation.components.SnackBarData
import net.thechance.mena.core_chat.presentation.navigation.ChatEffector
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.core_chat.presentation.utils.getUuidOrNull
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class ChatViewModel(
    private val chatRepository: ChatRepository,
    chatArgs: ChatArgs,
    effector: ChatEffector,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ChatScreenState>(ChatScreenState(), effector, dispatcher),
    ChatInteractionListener {

    private val _uiMessages = MutableStateFlow<List<MessageUiState>>(emptyList())
    private val uiMessages = _uiMessages.asStateFlow()


    init {
        val chatId = getUuidOrNull(chatArgs.chatId)

        updateState { state ->
            state.copy(
                chatId = chatId,
                chatName = chatArgs.chatName
            )
        }

        if (chatId == null) {
            onGetChatError()
        } else {
            tryToExecute(
                execute = { chatRepository.getChatById(chatId) },
                onSuccess = ::onGetChatSuccess,
                onError = { onGetChatError() }
            )
        }
    }

    private fun onGetChatSuccess(chat: Chat) {
        updateInitialState(
            chatId = chat.id,
            requesterUserId = chat.requesterId,
            chatAvatarUrl = chat.imageUrl.orEmpty()
        )
    }

    private fun onGetChatError() {
        showSnackBar(
            titleStringResource = Res.string.error,
            messageStringResource = Res.string.error_cant_get_messages,
            isError = true
        )
        popBackStack()
    }

    private fun updateInitialState(
        chatId: Uuid,
        requesterUserId: Uuid,
        chatAvatarUrl: String
    ) {
        updateState { state ->
            state.copy(
                chatId = chatId,
                chatAvatarUrl = chatAvatarUrl,
                chatRequesterId = requesterUserId,
            )
        }

        subscribeToNewMessages(chatId)
        loadChatHistory(chatId)
        observeReadMessages()
    }

    override fun onBackClicked() {
        popBackStack()
    }

    override fun onSendImageClicked(imageByteArrays: List<ByteArray>) {
        val chatId = state.value.chatId
        val senderId = state.value.chatRequesterId

        if (chatId == null || senderId == null || imageByteArrays.isEmpty()) {
            return
        }

        val content = MessageContent.Images(ImagesSource.Local(imageByteArrays))

        sendImageMessage(chatId, senderId, content)
    }

    private fun sendImageMessage(chatId: Uuid, senderId: Uuid, content: MessageContent) {
        val message = MessageUiState(
            chatId = chatId,
            senderId = senderId,
            content = content
        )

        updateStateWithNewMessage(message)

        tryToExecute(
            execute = { chatRepository.sendMessage(message.toEntity()) },
            onSuccess = { onSendMessageSuccess(message) },
            onError = { onSendMessageError(message) },
        )
    }

    override fun onInputMessageChanged(value: String) {
        updateState { state -> state.copy(inputMessage = value) }
    }

    override fun onSendMessageClicked() {
        val text = state.value.inputMessage.trim()
        val chatId = state.value.chatId
        val senderId = state.value.chatRequesterId

        if (chatId == null || senderId == null || text.isEmpty()) return

        val content = MessageContent.Text(text)
        val message = MessageUiState(
            chatId = chatId,
            senderId = senderId,
            content = content
        )
        sendMessage(message)
    }

    private fun sendMessage(message: MessageUiState) {
        updateStateWithNewMessage(message)
        updateState { state -> state.copy(inputMessage = "") }

        tryToExecute(
            execute = { chatRepository.sendMessage(message.toEntity()) },
            onSuccess = { onSendMessageSuccess(message) },
            onError = { onSendMessageError(message) },
        )
    }

    private fun onSendMessageSuccess(message: MessageUiState) {
        filterMessagesState { it.id != message.id && it.sendTime != message.sendTime }
    }

    private fun onSendMessageError(message: MessageUiState) {
        updateStateWithNewMessage(message.copy(status = MessageStatus.FAILED))
    }

    override fun onMessageClicked(messageId: Uuid) {
        updateState { state ->
            state.copy(chatListItems = state.chatListItems.toggleMessageInfo(messageId))
        }
    }

    override fun onFailedMessageClicked(message: MessageUiState) {
        updateState { state ->
            state.copy(
                isResendMessageDialogVisible = true,
                failedMessageToReSend = message
            )
        }
    }

    override fun onDeleteFailedMessageClicked() {
        val failedMessage = state.value.failedMessageToReSend ?: return

        tryToExecute(
            execute = { chatRepository.deleteMessage(failedMessage.toEntity()) },
            onSuccess = { onDeleteFailedMessageSuccess(failedMessage) }
        )
    }

    private fun onDeleteFailedMessageSuccess(failedMessage: MessageUiState) {
        filterMessagesState { it.id != failedMessage.id }
        updateState { state ->
            state.copy(
                failedMessageToReSend = null,
                isResendMessageDialogVisible = false
            )
        }
    }

    override fun onResendMessageClicked() {
        val message =
            state.value.failedMessageToReSend?.copy(status = MessageStatus.LOADING) ?: return

        updateState { state ->
            state.copy(
                isResendMessageDialogVisible = false,
                failedMessageToReSend = null
            )
        }
        updateStateWithNewMessage(message)

        sendMessage(message)
    }

    override fun onResendMessageDialogDismissed() {
        updateState { it.copy(isResendMessageDialogVisible = false) }
    }

    private fun subscribeToNewMessages(chatId: Uuid) {
        tryToCollect(
            collect = { chatRepository.subscribeToMessages(chatId) },
            onCollect = ::onCollectNewMessage,
            onError = {
                showSnackBar(
                    Res.string.error,
                    Res.string.error_cant_subscribe_to_new_messages,
                    true
                )
            },
        )
    }

    private fun onCollectNewMessage(message: Message?) {
        if (message == null) return

        val senderId = state.value.chatRequesterId
            ?: return showSnackBar(Res.string.error, Res.string.error_cant_get_messages, true)

        updateStateWithNewMessage(message.toUi(senderId))
    }

    private fun loadChatHistory(chatId: Uuid) {
        tryToExecute(
            execute = {
                val messagesHistory = chatRepository.loadMessages(chatId)
                val pendingMessages = chatRepository.getLocalMessages(chatId)
                (messagesHistory + pendingMessages)
            },
            onSuccess = ::onLoadChatHistorySuccess,
            onError = { showSnackBar(Res.string.error, Res.string.error_cant_get_messages, true) }
        )
    }

    private fun onLoadChatHistorySuccess(messages: List<Message>) {
        val senderId = state.value.chatRequesterId
            ?: return showSnackBar(Res.string.error, Res.string.error_cant_get_messages, true)

        _uiMessages.update { messages.map { it.toUi(senderId) } }
        updateChatListItems(uiMessages.value)

        uiMessages.value
            .filter { it.status == MessageStatus.LOADING }
            .forEach { sendMessage(it) }
    }

    private fun observeReadMessages() {
        tryToCollect(
            collect = { chatRepository.observeReadMessages() },
            onCollect = ::onCollectReadMessagesEvent
        )
    }

    private fun onCollectReadMessagesEvent(readerId: String?) {
        if (readerId == null) return

        mapMessagesState { message ->
            if (message.senderId.toString() != readerId && message.status == MessageStatus.SENT)
                message.copy(status = MessageStatus.READ)
            else message
        }

    }

    private fun updateStateWithNewMessage(newMessage: MessageUiState) {
        _uiMessages.update { messages ->
            messages.toMutableList().apply { add(0, newMessage) }
        }
        updateChatListItems(uiMessages.value)
    }

    private fun mapMessagesState(transform: (MessageUiState) -> MessageUiState) {
        _uiMessages.update { messages -> messages.map(transform) }
        updateChatListItems(uiMessages.value)
    }

    private fun filterMessagesState(predicate: (MessageUiState) -> Boolean) {
        _uiMessages.update { messages -> messages.filter(predicate) }
        updateChatListItems(uiMessages.value)
    }

    private fun updateChatListItems(messages: List<MessageUiState>) {
        updateState { state ->
            state.copy(
                chatListItems = messages
                    .distinctBy { it.id }
                    .sortedByDescending { it.sendTime }
                    .buildListItems()
            )
        }
    }

    override fun onMessageImageClicked(message: MessageUiState, initialImageIndex: Int) {
        updateState {
            it.copy(
                isImagePagerVisible = true,
                selectedMessage = message,
                currentImageIndexForPreview = initialImageIndex
            )
        }
    }

    override fun onDownloadImageClicked(url: String) {
        tryToExecute(
            execute = { chatRepository.downloadImage(url) },
            onSuccess = { onDownloadImageSuccess() },
            onError = {
                showSnackBar(
                    Res.string.error,
                    Res.string.error_failed_to_download_image,
                    true
                )
            }
        )
    }

    private fun onDownloadImageSuccess() {
        showSnackBar(Res.string.success, Res.string.image_saved_successfully, isError = false)
    }

    override fun onCloseImageViewClicked() {
        updateState {
            it.copy(
                isImagePagerVisible = false,
                selectedMessage = null,
                currentImageIndexForPreview = 0
            )
        }
    }

    private fun showSnackBar(
        titleStringResource: StringResource,
        messageStringResource: StringResource,
        isError: Boolean = false
    ) {
        showSnackBar(
            SnackBarData(
                title = UiText.StringRes(titleStringResource),
                message = UiText.StringRes(messageStringResource),
                isError = isError
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        tryToExecute(
            coroutineScope = CoroutineScope(Dispatchers.IO), // Required to avoid cancellation
            execute = { chatRepository.disconnect() }
        )
    }

    override fun onAttachmentClicked() {
        updateState { it.copy(isAttachmentsOverlayVisible = true) }
    }

    override fun onGalleryClicked() {
        updateState { it.copy(isAttachmentsOverlayVisible = false) }
    }

    override fun onCameraClicked() {
        updateState { it.copy(isAttachmentsOverlayVisible = false) }
    }

    override fun onCloseAttachmentClicked() {
        updateState { it.copy(isAttachmentsOverlayVisible = false) }
    }
}
