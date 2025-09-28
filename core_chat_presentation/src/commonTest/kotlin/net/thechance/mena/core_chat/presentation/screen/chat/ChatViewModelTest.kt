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
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.presentation.components.SnackBarData
import net.thechance.mena.core_chat.presentation.navigation.ChatEffector
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.core_chat.presentation.utils.now
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.test.Test

@OptIn(ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private val repository = mock<ChatRepository>()
    val chatId = Uuid.parse("11111111-1111-1111-1111-111111111111")
    val currentUserId = Uuid.parse("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
    private val chatArgs = mock<ChatArgs>()
    private val effector = mock<ChatEffector>(MockMode.autofill)
    private lateinit var chatViewModel: ChatViewModel

    private val testDispatcher = StandardTestDispatcher()


    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { chatArgs.chatId } returns chatId.toString()
        chatViewModel = ChatViewModel(repository, chatArgs, effector, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should update chat ui state when its loaded the chat successfully`() {
        val chat = Chat(chatId, "https://image.com", "Noor")
        everySuspend { repository.getChatById(chatId) } returns chat

        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(chatViewModel.state.value.chat).isEqualTo(chat.toUi())
    }

    @Test
    fun `init should update chat list when its loaded messages successfully`() {
        everySuspend { repository.loadMessages(chatId) } returns messages

        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(chatViewModel.state.value.uiMessages)
            .isEqualTo(messages.map { it.toUi(currentUserId) }.reversed())
    }


    @Test
    fun `init should send snack bar effect when its loading the messages failed`() {
        everySuspend { repository.loadMessages(chatId) } throws Exception()

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
        every { repository.subscribeToMessages(chatId) } returns  flowOf(messages.first())

        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(chatViewModel.state.value.uiMessages).isEqualTo(listOf(messages.first().toUi(currentUserId)))
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
                chat = it.chat.copy(id = chatId),
                inputMessage = inputMessage
            )
        }
        everySuspend { repository.sendMessage(any()) } returns Unit

        chatViewModel.onSendMessageClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(chatViewModel.state.value.uiMessages.first().chatId).isEqualTo(chatId)
        assertThat(chatViewModel.state.value.uiMessages.first().senderId).isEqualTo(
            currentUserId
        )
        assertThat(chatViewModel.state.value.uiMessages.first().status).isEqualTo(
            MessageStatusUiState.SENT
        )
        assertThat(chatViewModel.state.value.inputMessage).isEmpty()
    }

    @Test
    fun `onSendMessageClicked should update current messages with failed state and reset the user input when its call`() {
        val inputMessage = "hi"
        chatViewModel.updateState {
            chatViewModel.state.value.copy(
                chat = it.chat.copy(id = chatId),
                inputMessage = inputMessage
            )
        }


        chatViewModel.onSendMessageClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(chatViewModel.state.value.uiMessages.first().chatId).isEqualTo(chatId)
        assertThat(chatViewModel.state.value.uiMessages.first().senderId).isEqualTo(
            currentUserId
        )
        assertThat(chatViewModel.state.value.uiMessages.first().status).isEqualTo(
            MessageStatusUiState.FAILED
        )
        assertThat(chatViewModel.state.value.inputMessage).isEmpty()
    }

    @Test
    fun `onFailedMessageClicked should update the failedMessageToResend to the failedMessage when its call`() {
        val failedMessage = messages.first().toUi(currentUserId)

        chatViewModel.onFailedMessageClicked(failedMessage)

        assertThat(chatViewModel.state.value.failedMessageToReSend).isEqualTo(failedMessage)
    }


    @Test
    fun `onFailedMessageClicked should update the isResendMessageDialogVisible to true when its call`() {
        val failedMessage = messages.first().toUi(currentUserId)

        chatViewModel.onFailedMessageClicked(failedMessage)

        assertThat(chatViewModel.state.value.isResendMessageDialogVisible).isEqualTo(true)
    }


    @Test
    fun `onDeleteFailedMessageClick should delete the clicked failed message when its call`() {
        chatViewModel.updateState { it.copy(
            failedMessageToReSend = messages.first().toUi(currentUserId),
            uiMessages = listOf(messages.first().toUi(currentUserId))
        ) }

        chatViewModel.onDeleteFailedMessageClicked()
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(chatViewModel.state.value.uiMessages).doesNotContain(messages.first().toUi(currentUserId))
    }

    @Test
    fun `onResendMessageClick should update the resend message state to sent when resend message success`() {
        val failedMessage = messages.first().toUi(currentUserId)
        chatViewModel.updateState { it.copy(
            failedMessageToReSend = failedMessage,
            uiMessages = listOf(messages.first().toUi(currentUserId))
        ) }
        everySuspend { repository.sendMessage(failedMessage.toEntity()) } returns Unit

        chatViewModel.onResendMessageClicked()

        assertThat(chatViewModel.state.value.uiMessages.first().status).isEqualTo(MessageStatusUiState.SENDING)
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(chatViewModel.state.value.uiMessages.first().status).isEqualTo(MessageStatusUiState.SENT)
    }

    @Test
    fun `onResendMessageClick should update the resend message state to failed when resend message failed`() {
        val failedMessage = messages.first().toUi(currentUserId)
        chatViewModel.updateState { it.copy(
            failedMessageToReSend = failedMessage,
            uiMessages = listOf(messages.first().toUi(currentUserId))
        ) }

        chatViewModel.onResendMessageClicked()

        assertThat(chatViewModel.state.value.uiMessages.first().status).isEqualTo(MessageStatusUiState.SENDING)
        testDispatcher.scheduler.advanceUntilIdle()
        assertThat(chatViewModel.state.value.uiMessages.first().status).isEqualTo(MessageStatusUiState.FAILED)
    }

    @Test
    fun `onMessageClicked should toggle showMessageInfo when message with id exists`() {
        val markedMessageUiState = MarkedMessageUiState(
            message = messages.first().toUi(currentUserId),
            isMarkedLastInSeries = false,
            showMessageInfo = false
        )
        val chatListItem = ChatListItem.Message(markedMessageUiState)
        chatViewModel.updateState { it.copy(chatListItems = listOf(chatListItem)) }

        chatViewModel.onMessageClicked(markedMessageUiState.message.id)
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedItem = chatViewModel.state.value.chatListItems.first() as ChatListItem.Message
        assertThat(updatedItem.data.showMessageInfo).isTrue()

        chatViewModel.onMessageClicked(markedMessageUiState.message.id)
        testDispatcher.scheduler.advanceUntilIdle()

        val toggledBack = chatViewModel.state.value.chatListItems.first() as ChatListItem.Message
        assertThat(toggledBack.data.showMessageInfo).isFalse()
    }

    @Test
    fun `onMessageClicked should not change items when message id does not exist`() {
        val markedMessageUiState = MarkedMessageUiState(
            message = messages.first().toUi(currentUserId),
            isMarkedLastInSeries = false,
            showMessageInfo = false
        )
        val chatListItem = ChatListItem.Message(markedMessageUiState)
        chatViewModel.updateState { it.copy(chatListItems = listOf(chatListItem)) }

        chatViewModel.onMessageClicked(currentUserId)
        testDispatcher.scheduler.advanceUntilIdle()

        val result = chatViewModel.state.value.chatListItems.first() as ChatListItem.Message
        assertThat(result).isEqualTo(chatListItem)
    }



    private val messages =
        listOf(
            Message(
                Uuid.random(),
                currentUserId,
                chatId,
                "Hello, World", LocalDateTime.now(),
                MessageStatus.SENT
            ),
            Message(
                Uuid.random(),
                currentUserId,
                chatId,
                "Hello, World2", LocalDateTime.now(),
                MessageStatus.SENT
            )
        )

}