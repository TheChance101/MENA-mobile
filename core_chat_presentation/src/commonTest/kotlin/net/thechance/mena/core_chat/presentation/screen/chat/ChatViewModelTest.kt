package net.thechance.mena.core_chat.presentation.screen.chat

import assertk.assertThat
import assertk.assertions.doesNotContain
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.error
import mena.core_chat_presentation.generated.resources.error_cant_get_messages
import mena.core_chat_presentation.generated.resources.error_failed_to_download_image
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.entity.User
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.UserRepository
import net.thechance.mena.core_chat.presentation.components.SnackBarData
import net.thechance.mena.core_chat.presentation.navigation.ChatEffector
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.core_chat.presentation.utils.now
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private val repository = mock<ChatRepository>()
    private val userRepository = mock<UserRepository>()
    private val chatArgs = mock<ChatArgs>()
    private val permissionsController = mock<PermissionsController>()
    private val effector = mock<ChatEffector>(MockMode.autofill)
    private lateinit var chatViewModel: ChatViewModel

    private val testDispatcher = StandardTestDispatcher()


    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { chatArgs.chatId } returns chatId.toString()
        every { chatArgs.chatName } returns chatName

        everySuspend { repository.getChatById(chatId) } returns mockChat
        everySuspend { repository.loadMessages(chatId) } returns emptyList()
        everySuspend { repository.getLocalMessages(chatId) } returns flowOf(emptyList())
        every { repository.getMessages(chatId) } returns flowOf()
        every { repository.observeReadMessages() } returns flowOf()

        chatViewModel = ChatViewModel(repository, userRepository, chatArgs, effector, permissionsController, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should update chat list when its loaded messages successfully`() {
        everySuspend { repository.getChatById(chatId) } returns mockChat
        everySuspend { repository.loadMessages(chatId) } returns messages
        everySuspend { repository.getLocalMessages(chatId) } returns flowOf(emptyList())
        every { repository.getMessages(chatId) } returns flowOf()
        every { repository.observeReadMessages() } returns flowOf()

        chatViewModel = ChatViewModel(repository, userRepository, chatArgs, effector, permissionsController, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(
            chatViewModel.state.value.chatListItems.currentUiMessages()
                .map { it.copy(isLastInSeries = false, isVisibleMessageInfo = false) }
        )
            .isEqualTo(messages.map { it.toUi(chatRequesterId) }.reversed())
    }

    @Test
    fun `init should send snack bar effect when its LOADING the messages failed`() {
        everySuspend { repository.getChatById(chatId) } returns mockChat
        everySuspend { repository.loadMessages(chatId) } throws Exception()
        everySuspend { repository.getLocalMessages(chatId) } returns flowOf(emptyList())
        every { repository.getMessages(chatId) } returns flowOf()
        every { repository.observeReadMessages() } returns flowOf()

        chatViewModel = ChatViewModel(repository, userRepository, chatArgs, effector, permissionsController, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend {
            effector.showSnackBar(
                SnackBarData(
                    title = UiText.StringRes(Res.string.error),
                    message = UiText.StringRes(Res.string.error_cant_get_messages)
                )
            )
        }
    }

    @Test
    fun `init should update uiMessage and chatListItems when receive new message`() {
        everySuspend { repository.getChatById(chatId) } returns mockChat
        everySuspend { repository.loadMessages(chatId) } returns emptyList()
        everySuspend { repository.getLocalMessages(chatId) } returns flowOf(emptyList())
        every { repository.getMessages(chatId) } returns flowOf(messages.first())
        every { repository.observeReadMessages() } returns flowOf()

        chatViewModel = ChatViewModel(repository, userRepository, chatArgs, effector, permissionsController, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(
            chatViewModel.state.value.chatListItems.currentUiMessages()
                .map { it.copy(isLastInSeries = false, isVisibleMessageInfo = false) }
        ).isEqualTo(
            listOf(messages.first().toUi(chatRequesterId))
        )
    }
    @Test
    fun `init should update user data when receive user data from repository`() {

        everySuspend {userRepository.getUserInfo() } returns user
        chatViewModel = ChatViewModel(repository, userRepository, chatArgs, effector, permissionsController, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(chatViewModel.state.value.userData.firstName).isEqualTo(user.firstName)
        assertThat(chatViewModel.state.value.userData.lastName).isEqualTo(user.lastName)
        assertThat(chatViewModel.state.value.userData.imageUrl).isEqualTo(user.imageUrl)
    }

    @Test
    fun `onBackClicked should send pop back stack effect when its call`() {
        chatViewModel.onBackClicked()
        testDispatcher.scheduler.advanceUntilIdle()
        verifySuspend { effector.popBackStack() }
    }

    @Test
    fun `onInputMessageChanged should update the inputMessage value with provided value when its call`() {
        val inputMessage = "Hi Noor"
        chatViewModel.onInputMessageChanged(inputMessage)
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(chatViewModel.state.value.inputMessage).isEqualTo(inputMessage)
    }

    @Test
    fun `onResendMessageDialogDismissed should set isResendMessageDialogVisible to false when its call`() {
        chatViewModel.onResendMessageDialogDismissed()
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(chatViewModel.state.value.isResendMessageDialogVisible).isFalse()
    }

    @Test
    fun `onResendMessageDialogDismissed should set isChatActionsDialogVisible to false when its called`() {
        chatViewModel.onResendMessageDialogDismissed()
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(chatViewModel.state.value.isResendMessageDialogVisible).isFalse()
    }

    @Test
    fun `onSendMessageClicked should update current messages with sent state and reset the user input when its successfully sent `() {
        val inputMessage = "hi"
        chatViewModel.updateState {
            chatViewModel.state.value.copy(
                chatId = chatId,
                chatRequesterId = chatRequesterId,
                inputMessage = inputMessage
            )
        }
        everySuspend { repository.sendMessage(any()) } returns Unit

        chatViewModel.onSendMessageClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(chatViewModel.state.value.inputMessage).isEmpty()
    }

    @Test
    fun `onSendMessageClicked should reset the user input when its call`() {
        val inputMessage = "hi"
        chatViewModel.updateState {
            chatViewModel.state.value.copy(
                chatId = chatId,
                chatRequesterId = chatRequesterId,
                inputMessage = inputMessage
            )
        }

        everySuspend { repository.sendMessage(any()) } throws Exception("Send failed")

        chatViewModel.onSendMessageClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(chatViewModel.state.value.inputMessage).isEmpty()
    }

    @Test
    fun `onFailedMessageClicked should update the failedMessageToResend to the failedMessage when its call`() {
        val failedMessage = messages.first().toUi(chatRequesterId)
        chatViewModel.onFailedMessageClicked(failedMessage)
        assertThat(chatViewModel.state.value.failedMessageToReSend).isEqualTo(failedMessage)
    }

    @Test
    fun `onFailedMessageClicked should update the isResendMessageDialogVisible to true when its call`() {
        val failedMessage = messages.first().toUi(chatRequesterId)
        chatViewModel.onFailedMessageClicked(failedMessage)
        assertThat(chatViewModel.state.value.isResendMessageDialogVisible).isEqualTo(true)
    }

    @Test
    fun `onDeleteFailedMessageClick should delete the clicked failed message when its call`() {
        val msgUi = messages.first().toUi(chatRequesterId)
        // set failed message and chatListItems (instead of uiMessages)
        chatViewModel.updateState {
            it.copy(
                failedMessageToReSend = msgUi,
                chatListItems = listOf(msgUi.toChatListMessage())
            )
        }

        everySuspend { repository.deleteMessage(any()) } returns Unit

        chatViewModel.onDeleteFailedMessageClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(chatViewModel.state.value.chatListItems.currentUiMessages()).doesNotContain(msgUi)
    }

    @Test
    fun `onResendMessageClick should remove the failed message when resend message success`() {
        val failedMessage =
            messages.first().copy(status = MessageStatus.FAILED).toUi(chatRequesterId)

        chatViewModel.updateState {
            it.copy(
                chatId = chatId,
                chatRequesterId = chatRequesterId,
                failedMessageToReSend = failedMessage,
                chatListItems = listOf(failedMessage.toChatListMessage())
            )
        }

        everySuspend { repository.sendMessage(any()) } returns Unit

        chatViewModel.onResendMessageClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        val finalMessages = chatViewModel.state.value.chatListItems.currentUiMessages()
        assertThat(finalMessages.isEmpty()).isTrue()
        verifySuspend { repository.sendMessage(any()) }
    }

    @Test
    fun `onMessageClicked should toggle showMessageInfo when message with id exists`() {
        val message = messages.first()
        val messageUiState = message.toUi(chatRequesterId)
        val chatListItem = ChatListItem.Message(messageUiState)
        chatViewModel.updateState { it.copy(chatListItems = listOf(chatListItem)) }

        chatViewModel.onMessageClicked(message.id)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedItem = chatViewModel.state.value.chatListItems.first() as ChatListItem.Message
        assertThat(updatedItem.data.isVisibleMessageInfo).isTrue()

        chatViewModel.onMessageClicked(message.id)
        testDispatcher.scheduler.advanceUntilIdle()

        val toggledBack = chatViewModel.state.value.chatListItems.first() as ChatListItem.Message
        assertThat(toggledBack.data.isVisibleMessageInfo).isFalse()
    }

    @Test
    fun `onMessageClicked should not change items when message id does not exist`() {
        val message = messages.first()
        val messageUiState = message.toUi(chatRequesterId)
        val chatListItem = ChatListItem.Message(messageUiState)
        chatViewModel.updateState { it.copy(chatListItems = listOf(chatListItem)) }

        val nonExistentId = Uuid.parse("99999999-9999-9999-9999-999999999999")
        chatViewModel.onMessageClicked(nonExistentId)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = chatViewModel.state.value.chatListItems.first() as ChatListItem.Message
        assertThat(result.data.isVisibleMessageInfo).isEqualTo(messageUiState.isVisibleMessageInfo)
    }

    @Test
    fun `onMessageImageClicked should update state to show image pager with correct message and index`() {
        val message = messages.first().toUi(chatRequesterId)
        val index = 2

        chatViewModel.onMessageImageClicked(message, index)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(chatViewModel.state.value.isImagePagerVisible).isTrue()
        assertThat(chatViewModel.state.value.selectedMessage).isEqualTo(message)
        assertThat(chatViewModel.state.value.currentImageIndexForPreview).isEqualTo(index)
    }

    @Test
    fun `onDownloadImageClicked should call repository and show success snackbar on success`() {
        everySuspend { repository.downloadImage(imageUrl) } returns Unit

        chatViewModel.onDownloadImageClicked(imageUrl)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend { repository.downloadImage(imageUrl) }
        verifySuspend { effector.showSnackBar(any()) }
    }

    @Test
    fun `onDownloadImageClicked should show error snackbar on failure`() {

        everySuspend { repository.downloadImage(imageUrl) } throws Exception()

        chatViewModel.onDownloadImageClicked(imageUrl)
        testDispatcher.scheduler.advanceUntilIdle()

        verifySuspend {
            effector.showSnackBar(
                SnackBarData(
                    title = UiText.StringRes(Res.string.error),
                    message = UiText.StringRes(Res.string.error_failed_to_download_image),
                    isError = true
                )
            )
        }
    }

    @Test
    fun `onCloseImageViewClicked should reset image pager state`() {

        chatViewModel.onCloseImageViewClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(chatViewModel.state.value.isImagePagerVisible).isFalse()
        assertThat(chatViewModel.state.value.selectedMessage).isEqualTo(null)
        assertThat(chatViewModel.state.value.currentImageIndexForPreview).isEqualTo(0)
    }


    @Test
    fun `onCameraClicked should start getting camera permission when user click it`(){
        chatViewModel.onCameraClicked()
        testDispatcher.scheduler.advanceUntilIdle()
        verifySuspend {permissionsController.providePermission(permission = Permission.CAMERA)}
    }

    @Test
    fun `onCameraClicked should open camera when permission is granted`(){
        everySuspend {  permissionsController.providePermission(permission = Permission.CAMERA)} returns Unit
        chatViewModel.onCameraClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(chatViewModel.state.value.isCameraOpen).isTrue()
    }

    @Test
    fun `onCameraClicked should not open camera when camera permission is declined by user`(){
        everySuspend {  permissionsController.providePermission(permission = Permission.CAMERA)} throws DeniedException(Permission.CAMERA)
        chatViewModel.onCameraClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(chatViewModel.state.value.isCameraOpen).isFalse()
    }

    @Test
    fun`onCameraClosed should close camera when clicked`(){
        chatViewModel.onCameraClosed()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(chatViewModel.state.value.isCameraOpen).isFalse()
    }

    private fun List<ChatListItem>.currentUiMessages(): List<MessageUiState> =
        filterIsInstance<ChatListItem.Message>()
            .map { it.data }
            .sortedByDescending { it.sendTime }

    private fun MessageUiState.toChatListMessage(): ChatListItem.Message =
        ChatListItem.Message(this)

    private companion object {

        val user: User = User(
            firstName = "ali",
            lastName = "nawar",
            imageUrl = ""
        )
        val chatId = Uuid.parse("11111111-1111-1111-1111-111111111111")
        val chatRequesterId = Uuid.parse("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
        val chatName = "Noor"
        val chatImage = "https://image.com/noor.jpg"

        val mockChat = Chat(
            id = chatId,
            name = chatName,
            imageUrl = chatImage,
            requesterId = chatRequesterId
        )

        val message1Id = Uuid.parse("22222222-2222-2222-2222-222222222222")
        val message2Id = Uuid.parse("33333333-3333-3333-3333-333333333333")

        const val imageUrl = "https://test.com/image.jpg"

        val messages =
            listOf(
                Message(
                    message1Id,
                    chatRequesterId,
                    chatId,
                    LocalDateTime.now(),
                    MessageStatus.SENT,
                    MessageContent.Text("Hello, World"),
                    true
                ),
                Message(
                    message2Id,
                    chatRequesterId,
                    chatId,
                    LocalDateTime.now(),
                    MessageStatus.SENT,
                    MessageContent.Text("Hello, World2"),
                    false
                )
            )
    }
}

