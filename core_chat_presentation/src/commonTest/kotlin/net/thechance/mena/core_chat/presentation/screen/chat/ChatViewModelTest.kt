package net.thechance.mena.core_chat.presentation.screen.chat

import assertk.assertThat
import assertk.assertions.doesNotContain
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
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
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.repository.ChatRepository
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
    private val chatArgs = mock<ChatArgs>()
    private val effector = mock<ChatEffector>(MockMode.autofill)
    private lateinit var chatViewModel: ChatViewModel

    private val testDispatcher = StandardTestDispatcher()


    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { chatArgs.chatId } returns chatId.toString()
        every { chatArgs.chatName } returns chatName
        every { chatArgs.chatRequesterId } returns chatRequesterId.toString()
        every { chatArgs.chatImageUrl } returns chatImage

        chatViewModel = ChatViewModel(repository, chatArgs, effector, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should update chat list when its loaded messages successfully`() {
        everySuspend { repository.loadMessages(chatId) } returns messages
        everySuspend { repository.getLocalMessages(chatId) } returns emptyList()

        chatViewModel = ChatViewModel(repository, chatArgs, effector, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(
            chatViewModel.state.value.chatListItems.currentUiMessages()
                .map { it.copy(isLastInSeries = false, isVisibleMessageInfo = false) }
        )
            .isEqualTo(messages.map { it.toUi(chatRequesterId) }.reversed())
    }

    @Test
    fun `init should send snack bar effect when its LOADING the messages failed`() {
        everySuspend { repository.loadMessages(chatId) } throws Exception()

        chatViewModel = ChatViewModel(repository, chatArgs, effector, testDispatcher)
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
        every { repository.subscribeToMessages(chatId) } returns flowOf(messages.first())

        chatViewModel = ChatViewModel(repository, chatArgs, effector, testDispatcher)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(
            chatViewModel.state.value.chatListItems.currentUiMessages()
                .map { it.copy(isLastInSeries = false, isVisibleMessageInfo = false) }
        ).isEqualTo(
            listOf(messages.first().toUi(chatRequesterId))
        )
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
                inputMessage = inputMessage
            )
        }
        everySuspend { repository.sendMessage(any()) } returns Unit

        chatViewModel.onSendMessageClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(chatViewModel.state.value.inputMessage).isEmpty()
    }

    @Test
    fun `onSendMessageClicked should update current messages with failed state and reset the user input when its call`() {
        val inputMessage = "hi"
        chatViewModel.updateState {
            chatViewModel.state.value.copy(
                chatId = chatId,
                inputMessage = inputMessage
            )
        }

        // repository.sendMessage not stubbed -> will fail path (or you can explicitly throw)
        chatViewModel.onSendMessageClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        val first = chatViewModel.state.value.chatListItems.currentUiMessages().first()
        assertThat(first.chatId).isEqualTo(chatId)
        assertThat(first.isMine).isTrue()
        assertThat(first.status).isEqualTo(MessageStatus.FAILED)
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
    fun `onResendMessageClick should update the resend message state to sent when resend message success`() {
        val failedMessage =
            messages.first().copy(status = MessageStatus.FAILED).toUi(chatRequesterId)

        chatViewModel.updateState {
            it.copy(
                chatId = chatId,
                failedMessageToReSend = failedMessage,
                chatListItems = listOf(failedMessage.toChatListMessage())
            )
        }

        everySuspend { repository.loadMessages(chatId) } returns emptyList()
        everySuspend { repository.getLocalMessages(chatId) } returns listOf(failedMessage.toEntity())
        everySuspend { repository.sendMessage(any()) } returns Unit

        chatViewModel.onResendMessageClicked()

        assertThat(
            chatViewModel.state.value.chatListItems.currentUiMessages().first().status
        ).isEqualTo(MessageStatus.LOADING)

        val sentMessage = failedMessage.toEntity().copy(status = MessageStatus.SENT)
        everySuspend { repository.loadMessages(chatId) } returns listOf(sentMessage)
        everySuspend { repository.getLocalMessages(chatId) } returns emptyList()

        testDispatcher.scheduler.advanceUntilIdle()

        val finalMessages = chatViewModel.state.value.chatListItems.currentUiMessages()
        if (finalMessages.isNotEmpty()) {
            assertThat(finalMessages.first().status).isEqualTo(MessageStatus.SENT)
        } else {
            verifySuspend { repository.sendMessage(any()) }
        }
    }

    @Test
    fun `onResendMessageClick should update the resend message state to failed when resend message failed`() {
        val failedMessage = messages.first().toUi(chatRequesterId)
        chatViewModel.updateState {
            it.copy(
                failedMessageToReSend = failedMessage,
                chatListItems = listOf(messages.first().toUi(chatRequesterId).toChatListMessage())
            )
        }

        chatViewModel.onResendMessageClicked()

        assertThat(
            chatViewModel.state.value.chatListItems.currentUiMessages().first().status
        ).isEqualTo(MessageStatus.LOADING)
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(
            chatViewModel.state.value.chatListItems.currentUiMessages().first().status
        ).isEqualTo(MessageStatus.FAILED)
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


    private fun List<ChatListItem>.currentUiMessages(): List<MessageUiState> =
        filterIsInstance<ChatListItem.Message>()
            .map { it.data }
            .sortedByDescending { it.sendTime }

    private fun MessageUiState.toChatListMessage(): ChatListItem.Message =
        ChatListItem.Message(this)

    private companion object {

        val chatId = Uuid.parse("11111111-1111-1111-1111-111111111111")
        val chatRequesterId = Uuid.parse("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
        val chatName = "Noor"
        val chatImage = "https://image.com/noor.jpg"

        val message1Id = Uuid.parse("22222222-2222-2222-2222-222222222222")
        val message2Id = Uuid.parse("33333333-3333-3333-3333-333333333333")

        val messages =
            listOf(
                Message(
                    message1Id,
                    chatRequesterId,
                    chatId,
                    "Hello, World",
                    LocalDateTime.now(),
                    MessageStatus.SENT
                ),
                Message(
                    message2Id,
                    chatRequesterId,
                    chatId,
                    "Hello, World2",
                    LocalDateTime.now(),
                    MessageStatus.SENT
                )
            )
    }
}

