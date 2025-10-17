@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.error
import mena.core_chat_presentation.generated.resources.error_cant_get_messages
import mena.core_chat_presentation.generated.resources.error_cant_subscribe_to_new_messages
import mena.core_chat_presentation.generated.resources.error_failed_to_download_image
import mena.core_chat_presentation.generated.resources.error_get_user_info
import mena.core_chat_presentation.generated.resources.image_saved_successfully
import mena.core_chat_presentation.generated.resources.permission_denied_title
import mena.core_chat_presentation.generated.resources.success
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.ImagesSource
import net.thechance.mena.core_chat.domain.entity.MarkMessageAsReadEvent
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.entity.User
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.UserRepository
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
    private val userRepository: UserRepository,
    chatArgs: ChatArgs,
    effector: ChatEffector,
    private val permissionsController: PermissionsController,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ChatScreenState>(ChatScreenState(), effector, dispatcher),
    ChatInteractionListener {

    private var uiMessages: List<MessageUiState> = emptyList()

    init {
        val chatId = getUuidOrNull(chatArgs.chatId)
        getUserInfo()
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

    private fun getUserInfo(){
        tryToExecute(
            execute = { userRepository.getUserInfo() },
            onSuccess = ::onGetUserDataSuccess,
            onError = ::onGetUserDataError
        )
    }

    private fun onGetUserDataSuccess(user: User) {
        updateState { state -> state.copy(userData = UserData(
            firstName = user.firstName,
            lastName = user.lastName,
            imageUrl = user.imageUrl.orEmpty()
        )) }
    }
    private fun onGetUserDataError(t: Throwable) {
        showSnackBar(
            titleStringResource = Res.string.error,
            messageStringResource = Res.string.error_get_user_info,
            isError = true
        )
    }
    private fun onGetChatSuccess(chat: Chat) {
        updateState { state ->
            state.copy(
                chatId = chat.id,
                chatAvatarUrl = chat.imageUrl.orEmpty(),
                chatRequesterId = chat.requesterId,
            )
        }

        subscribeToNewMessages(chat.id)
        loadChatHistory(chat.id)
        observeReadMessages()
    }

    private fun onGetChatError() {
        showSnackBar(
            titleStringResource = Res.string.error,
            messageStringResource = Res.string.error_cant_get_messages,
            isError = true
        )
        popBackStack()
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
            collect = { chatRepository.getMessages(chatId) },
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

    private suspend fun onCollectNewMessage(message: Message?) {
        if (message == null) return

        val senderId = state.value.chatRequesterId
            ?: return showSnackBar(Res.string.error, Res.string.error_cant_get_messages, true)

        updateStateWithNewMessage(message.toUi(senderId))
        chatRepository.markMessagesAsRead(message.chatId)
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

    private suspend fun onLoadChatHistorySuccess(messages: List<Message>) {
        val senderId = state.value.chatRequesterId
            ?: return showSnackBar(Res.string.error, Res.string.error_cant_get_messages, true)

        val uiMessages = messages.map { it.toUi(senderId) }
        updateChatListItems(uiMessages)

        uiMessages
            .filter { it.status == MessageStatus.LOADING }
            .forEach { sendMessage(it) }

        chatRepository.markMessagesAsRead(state.value.chatId ?: return)
    }

    private fun observeReadMessages() {
        tryToCollect(
            collect = { chatRepository.observeReadMessages() },
            onCollect = ::onCollectReadMessagesEvent
        )
    }

    private fun onCollectReadMessagesEvent(markMessageAsReadEvent: MarkMessageAsReadEvent?) {
        if (markMessageAsReadEvent == null) return

        mapMessagesState { message ->
            if (message.senderId != markMessageAsReadEvent.readByUserId && message.status == MessageStatus.SENT)
                message.copy(status = MessageStatus.READ)
            else message
        }

    }

    private fun updateStateWithNewMessage(newMessage: MessageUiState) {
        val messages = uiMessages.toMutableList()
            .apply { add(0, newMessage) }
        updateChatListItems(messages)
    }

    private fun mapMessagesState(transform: (MessageUiState) -> MessageUiState) {
        val messages = uiMessages.map(transform)
        updateChatListItems(messages)
    }

    private fun filterMessagesState(predicate: (MessageUiState) -> Boolean) {
        val messages = uiMessages.filter(predicate)
        updateChatListItems(messages)
    }

    private fun updateChatListItems(messages: List<MessageUiState>) {
        uiMessages = messages
            .distinctBy { it.id }
            .sortedByDescending { it.sendTime }
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

    override fun onAttachmentClicked() {
        updateState { it.copy(isAttachmentsOverlayVisible = true) }
    }

    override fun onGalleryClicked() {
        updateState { it.copy(isAttachmentsOverlayVisible = false) }
    }

    override fun onCameraClicked() {
        tryToExecute(
            execute = { permissionsController.providePermission(permission = Permission.CAMERA) },
            onSuccess = { onCameraPermissionGranted() },
            onError = { showSnackBar(Res.string.error, Res.string.permission_denied_title, true) }
        )
    }

    private fun onCameraPermissionGranted() {
        updateState { it.copy(isCameraOpen = true, isAttachmentsOverlayVisible = false) }
    }
    override fun onCameraClosed() {
        updateState { it.copy(isCameraOpen = false) }
    }
    override fun onCloseAttachmentClicked() {
        updateState { it.copy(isAttachmentsOverlayVisible = false) }
    }
}
