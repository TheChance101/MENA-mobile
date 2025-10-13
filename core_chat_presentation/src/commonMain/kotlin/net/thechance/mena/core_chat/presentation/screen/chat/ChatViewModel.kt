@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.datetime.LocalDateTime
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.error
import mena.core_chat_presentation.generated.resources.error_cant_get_messages
import mena.core_chat_presentation.generated.resources.error_cant_subscribe_to_new_messages
import net.thechance.mena.core_chat.domain.entity.Chat
import mena.core_chat_presentation.generated.resources.error_failed_to_download_image
import mena.core_chat_presentation.generated.resources.image_saved_successfully
import mena.core_chat_presentation.generated.resources.success
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
import net.thechance.mena.core_chat.presentation.utils.now
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

    private var uiMessages: List<MessageUiState> = emptyList()

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
        showSnackBar(titleStringResource = Res.string.error, messageStringResource = Res.string.error_cant_get_messages, isError = true)
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
            id = Uuid.random(),
            chatId = chatId,
            senderId = senderId,
            sendTime = LocalDateTime.now(),
            isMine = true,
            status = MessageStatus.LOADING,
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

        // todo temp text content
        val content = MessageContent.Text(text)
        sendMessage(chatId, senderId, content)
    }

    private fun sendMessage(chatId: Uuid, senderId: Uuid, content: MessageContent) {
        val message = MessageUiState(
            chatId = chatId,
            senderId = senderId,
            content = content
        )

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
        val message = state.value.failedMessageToReSend ?: return

        updateState { state ->
            state.copy(
                isResendMessageDialogVisible = false,
                failedMessageToReSend = null
            )
        }
        updateStateWithNewMessage(message.copy(status = MessageStatus.LOADING))

        sendMessage(
            chatId = message.chatId,
            senderId = message.senderId,
            content = message.content
        )
    }

    override fun onResendMessageDialogDismissed() {
        updateState { it.copy(isResendMessageDialogVisible = false) }
    }

    private fun subscribeToNewMessages(chatId: Uuid) {
        tryToCollect(
            collect = { chatRepository.subscribeToMessages(chatId) },
            onCollect = ::onCollectNewMessage,
            onError = { showSnackBar(Res.string.error, Res.string.error_cant_subscribe_to_new_messages, true) },
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
            ?: return  showSnackBar(Res.string.error, Res.string.error_cant_get_messages, true)

        val uiMessages = messages.map { it.toUi(senderId) }
        updateChatListItems(uiMessages)

        messages
            .filter { it.status == MessageStatus.LOADING }
            .forEach { sendMessage(chatId = it.chatId, senderId = senderId, content = it.content) }
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
        val messages = uiMessages.toMutableList()
            .apply { add(0, newMessage) }
            .distinctBy { it.id }
            .sortedByDescending { it.sendTime }
        updateChatListItems(messages)
    }

    private fun mapMessagesState(transform: (MessageUiState) -> MessageUiState) {
        val messages = uiMessages.map(transform).distinctBy { it.id }
            .sortedByDescending { it.sendTime }
        updateChatListItems(messages)
    }

    private fun filterMessagesState(predicate: (MessageUiState) -> Boolean) {
        val messages = uiMessages.filter(predicate).distinctBy { it.id }
            .sortedByDescending { it.sendTime }
        updateChatListItems(messages)
    }

    private fun updateChatListItems(messages: List<MessageUiState>) {
        uiMessages = messages.distinctBy { it.id }.sortedByDescending { it.sendTime }
        updateState { state ->
            state.copy(
                chatListItems = messages.buildListItems()
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
            onError = { showSnackBar(Res.string.error, Res.string.error_failed_to_download_image, true) }
        )
    }

    private fun onDownloadImageSuccess() {
        showSnackBar(Res.string.success, Res.string.image_saved_successfully,isError = false)    }

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
