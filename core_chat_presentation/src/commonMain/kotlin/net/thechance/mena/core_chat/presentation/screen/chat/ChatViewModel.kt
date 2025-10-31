@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
import net.thechance.mena.core_chat.domain.entity.ImageData
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.entity.User
import net.thechance.mena.core_chat.domain.event.MarkMessageAsReadEvent
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.MessageRepository
import net.thechance.mena.core_chat.domain.repository.UserRepository
import net.thechance.mena.core_chat.domain.service.ImageDownloaderService
import net.thechance.mena.core_chat.presentation.components.snackBarHost.SnackBarData
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel
import net.thechance.mena.core_chat.presentation.utils.Paginator
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.core_chat.presentation.utils.encodeToByteArrayWithCompressionToMaxSize
import net.thechance.mena.core_chat.presentation.utils.getUuidOrNull
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
    private val imageDownloaderService: ImageDownloaderService,
    chatArgs: ChatArgs,
    private val permissionsController: PermissionsController,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ChatScreenState, ChatScreenEffect>(ChatScreenState(), dispatcher),
    ChatInteractionListener {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    private val messages = _messages.asStateFlow()
    private val messagesMutex = Mutex()

    private var hasResentPendingMessages = false

    private val chatHistoryPaginator by lazy {
        Paginator(
            initialKey = INITIAL_PAGE,
            onLoadUpdated = { },
            onRequest = ::getChatHistory,
            getNextKey = { currentPage, _ -> currentPage + 1 },
            onError = { showSnackBar(Res.string.error, Res.string.error_cant_get_messages, true) },
            onSuccess = { result, newPage -> onGetChatHistorySuccess(result) },
            endReached = { _, result -> result.isLastPage }
        )
    }

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
        startUiDerivation()
    }

    private fun startUiDerivation() {
        viewModelScope.launch(dispatcher) {
            messages
                .map { list -> list.map { it.toUi() } }
                .collectLatest { uiList ->
                    updateState { state ->
                        state.copy(
                            chatListItems = uiList
                                .sortedByDescending { it.sendTime }
                                .buildListItems()
                        )
                    }
                }
        }
    }

    private fun getUserInfo() {
        tryToExecute(
            execute = { userRepository.getUserInfo() },
            onSuccess = ::onGetUserDataSuccess,
            onError = ::onGetUserDataError
        )
    }

    private fun onGetUserDataSuccess(user: User) {
        updateState { state ->
            state.copy(
                userData = UserData(
                    firstName = user.firstName,
                    lastName = user.lastName,
                    imageUrl = user.imageUrl.orEmpty()
                )
            )
        }
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

        onMessagesScrolled()
        subscribeToNewMessages(chat.id)
        subscribeToPendingMessages(chat.id)
        observeReadMessages()
    }

    private fun onGetChatError() {
        showSnackBar(
            titleStringResource = Res.string.error,
            messageStringResource = Res.string.error_cant_get_messages,
            isError = true
        )
        emitEffect(ChatScreenEffect.NavigateBack)
    }

    override fun onBackClicked() {
        emitEffect(ChatScreenEffect.NavigateBack)
    }

    override fun onSendImageClicked(imageByteArrays: List<ByteArray>) {
        val chatId = state.value.chatId
        val senderId = state.value.chatRequesterId

        if (chatId == null || senderId == null || imageByteArrays.isEmpty()) {
            return
        }

        imageByteArrays.forEach { image ->
            val content = MessageContent.Image(ImageData.ImageByteArray(image))
            sendImageMessage(chatId, senderId, content)
        }
    }

    private fun sendImageMessage(chatId: Uuid, senderId: Uuid, content: MessageContent) {
        val message = MessageUiState(
            chatId = chatId,
            senderId = senderId,
            content = content
        )

        tryToExecute(
            execute = { messageRepository.sendMessage(message.toEntity()) },
            onSuccess = { onSendMessageSuccess(message) }
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
        updateState { state -> state.copy(inputMessage = "") }

        tryToExecute(
            execute = { messageRepository.sendMessage(message.toEntity()) },
            onSuccess = { onSendMessageSuccess(message) }
        )
    }

    private suspend fun onSendMessageSuccess(message: MessageUiState) {
        safeUpdateMessages { messages -> messages.filter{ it.id != message.id && it.sendAt != message.sendTime } }
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
            execute = { messageRepository.deleteMessage(failedMessage.toEntity()) },
            onSuccess = { onDeleteFailedMessageSuccess(failedMessage) }
        )
    }

    private suspend fun onDeleteFailedMessageSuccess(failedMessage: MessageUiState) {
        safeUpdateMessages { messages -> messages.filter{ it.id != failedMessage.id } }
        updateState { state ->
            state.copy(
                failedMessageToReSend = null,
                isResendMessageDialogVisible = false
            )
        }
    }

    override fun onResendMessageClicked() {
        val message =
            state.value.failedMessageToReSend ?: return

        updateState { state ->
            state.copy(
                isResendMessageDialogVisible = false,
                failedMessageToReSend = null
            )
        }

        sendMessage(message)
    }

    override fun onResendMessageDialogDismissed() {
        updateState { it.copy(isResendMessageDialogVisible = false) }
    }

    private fun subscribeToNewMessages(chatId: Uuid) {
        tryToCollect(
            collect = { messageRepository.observeMessagesForChatOrAll(chatId) },
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
        safeUpdateMessages { current ->
            current.toMutableList().apply { add(0, message) }
                .distinctBy { it.id }
                .sortedByDescending { it.sendAt }
        }

        messageRepository.markMessagesOfChatAsRead(message.chatId)
    }

    private fun subscribeToPendingMessages(chatId: Uuid) {
        tryToCollect(
            collect = { messageRepository.observePendingMessagesByChatId(chatId) },
            onCollect = ::onCollectPendingMessages
        )
    }

    private suspend fun onCollectPendingMessages(messages: List<Message>?) {
        val pendingMessages = messages ?: emptyList()
        safeUpdateMessages { current ->
            current
                .filter { it.status != MessageStatus.LOADING }
                .toMutableList()
                .apply { addAll(pendingMessages) }
                .distinctBy { it.id }
                .sortedByDescending { it.sendAt }
        }

        if (!hasResentPendingMessages) {
            hasResentPendingMessages = true
            pendingMessages
                .filter { it.status == MessageStatus.LOADING }
                .forEach { sendMessage(it.toUi()) }
        }
    }

    private suspend fun getChatHistory(page: Int): PagedData<Message> {
        val chatId = state.value.chatId ?: return PagedData(emptyList(), 0, false)
        return messageRepository.loadMessages(
            chatId = chatId,
            page = page,
            pageSize = PAGE_SIZE
        )
    }

    private suspend fun onGetChatHistorySuccess(messages: PagedData<Message>) {
        safeUpdateMessages { current ->
            current.toMutableList().apply { addAll(messages.data) }
                .distinctBy { it.id }
                .sortedByDescending { it.sendAt }
        }
        messageRepository.markMessagesOfChatAsRead(state.value.chatId ?: return)

    }

    private fun observeReadMessages() {
        tryToCollect(
            collect = { messageRepository.observeReadMessages() },
            onCollect = ::onCollectReadMessagesEvent
        )
    }

    private suspend fun onCollectReadMessagesEvent(markMessageAsReadEvent: MarkMessageAsReadEvent?) {
        if (markMessageAsReadEvent == null) return
        safeUpdateMessages { messages ->
            messages.map { message ->
                if (message.senderId != markMessageAsReadEvent.readByUserId && message.status == MessageStatus.SENT)
                    message.copy(status = MessageStatus.READ)
                else message

            }
        }

    }



    override fun onMessageImageClicked(messages: List<MessageUiState>, initialImageIndex: Int) {
        updateState {
            it.copy(
                isImagePagerVisible = true,
                selectedImageMessages = messages,
                currentImageIndexForPreview = initialImageIndex
            )
        }
    }

    override fun onDownloadImageClicked(url: String) {
        tryToExecute(
            execute = { imageDownloaderService.downloadImageToGallery(url) },
            onSuccess = ::onDownloadImageSuccess,
            onError = { onDownloadImageError() }
        )
    }

    private fun onDownloadImageSuccess(isSuccess: Boolean) {
        if (isSuccess) {
            showSnackBar(Res.string.success, Res.string.image_saved_successfully, isError = false)
        } else {
            onDownloadImageError()
        }
    }

    private fun onDownloadImageError() {
        showSnackBar(Res.string.error, Res.string.error_failed_to_download_image, true)
    }

    override fun onCloseImageViewClicked() {
        updateState {
            it.copy(
                isImagePagerVisible = false,
                selectedImageMessages = emptyList(),
                currentImageIndexForPreview = 0
            )
        }
    }

    private fun showSnackBar(
        titleStringResource: StringResource,
        messageStringResource: StringResource,
        isError: Boolean = false
    ) {
        emitEffect(
            ChatScreenEffect.ShowSnackBar(
                SnackBarData(
                    title = UiText.StringRes(titleStringResource),
                    message = UiText.StringRes(messageStringResource),
                    isError = isError
                )
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

    override fun onCameraResult(image: ImageBitmap?) {
        updateState { it.copy(isCameraOpen = false) }

        if (image == null) return

        tryToExecute(
            execute = { image.encodeToByteArrayWithCompressionToMaxSize() },
            onSuccess = { byteArray -> onSendImageClicked(listOf(byteArray)) }
        )
    }

    private fun onCameraPermissionGranted() {
        updateState { it.copy(isCameraOpen = true, isAttachmentsOverlayVisible = false) }
    }

    override fun onCloseAttachmentClicked() {
        updateState { it.copy(isAttachmentsOverlayVisible = false) }
    }

    override fun onMessagesScrolled() {
        viewModelScope.launch {
            chatHistoryPaginator.loadNextItems()
        }
    }

    private suspend fun safeUpdateMessages(block: (List<Message>) -> List<Message>) {
        messagesMutex.withLock {
            _messages.update(block)
        }
    }
    companion object {
        const val PAGE_SIZE = 40
        const val INITIAL_PAGE = 0
    }
}
