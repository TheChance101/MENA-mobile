@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.datetime.LocalDateTime
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.error
import mena.core_chat_presentation.generated.resources.error_cant_get_messages
import mena.core_chat_presentation.generated.resources.error_cant_subscribe_to_new_messages
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.presentation.components.SnackBarData
import net.thechance.mena.core_chat.presentation.navigation.ChatEffector
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.core_chat.presentation.utils.getUuidOrNull
import net.thechance.mena.core_chat.presentation.utils.now
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ChatViewModel(
    private val chatRepository: ChatRepository,
    chatArgs: ChatArgs,
    effector: ChatEffector,
    private val permissionsController: PermissionsController,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ChatScreenState>(ChatScreenState(), effector, defaultDispatcher),
    ChatInteractionListener {

    init {
        val chatId = getUuidOrNull(chatArgs.chatId)
        val requesterUserId = getUuidOrNull(chatArgs.chatRequesterId)

        if (chatId == null || requesterUserId == null) {
            showSnackBarAndNavigateBack()
        } else {
            updateState {
                it.copy(
                    chat = ChatUiState(
                        id = chatId,
                        name = chatArgs.chatName,
                        avatarUrl = chatArgs.chatImageUrl,
                        requesterId = requesterUserId
                    )
                )
            }

            subscribeToNewMessages(chatId)
            loadChatHistory(chatId)
            observeReadMessages()
        }
    }

    private fun showSnackBarAndNavigateBack() {
        showSnackBar(
            SnackBarData(
                title = UiText.StringRes(Res.string.error),
                message = UiText.StringRes(Res.string.error_cant_get_messages)
            )
        )
        popBackStack()
    }


    override fun onBackClicked() {
        popBackStack()
    }

    override fun onSendImageClicked(imageByteArrays: List<ByteArray>) {
        val chatId = state.value.chat.id
        val requesterId = state.value.chat.requesterId
        val now = LocalDateTime.now()

        imageByteArrays.forEach { bytes ->
            val uiMessage = MessageUiState(
                id = Uuid.random(),
                chatId = chatId,
                senderId = requesterId,
                sendTime = now,
                status = MessageStatusUiState.SENDING,
                isMine = true,
                imageBytes = bytes,
                text = null
            )

            tryToExecute(
                execute = { chatRepository.sendMessage(uiMessage.toEntity()) },
                onSuccess = { onSendMessageSuccess(uiMessage) },
                onError = { onSendMessageError(uiMessage) }
            )
        }
    }


    override fun onInputMessageChanged(value: String) {
        updateState { it.copy(inputMessage = value) }
    }

    override fun onSendMessageClicked() {
        val chatId = state.value.chat.id
        val text = state.value.inputMessage.trim()
        if (text.isEmpty()) return

        val now = LocalDateTime.now()
        val uiMessage = MessageUiState(
            chatId = chatId,
            sendTime = now,
            senderId = state.value.chat.requesterId,
            status = MessageStatusUiState.SENDING,
            isMine = true,
            text = text
        )

        updateState { s ->
            val newMessages = s.uiMessages + uiMessage
            s.copy(
                uiMessages = newMessages.sortedByDescending { it.sendTime },
                inputMessage = "",
                chatListItems = buildListItems(newMessages)
            )
        }

        tryToExecute(
            execute = { chatRepository.sendMessage(uiMessage.toEntity()) },
            onSuccess = { onSendMessageSuccess(uiMessage) },
            onError = { onSendMessageError(uiMessage) },
        )
    }

    private fun onSendMessageSuccess(message: MessageUiState) {
        val updatedMessages =
            state.value.uiMessages.filterNot { it.id == message.id && it.sendTime == message.sendTime }
        updateState {
            it.copy(
                uiMessages = updatedMessages,
                chatListItems = buildListItems(updatedMessages),
            )
        }
    }

    private fun onSendMessageError(message: MessageUiState) {
        updateStateWithNewMessage(message.copy(status = MessageStatusUiState.FAILED))
    }

    override fun onMessageClicked(messageId: Uuid) {
        updateState {
            it.copy(
                chatListItems = it.chatListItems.map { item ->
                    if (item is ChatListItem.Message && item.data.message.id == messageId) {
                        item.copy(
                            data = item.data.copy(
                                showMessageInfo = !item.data.showMessageInfo
                            )
                        )
                    } else {
                        item
                    }
                }
            )
        }
    }


    override fun onFailedMessageClicked(message: MessageUiState) {
        updateState {
            it.copy(
                isResendMessageDialogVisible = true,
                failedMessageToReSend = message
            )
        }
    }

    override fun onDeleteFailedMessageClicked() {
        state.value.failedMessageToReSend?.let { failedMessage ->
            tryToExecute(
                execute = { chatRepository.deleteMessage(failedMessage.toEntity()) },
                onSuccess = { onDeleteFailedMessageSuccess(failedMessage) }
            )
        }
    }

    private fun onDeleteFailedMessageSuccess(failedMessage: MessageUiState) {
        updateState { s ->
            val updatedMessages = s.uiMessages.filterNot { it.id == failedMessage.id }
                .sortedByDescending { it.sendTime }
            s.copy(
                uiMessages = updatedMessages,
                chatListItems = buildListItems(updatedMessages),
                failedMessageToReSend = null,
                isResendMessageDialogVisible = false
            )
        }
    }

    override fun onResendMessageClicked() {
        updateState { it.copy(isResendMessageDialogVisible = false) }
        val failedMessage = state.value.failedMessageToReSend
        updateState { s ->
            val updatedMessages = s.uiMessages.toMutableList().mapIndexed { index, message ->
                if (message.id == failedMessage?.id) {
                    failedMessage.copy(status = MessageStatusUiState.SENDING)
                } else {
                    message
                }
            }
            s.copy(
                uiMessages = updatedMessages,
                failedMessageToReSend = null,
            )
        }
        failedMessage?.let { message ->
            tryToExecute(
                execute = { chatRepository.sendMessage(message.toEntity()) },
                onSuccess = { onSendMessageSuccess(message) },
                onError = { onSendMessageError(message) },
            )
        }
    }

    override fun onResendMessageDialogDismissed() {
        updateState { it.copy(isResendMessageDialogVisible = false) }
    }

    private fun loadChatHistory(chatId: Uuid) {
        tryToExecute(
            execute = {
                val serverMessages = chatRepository.loadMessages(chatId)
                val localMessages = chatRepository.getLocalMessages(chatId)
                (serverMessages + localMessages)
            },
            onSuccess = ::onLoadChatHistorySuccess,
            onError = ::onLoadChatHistoryError
        )
    }

    private fun onLoadChatHistorySuccess(messages: List<Message>) {
        val uiMessages =
            messages.map { it.toUi(state.value.chat.requesterId) }
                .sortedByDescending { it.sendTime }
        updateState { s ->
            s.copy(
                uiMessages = uiMessages,
                chatListItems = buildListItems(uiMessages)
            )
        }

        val loadingMessages = messages.filter { it.status == MessageStatus.LOADING }
        handleLoadingMessages(loadingMessages)
    }

    private fun handleLoadingMessages(messages: List<Message>) {
        val loadingMessages = messages.filter { it.status == MessageStatus.LOADING }
        loadingMessages.forEach { message ->
            tryToExecute(
                execute = { chatRepository.sendMessage(message) },
                onSuccess = { onSendMessageSuccess(message.toUi(state.value.chat.requesterId)) },
                onError = { onSendMessageError(message.toUi(state.value.chat.requesterId)) },
            )
        }
    }

    private fun onLoadChatHistoryError(throwable: Throwable) {
        showSnackBar(
            SnackBarData(
                title = UiText.StringRes(Res.string.error),
                message = UiText.StringRes(Res.string.error_cant_get_messages)
            )
        )
    }

    private fun subscribeToNewMessages(chatId: Uuid) {
        tryToCollect(
            collect = { chatRepository.subscribeToMessages(chatId) },
            onCollect = ::onSubscribeToNewMessagesSuccess,
            onError = ::onSubscribeToNewMessagesError,
        )
    }

    private fun onSubscribeToNewMessagesSuccess(newMessage: Message?) {
        newMessage?.toUi(state.value.chat.requesterId)?.let { incomingUi ->
            updateStateWithNewMessage(incomingUi)
        }
    }

    private fun onSubscribeToNewMessagesError(throwable: Throwable) {
        showSnackBar(
            snackBarData = SnackBarData(
                title = UiText.StringRes(Res.string.error),
                message = UiText.StringRes(Res.string.error_cant_subscribe_to_new_messages),
            )
        )
    }

    private fun observeReadMessages() {
        tryToCollect(
            collect = { chatRepository.observeReadMessages() },
            onCollect = ::onObserveReadMessagesSuccess
        )
    }

    private fun onObserveReadMessagesSuccess(readerId: String?) {
        readerId?.let { readerId ->
            updateState {
                val updatedMessages = it.uiMessages.toMutableList().map { message ->
                    if (message.senderId.toString() != readerId && message.status == MessageStatusUiState.SENT) {
                        println("${message.text} :    ${message.senderId} != $readerId")
                        message.copy(status = MessageStatusUiState.READ)
                    } else {
                        message
                    }
                }
                it.copy(
                    uiMessages = updatedMessages,
                    chatListItems = buildListItems(updatedMessages)
                )
            }
        }
    }

    private fun updateStateWithNewMessage(newMessage: MessageUiState) {
        updateState { s ->
            val merged =
                s.uiMessages.toMutableList().apply { add(0, newMessage) }.distinctBy { it.id }
                    .sortedByDescending { it.sendTime }
            s.copy(uiMessages = merged, chatListItems = buildListItems(merged))
        }
    }

    private fun buildListItems(uiMessages: List<MessageUiState>): List<ChatListItem> {
        val marked = uiMessages.sortedByDescending { it.sendTime }.markLastInSeries()
        return marked.withDateSeparators()
    }

    override fun onCleared() {
        super.onCleared()
        println("Disconnected")
        tryToExecute(
            coroutineScope = CoroutineScope(Dispatchers.IO), // Required to avoid cancellation
            execute = { chatRepository.disconnect() }
        )
    }

    override fun onCameraClicked() {
        tryToExecute(
            execute = { permissionsController.providePermission(permission = Permission.CAMERA) },
            onSuccess = { onCameraPermissionGranted() },
        )
    }

    private fun onCameraPermissionGranted() {
        updateState { it.copy(isCameraOpen = true) }
    }

    override fun onCameraDismissed() {
        updateState { it.copy(isCameraOpen = false) }
    }
}
