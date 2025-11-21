@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.LocalDateTime
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.chat_deleted_successfully
import mena.core_chat_presentation.generated.resources.could_not_delete_chat
import mena.core_chat_presentation.generated.resources.error
import mena.core_chat_presentation.generated.resources.error_cant_get_messages
import mena.core_chat_presentation.generated.resources.error_cant_subscribe_to_new_messages
import mena.core_chat_presentation.generated.resources.error_failed_to_download_image
import mena.core_chat_presentation.generated.resources.error_failed_to_process_audio
import mena.core_chat_presentation.generated.resources.error_get_user_info
import mena.core_chat_presentation.generated.resources.error_invalid_recording
import mena.core_chat_presentation.generated.resources.error_recording_failed
import mena.core_chat_presentation.generated.resources.image_saved_successfully
import mena.core_chat_presentation.generated.resources.no_internet
import mena.core_chat_presentation.generated.resources.no_internet_connected
import mena.core_chat_presentation.generated.resources.permission_denied_title
import mena.core_chat_presentation.generated.resources.something_went_wrong
import mena.core_chat_presentation.generated.resources.success
import net.thechance.mena.core_chat.domain.entity.AudioData
import net.thechance.mena.core_chat.domain.entity.ImageData
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageReaction
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.entity.User
import net.thechance.mena.core_chat.domain.event.DeleteChatEvent
import net.thechance.mena.core_chat.domain.event.MarkMessageAsReadEvent
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.AudioRecordRepository
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.MessageRepository
import net.thechance.mena.core_chat.domain.repository.UserRepository
import net.thechance.mena.core_chat.domain.service.ImageDownloaderService
import net.thechance.mena.core_chat.presentation.components.snackBarHost.SnackBarData
import net.thechance.mena.core_chat.presentation.shared.BaseViewModel
import net.thechance.mena.core_chat.presentation.utils.AudioPlayer
import net.thechance.mena.core_chat.presentation.utils.Paginator
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.core_chat.presentation.utils.convertAudioFileToByteArray
import net.thechance.mena.core_chat.presentation.utils.encodeToByteArrayWithCompressionToMaxSize
import net.thechance.mena.core_chat.presentation.utils.now
import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import net.thechance.mena.wallet.domain.repository.TransactionRepository
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
    private val audioRecordRepository: AudioRecordRepository,
    private val userRepository: UserRepository,
    private val imageDownloaderService: ImageDownloaderService,
    val permissionsController: PermissionsController,
    private val audioPlayer: AudioPlayer,
    private val transactionRepository: TransactionRepository,
    chatArgs: ChatArgs,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<ChatScreenState, ChatScreenEffect>(ChatScreenState(), dispatcher),
    ChatInteractionListener {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    private val messages = _messages.asStateFlow()
    private val messagesMutex = Mutex()
    private val waveformCache = mutableMapOf<Uuid, List<Float>>()

    private var hasResentPendingMessages = false

    private val chatHistoryPaginator by lazy {
        Paginator(
            initialKey = INITIAL_PAGE,
            onLoadUpdated = { },
            onRequest = ::getChatHistory,
            getNextKey = { currentPage, _ -> currentPage + 1 },
            onSuccess = { result, _ -> handleChatHistorySuccess(result) },
            endReached = { _, result -> result.isLastPage }
        )
    }

    private var firstUnReadByMeMessageTime: LocalDateTime? = null

    init {
        val chatId = Uuid.parse(chatArgs.chatId)
        getUserInfo()
        updateState { state -> state.copy(chatId = chatId, chatName = chatArgs.chatName) }
        getChat(chatId)
        onMessagesScrolled()
        subscribeToNewMessages(chatId)
        subscribeToPendingMessages(chatId)
        observeReadMessages()
        observeDeleteChat()
        observeConnectionStatus(chatId)
        observeMessageReactions()
        startUiDerivation()
    }

    private fun setFirstUnReadByMeMessageTime(messages: List<Message>) {
        if (messages.isEmpty()) return
        for (message in (messages.sortedByDescending { it.sendAt })) {
            when {
                message.isMine -> break
                message.isMine.not() && message.status == MessageStatus.READ -> break
                message.isMine.not() && message.status == MessageStatus.SENT -> firstUnReadByMeMessageTime = message.sendAt
            }
        }
        if (firstUnReadByMeMessageTime == null) firstUnReadByMeMessageTime = LocalDateTime.now()
    }

    private fun startUiDerivation() {
        viewModelScope.launch(dispatcher) {
            messages
                .collectLatest { messageList ->
                    updateState {
                        it.copy(chatListItems = messageList.toChatItems())
                    }
                }
        }
    }

    private fun List<Message>.toChatItems(): List<ChatListItem> {
        if (firstUnReadByMeMessageTime == null) setFirstUnReadByMeMessageTime(this)
        return sortedByDescending { it.sendAt }
            .map { it.toUi() }
            .map { if (it is AudioMessageUiState) it.useCacheWaveform() else it }
            .markIsLastMessages()
            .addDateSeparators()
            .groupImages(firstUnReadByMeMessageTime ?: LocalDateTime.now())
    }

    private fun AudioMessageUiState.useCacheWaveform(): AudioMessageUiState {
        return copy(waveformData = waveformCache.getOrPut(messageDetails.id) { waveformData })
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

    private fun getChat(chatId: Uuid) {

        tryToExecute(
            execute = { chatRepository.getChatById(chatId) },
            onSuccess = { chat ->
        updateState { state ->
            state.copy(
                chatId = chat.id,
                chatName = chat.name,
                chatAvatarUrl = chat.imageUrl.orEmpty(),
                chatRequesterId = chat.requesterId,
                receiverId = chat.receiverId
            )
        }
},
            onError = { onGetChatError() }
        )
    }

    private fun observeConnectionStatus(chatId: Uuid) {
        tryToCollect(
            collect = { messageRepository.observeConnectionStatus(chatId) },
            onCollect = { },
            onError = {
                showSnackBar(Res.string.error, Res.string.error_cant_get_messages, true)
            }
        )
    }

    private fun onGetChatError() {
        viewModelScope.launch {
            delay(100)
            showSnackBar(
                titleStringResource = Res.string.error,
                messageStringResource = Res.string.error_cant_get_messages,
                isError = true
            )
            emitEffect(ChatScreenEffect.NavigateBack)
        }
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

        imageByteArrays.forEach { imageByteArray ->
            sendMessage(
                ImageMessageUiState(
                    imageDate = ImageData.ImageByteArray(byteArray = imageByteArray),
                    messageDetails = MessageDetailsUiState(
                        chatId = chatId,
                        senderId = senderId,
                    )
                )
            )
        }
    }


    override fun onInputMessageChanged(value: String) {
        updateState { state -> state.copy(inputMessage = value) }
    }

    override fun onSendTextMessageClicked() {
        val text = state.value.inputMessage.trim()
        val chatId = state.value.chatId
        val senderId = state.value.chatRequesterId

        if (chatId == null || senderId == null || text.isEmpty()) return

        sendMessage(
            TextMessageUiState(
                text = text,
                messageDetails = MessageDetailsUiState(
                    senderId = senderId,
                    chatId = chatId
                )
            )
        )
    }

    private fun sendMessage(message: MessageUiState) {
        updateState { state -> state.copy(inputMessage = "") }

        tryToExecute(
            execute = { messageRepository.sendMessage(message.toEntity()) },
        )
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
            execute = { messageRepository.deleteMessageById(failedMessage.messageDetails.id) },
            onSuccess = { onDeleteFailedMessageSuccess(failedMessage) }
        )
    }

    private suspend fun onDeleteFailedMessageSuccess(failedMessage: MessageUiState) {
        safeUpdateMessages { messages -> messages.filter { it.id != failedMessage.messageDetails.id } }
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
        tryToExecute(
            execute = {
                safeUpdateMessages { messages ->
                    messages.map {
                        if (it.id == message.messageDetails.id)
                            it.copy(status = MessageStatus.LOADING)
                        else
                            it
                    }}
            },
            onSuccess = { sendMessage(message) }
        )
    }

    override fun onResendMessageDialogDismissed() {
        updateState { it.copy(isResendMessageDialogVisible = false) }
    }

    private fun subscribeToNewMessages(chatId: Uuid) {
        tryToCollect(
            collect = { messageRepository.observeMessagesForChatOrAll(chatId) },
            onCollect = ::onCollectNewMessage,
            onError = { onCollectNewMessageFailed() },
        )
    }

    private suspend fun onCollectNewMessage(message: Message) {
        safeUpdateMessages { current ->
            current.toMutableList().apply { add(0, message) }
                .distinctBy { it.id }
        }

        messageRepository.markMessagesOfChatAsRead(message.chatId)
    }

    private fun onCollectNewMessageFailed() {
        showSnackBar(
            Res.string.error,
            Res.string.error_cant_subscribe_to_new_messages,
            true
        )
    }

    private fun subscribeToPendingMessages(chatId: Uuid) {
        tryToCollect(
            collect = { messageRepository.observePendingMessagesByChatId(chatId) },
            onCollect = ::onCollectPendingMessages
        )
    }

    private suspend fun onCollectPendingMessages(messages: List<Message>) {
        val pendingMessages = messages
        safeUpdateMessages { current ->
            current
                .filter { it.status != MessageStatus.LOADING }
                .toMutableList()
                .apply { addAll(pendingMessages) }
                .distinctBy { it.id }
        }

        if (!hasResentPendingMessages) {
            hasResentPendingMessages = true
            pendingMessages
                .filter { it.status == MessageStatus.LOADING }
                .forEach { sendMessage(it.toUi()) }
        }

        emitEffect(ChatScreenEffect.ScrollToBottom)
    }

    private suspend fun getChatHistory(page: Int): PagedData<Message> {
        val chatId = state.value.chatId ?: return PagedData(
            data = emptyList(),
            totalItems = 0,
            isLastPage = false
        )
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
        }
        messageRepository.markMessagesOfChatAsRead(state.value.chatId ?: return)

    }

    private fun observeDeleteChat() {
        tryToCollect(
            collect = { messageRepository.observeDeleteChat() },
            onCollect = ::onCollectDeleteChatEvent
        )
    }

    private fun onCollectDeleteChatEvent(deleteChatEvent: DeleteChatEvent) {
        onDeleteChatSuccess()
        emitEffect(ChatScreenEffect.NavigateBack)
    }

    private fun observeReadMessages() {
        tryToCollect(
            collect = { messageRepository.observeReadMessages() },
            onCollect = ::onCollectReadMessagesEvent
        )
    }

    private suspend fun onCollectReadMessagesEvent(markMessageAsReadEvent: MarkMessageAsReadEvent) {
        safeUpdateMessages { messages ->
            messages.map { message ->
                if (message.senderId != markMessageAsReadEvent.readByUserId && message.status == MessageStatus.SENT)
                    message.copy(status = MessageStatus.READ)
                else message

            }
        }
    }

    override fun onMessageImageClicked(
        messages: List<ImageMessageUiState>,
        initialImageIndex: Int
    ) {
        updateState {
            it.copy(
                isImagePagerVisible = true,
                selectedImageMessages = messages,
                currentImageIndexForPreview = initialImageIndex,
                isAttachmentsOverlayVisible = false
            )
        }
    }

    override fun onChatActionsMenuClicked() {
        updateState {
            it.copy(
                isChatActionsDialogVisible = true,
                isAttachmentsOverlayVisible = false
            )
        }
    }

    override fun onChatActionsMenuDialogDismissed() {
        updateState {
            it.copy(
                isChatActionsDialogVisible = false,
                isConfirmDeleteChatDialogVisible = false
            )
        }
    }

    override fun onConfirmDeleteChatDialogDismissed() {
        updateState { it.copy(isConfirmDeleteChatDialogVisible = false) }
    }

    override fun onDeleteChatClicked() {
        updateState {
            it.copy(
                isChatActionsDialogVisible = false,
                isConfirmDeleteChatDialogVisible = true
            )
        }
    }

    override fun onConfirmDeleteChatClicked() {
        tryToExecute(
            execute = { chatRepository.deleteChatById(state.value.chatId!!) },
            onSuccess = { onDeleteChatSuccess() },
            onError = { onDeleteChatFailure() }
        )
        updateState { it.copy(isConfirmDeleteChatDialogVisible = false) }
    }

    private fun onDeleteChatSuccess() {
        showSnackBar(
            titleStringResource = Res.string.success,
            messageStringResource = Res.string.chat_deleted_successfully,
            isError = false
        )
    }

    private fun onDeleteChatFailure() {
        showSnackBar(
            titleStringResource = Res.string.error,
            messageStringResource = Res.string.could_not_delete_chat,
            isError = true
        )
    }

    override fun onMessageLongClicked(message: MessageUiState) {
        if (message is ImageMessageUiState && message.imageDate !is ImageData.ImageUrl) return

        updateState {
            it.copy(
                isReactionDialogVisible = true,
                messageToReactTo = message
            )
        }
    }

    override fun onReactionDialogDismissed() {
        updateState {
            it.copy(
                isReactionDialogVisible = false,
                messageToReactTo = null
            )
        }
    }


    override fun onReactionSelected(messageId: Uuid, reaction: String) {
        val currentUserId = state.value.chatRequesterId ?: return
        val message = _messages.value.firstOrNull { it.id == messageId } ?: return
        val hasSameReaction =
            message.reactions.any { it.userId == currentUserId && it.emoji == reaction }

        if (hasSameReaction) {
            tryToExecute(
                execute = { messageRepository.removeMessageReaction(messageId, reaction) },
            )
        } else {
            tryToExecute(
                execute = { messageRepository.addMessageReaction(messageId, reaction) },
            )
        }
    }

    private fun observeMessageReactions() {
        tryToCollect(
            collect = { messageRepository.observeMessageReactions() },
            onCollect = ::onCollectAddReaction
        )

        tryToCollect(
            collect = { messageRepository.observeRemovedMessageReactions() },
            onCollect = ::onCollectRemoveReaction
        )
    }


    private suspend fun onCollectAddReaction(reaction: MessageReaction) {
        safeUpdateMessages { messages ->
            messages.map { message ->
                if (message.id == reaction.messageId) {
                    val filtered = message.reactions.filter { it.userId != reaction.userId }
                        .toMutableList()
                    filtered.add(reaction)
                    message.copy(reactions = filtered)
                } else message
            }
        }

        if (
            state.value.selectedImageMessages.isNotEmpty()
            && state.value.selectedImageMessages.any { it.messageDetails.id == reaction.messageId }
        ) {
            updateState {
                it.copy(
                    selectedImageMessages =
                        it.selectedImageMessages.map { message ->
                            if (message.messageDetails.id == reaction.messageId) {
                                val updatedReactions = message.messageDetails.reactions
                                    .filter { it.userId != reaction.userId }
                                    .toMutableList()
                                    .apply { add(reaction) }
                                message.copy(messageDetails = message.messageDetails.copy(reactions = updatedReactions))
                            } else {
                                message
                            }
                        }
                )
            }
        }
    }

    private suspend fun onCollectRemoveReaction(reaction: MessageReaction) {
        safeUpdateMessages { messages ->
            messages.map { message ->
                if (message.id == reaction.messageId) {
                    val filtered = message.reactions.filterNot {
                        it.userId == reaction.userId && it.emoji == reaction.emoji
                    }
                    message.copy(reactions = filtered)
                } else message
            }
        }

        if (state.value.selectedImageMessages.isNotEmpty() && state.value.selectedImageMessages.any { it.messageDetails.id == reaction.messageId }) {
            updateState {
                it.copy(selectedImageMessages = it.selectedImageMessages.map {
                    if (it.messageDetails.id == reaction.messageId) it.copy(
                        messageDetails = it.messageDetails.copy(reactions = it.messageDetails.reactions.filterNot { it == reaction })
                    ) else it
                }
                )
            }
        }
    }


    override fun onMessageVoiceClicked(messageId: Uuid) {
        val audioMessageUiStateItem = state.value.chatListItems.find {
            it is AudioMessageUiState && it.messageDetails.id == messageId
        } as? AudioMessageUiState ?: return


        if (audioMessageUiStateItem.isPlaying) {
            audioPlayer.pause()
            updateVoiceMessageState(messageId, isPlaying = false)
            return
        }

        stopAnyPlayingVoiceMessage(messageId)

        val audioContent = audioMessageUiStateItem.data
        val audioUrl = (audioContent as? AudioData.AudioUrl) ?: return

        val needsLoading = audioMessageUiStateItem.duration <= 0

        updateVoiceMessageState(
            messageId,
            isPlaying = true,
            isLoading = needsLoading
        )

        tryToExecute(
            execute = { audioRecordRepository.getAudioFilePath(audioUrl.url) },
            onSuccess = { filePath ->
                if (needsLoading) {
                    val duration = audioPlayer.getDuration(filePath)
                    audioPlayer.play(filePath)
                    updateVoiceMessageState(messageId, duration = duration, isLoading = false)
                    startProgressTracking(messageId)
                } else {
                    audioPlayer.play(filePath)
                    startProgressTracking(messageId)
                }
            },
            onError = {
                updateVoiceMessageState(
                    messageId,
                    isPlaying = false,
                    isLoading = false
                )
            }
        )
    }

    private fun startProgressTracking(messageId: Uuid) {
        viewModelScope.launch {
            val totalDuration = audioPlayer.getDurationOfCurrentAudio()
            var lastPositionMilliSeconds = audioPlayer.getCurrentPosition()
            while (true) {
                delay(100)

                val audioMessageUiStateItem = state.value.chatListItems.find {
                    it is AudioMessageUiState && it.messageDetails.id == messageId
                } as? AudioMessageUiState

                if (audioMessageUiStateItem == null || audioMessageUiStateItem.isPlaying.not()) break

                val currentPosition = audioPlayer.getCurrentPosition()

                val completed =
                    isPlaybackCompleted(
                        totalDuration = totalDuration,
                        currentPosition = currentPosition,
                        lastPosition = lastPositionMilliSeconds
                    )

                if (completed) {
                    updateVoiceMessageState(messageId, isPlaying = false, progress = 0f)
                    break
                }

                lastPositionMilliSeconds = currentPosition

                val progress = if (totalDuration > 0) {
                    currentPosition.toFloat() / totalDuration.toFloat()
                } else 0f

                updateVoiceMessageState(messageId, progress = progress)
            }
        }
    }

    private fun isPlaybackCompleted(
        totalDuration: Long,
        currentPosition: Long,
        lastPosition: Long
    ): Boolean {
        if (totalDuration <= 0) return false

        val remainingMs = totalDuration - currentPosition
        val isNearEnd = currentPosition >= totalDuration * AUDIO_END_THRESHOLD_RATIO
        val isStagnant = currentPosition == lastPosition && isNearEnd

        return remainingMs <= 0 || isStagnant
    }

    private fun stopAnyPlayingVoiceMessage(excludeMessageId: Uuid) {
        state.value.chatListItems.forEach { item ->
            if (item is AudioMessageUiState && item.messageDetails.id != excludeMessageId && (item.isPlaying || item.progress > 0f)) {
                updateVoiceMessageState(item.messageDetails.id, isPlaying = false, progress = 0f)
            }
        }
        audioPlayer.pause()
    }

    private fun updateVoiceMessageState(
        messageId: Uuid,
        isPlaying: Boolean? = null,
        isLoading: Boolean? = null,
        progress: Float? = null,
        duration: Long? = null
    ) {
        updateState { currentState ->
            val updatedItems = currentState.chatListItems.map { chatItem ->
                if (chatItem is AudioMessageUiState && chatItem.messageDetails.id == messageId) {
                    chatItem.copy(
                        isPlaying = isPlaying ?: chatItem.isPlaying,
                        isLoading = isLoading ?: chatItem.isLoading,
                        progress = progress ?: chatItem.progress,
                        duration = duration ?: chatItem.duration
                    )
                } else {
                    chatItem
                }
            }
            currentState.copy(chatListItems = updatedItems)
        }
    }

    override fun onRecordClicked() {
        if (audioRecordRepository.isRecording()) {
            stopAndPlayRecording()
        } else {
            requestRecordPermissionAndStart()
        }
    }

    private fun stopAndPlayRecording() {
        val filePath = audioRecordRepository.stopRecording()
        updateState { it.copy(isRecordingVoice = false) }
        audioPlayer.play(filePath)
    }

    private fun requestRecordPermissionAndStart() {
        tryToExecute(
            execute = { permissionsController.providePermission(Permission.RECORD_AUDIO) },
            onSuccess = { startRecording() },
            onError = { onRecordPermissionDenied() }
        )
    }

    private fun startRecording() {
        tryToExecute(
            execute = { audioRecordRepository.startRecording() },
            onSuccess = { updateState { it.copy(isRecordingVoice = true) } },
            onError = { onRecordingStartFailed() }
        )
    }

    private fun onRecordPermissionDenied() {
        showSnackBar(Res.string.error, Res.string.permission_denied_title, true)
    }

    private fun onRecordingStartFailed() {
        showSnackBar(Res.string.error, Res.string.error_recording_failed, true)
        updateState { it.copy(isRecordingVoice = false) }
    }

    override fun onCancelRecordClicked() {
        audioRecordRepository.stopRecording()
        updateState { it.copy(isRecordingVoice = false) }
    }

    override fun onSendRecordClicked() {
        val filePath = audioRecordRepository.stopRecording()
        updateState { it.copy(isRecordingVoice = false) }

        if (!validateRecordingData(filePath)) {
            return
        }

        processAndSendAudioMessage(filePath)
    }

    private fun validateRecordingData(filePath: String): Boolean {
        val chatId = state.value.chatId
        val senderId = state.value.chatRequesterId

        if (chatId == null || senderId == null) {
            showSnackBar(Res.string.error, Res.string.error_invalid_recording, true)
            return false
        }

        if (filePath.isEmpty()) {
            showSnackBar(Res.string.error, Res.string.error_invalid_recording, true)
            return false
        }

        return true
    }

    private fun processAndSendAudioMessage(filePath: String) {
        val audioByteArray = convertAudioFileToByteArray(filePath)

        val chatId = state.value.chatId
        val senderId = state.value.chatRequesterId
        if (chatId == null || senderId == null) return

        if (audioByteArray.isEmpty()) {
            showSnackBar(Res.string.error, Res.string.error_failed_to_process_audio, true)
            return
        }

        val audioDuration = audioPlayer.getDuration(filePath)

        sendMessage(
            AudioMessageUiState(
                data = AudioData.AudioByteArray(byteArray = audioByteArray),
                messageDetails = MessageDetailsUiState(
                    senderId = senderId,
                    chatId = chatId
                ),
                isPlaying = false,
                isLoading = false,
                progress = 0f,
                duration = audioDuration,
                waveformData = generateWaveformData()
            )
        )
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
    override fun onSurahClicked(surahId: Int) {
        emitEffect(ChatScreenEffect.NavigateToSurah(surahId))
    }

    override fun onAyahClicked(surahId: Int, ayahNumber: Int) {
        emitEffect(ChatScreenEffect.NavigateToAyah(surahId, ayahNumber))
    }

    override fun onCameraClicked() {
        tryToExecute(
            execute = { permissionsController.providePermission(permission = Permission.CAMERA) },
            onSuccess = { onCameraPermissionGranted() },
            onError = { showSnackBar(Res.string.error, Res.string.permission_denied_title, true) }
        )
    }

    override fun onSendMoneyClicked() {
        updateState {
            it.copy(
                amountToTransfer = "",
                isAttachmentsOverlayVisible = false,
                isSendMoneyDialogVisible = true
            )
        }
    }

    override fun onValueChanged(value: String) {
        updateState { it.copy(amountToTransfer = value) }
    }

    override fun onSendClicked() {
        tryToExecute(
            onStart = { updateState { it.copy(isLoadingSendMoneyButton = true) } },
            execute = ::sendMoney,
            onSuccess = { transactionId -> onSendMoneySuccess(transactionId) },
            onError = ::onSendMoneyFailed,
        )
    }

    private suspend fun sendMoney(): Uuid {
        val receiverId = state.value.receiverId
        val amount = state.value.amountToTransfer.toDouble()
        return getTransactionId(receiverId!!, amount)
    }

    private fun onSendMoneySuccess(transactionId: Uuid) {
        updateState {
            it.copy(
                isSendMoneyDialogVisible = false,
                isLoadingSendMoneyButton = false
            )
        }
        emitEffect(
            ChatScreenEffect.NavigateToConfirmPayment(
                state.value.amountToTransfer,
                transactionId
            )
        )
    }

    override fun onDismissSendMoneyDialog() {
        updateState {
            it.copy(
                isSendMoneyDialogVisible = false
            )
        }
    }

    private fun onSendMoneyFailed(e: Throwable) {
        updateState { it.copy(isLoadingSendMoneyButton = false) }
        when (e) {
            is NoInternetException -> {
                showSnackBar(
                    titleStringResource = Res.string.no_internet,
                    messageStringResource = Res.string.no_internet_connected,
                    isError = true
                )
            }

            else -> showSnackBar(
                titleStringResource = Res.string.error,
                messageStringResource = Res.string.something_went_wrong,
                isError = true
            )
        }
    }


    private suspend fun getTransactionId(receiverId: Uuid, amount: Double): Uuid {
        return transactionRepository.addPendingTransaction(
            receiverId = receiverId,
            amount = amount,
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

    private suspend fun handleChatHistorySuccess(result: PagedData<Message>) {
        onGetChatHistorySuccess(result)
    }

    override fun onStopAudioPlayback() {
        audioPlayer.pause()
        if (audioRecordRepository.isRecording()) audioRecordRepository.stopRecording()

        updateState { currentState ->
            val updatedItems = currentState.chatListItems.map { item ->
                if (item is AudioMessageUiState && item.isPlaying) item.copy(isPlaying = false) else item
            }
            currentState.copy(
                chatListItems = updatedItems,
                isRecordingVoice = false
            )
        }
    }
    override fun onLinkClicked(url: String) {
        emitEffect(ChatScreenEffect.OpenUrl(url))
    }

    companion object {
        const val PAGE_SIZE = 40
        const val INITIAL_PAGE = 0
        private const val AUDIO_END_THRESHOLD_RATIO = 0.90
    }
}