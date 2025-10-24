package net.thechance.mena.core_chat.presentation.screen.chat

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.doesNotContain
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNull
import assertk.assertions.isTrue
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
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
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDateTime
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.error
import mena.core_chat_presentation.generated.resources.error_failed_to_download_image
import mena.core_chat_presentation.generated.resources.image_saved_successfully
import mena.core_chat_presentation.generated.resources.success
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.entity.User
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.MessageRepository
import net.thechance.mena.core_chat.domain.repository.UserRepository
import net.thechance.mena.core_chat.domain.service.ImageDownloaderService
import net.thechance.mena.core_chat.presentation.components.snackBarHost.SnackBarData
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.core_chat.presentation.utils.now
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private val chatRepository = mock<ChatRepository>()
    private val messageRepository = mock<MessageRepository>()
    private val userRepository = mock<UserRepository>()
    private val chatArgs = mock<ChatArgs>()
    private val imageDownloaderService = mock<ImageDownloaderService>()
    private val permissionsController = mock<PermissionsController>()
    private lateinit var viewModel: ChatViewModel

    private val testDispatcher = StandardTestDispatcher()


    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { chatArgs.chatId } returns chatId.toString()
        every { chatArgs.chatName } returns chatName

        everySuspend { chatRepository.getChatById(chatId) } returns chat
        everySuspend {
            messageRepository.loadMessages(chatId, any(), any())
        } returns PagedData(emptyList(), 0, true)
        everySuspend { messageRepository.observePendingMessagesByChatId(chatId) } returns flowOf(
            emptyList()
        )
        every { messageRepository.observeMessagesForChatOrAll(chatId) } returns flowOf()
        every { messageRepository.observeReadMessages() } returns flowOf()
        everySuspend { messageRepository.markMessagesOfChatAsRead(any()) } returns Unit

        viewModel = createViewModel()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should update chat list when its loaded messages successfully`() = runTest {
        everySuspend { messageRepository.observePendingMessagesByChatId(chatId) } returns flowOf(
            messages
        )
        every { messageRepository.observeMessagesForChatOrAll(chatId) } returns flowOf()
        every { messageRepository.observeReadMessages() } returns flowOf()
        everySuspend {
            messageRepository.loadMessages(chatId, 0, 40)
        } returns PagedData(messages, messages.size, false)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertThat(
            viewModel.state.value.chatListItems.currentUiMessages()
                .map { it.copy(isLastInSeries = false, isVisibleMessageInfo = false) }
        ).isEqualTo(messages.map { it.toUi(chatRequesterId) }.reversed())
    }

    @Test
    fun `init should update uiMessage and chatListItems when receive new message`() = runTest {
        everySuspend { chatRepository.getChatById(chatId) } returns chat
        every { messageRepository.observePendingMessagesByChatId(chatId) } returns flowOf(messages)
        every { messageRepository.observeReadMessages() } returns flowOf()
        everySuspend {
            messageRepository.loadMessages(chatId, any(), any())
        } returns PagedData(emptyList(), 80, false)
        every { messageRepository.observeMessagesForChatOrAll(chatId) } returns flowOf(messages.first())

        advanceUntilIdle()

        assertThat(
            viewModel.state.value.chatListItems.currentUiMessages()
                .map { it.copy(isLastInSeries = false, isVisibleMessageInfo = false) }
        ).isEqualTo(messages.map{ it.toUi(chatRequesterId) }.reversed())
    }

    @Test
    fun `init should update user data when receive user data from repository`() = runTest {
        everySuspend { userRepository.getUserInfo() } returns user

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.state.value.userData.firstName).isEqualTo(user.firstName)
        assertThat(viewModel.state.value.userData.lastName).isEqualTo(user.lastName)
        assertThat(viewModel.state.value.userData.imageUrl).isEqualTo(user.imageUrl)
    }

    @Test
    fun `onBackClicked should emit NavigateBack effect`() = runTest {
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onBackClicked()
            advanceUntilIdle()

            assertEquals(ChatScreenEffect.NavigateBack, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onInputMessageChanged should update the inputMessage value with provided value when its call`() =
        runTest {
            advanceUntilIdle()
            val inputMessage = "Hi Noor"

            viewModel.onInputMessageChanged(inputMessage)
            advanceUntilIdle()

            assertThat(viewModel.state.value.inputMessage).isEqualTo(inputMessage)
        }

    @Test
    fun `onResendMessageDialogDismissed should set isResendMessageDialogVisible to false when its call`() =
        runTest {
            advanceUntilIdle()

            viewModel.onResendMessageDialogDismissed()

            assertThat(viewModel.state.value.isResendMessageDialogVisible).isFalse()
        }

    @Test
    fun `onSendMessageClicked should update current messages with sent state and reset the user input when its successfully sent `() =
        runTest {
            everySuspend { messageRepository.sendMessage(any()) } returns Unit
            advanceUntilIdle()
            val inputMessage = "hi"
            viewModel.onInputMessageChanged(inputMessage)

            viewModel.onSendMessageClicked()
            advanceUntilIdle()

            assertThat(viewModel.state.value.inputMessage).isEmpty()
        }

    @Test
    fun `onSendMessageClicked should reset the user input when its call`() = runTest {
        advanceUntilIdle()
        val inputMessage = "hi"
        viewModel.onInputMessageChanged(inputMessage)

        everySuspend { messageRepository.sendMessage(any()) } throws Exception("Send failed")

        viewModel.onSendMessageClicked()
        advanceUntilIdle()

        assertThat(viewModel.state.value.inputMessage).isEmpty()
    }

    @Test
    fun `onFailedMessageClicked should update the failedMessageToResend to the failedMessage when its call`() =
        runTest {
            val failedMessage = messages.first().toUi(chatRequesterId)

            viewModel.onFailedMessageClicked(failedMessage)

            assertThat(viewModel.state.value.failedMessageToReSend).isEqualTo(failedMessage)
        }

    @Test
    fun `onFailedMessageClicked should update the isResendMessageDialogVisible to true when its call`() =
        runTest {
            advanceUntilIdle()
            val failedMessage = messages.first().toUi(chatRequesterId)

            viewModel.onFailedMessageClicked(failedMessage)

            assertThat(viewModel.state.value.isResendMessageDialogVisible).isEqualTo(true)
        }

    @Test
    fun `onDeleteFailedMessageClick should delete the clicked failed message when its call`() =
        runTest {
            everySuspend { messageRepository.deleteMessage(any()) } returns Unit
            advanceUntilIdle()
            val msgUi = messages.first().copy(status = MessageStatus.FAILED).toUi(chatRequesterId)
            viewModel.onFailedMessageClicked(msgUi)

            viewModel.onDeleteFailedMessageClicked()
            advanceUntilIdle()

            assertThat(viewModel.state.value.chatListItems.currentUiMessages()).doesNotContain(msgUi)
        }

    @Test
    fun `onResendMessageClick should remove the failed message when resend message success`() =
        runTest {
            everySuspend { messageRepository.sendMessage(any()) } returns Unit
            advanceUntilIdle()
            val failedMessage =
                messages.first().copy(status = MessageStatus.FAILED).toUi(chatRequesterId)
            viewModel.onFailedMessageClicked(failedMessage)

            viewModel.onResendMessageClicked()
            advanceUntilIdle()

            val finalMessages = viewModel.state.value.chatListItems.currentUiMessages()
            assertThat(finalMessages.isEmpty()).isTrue()
            verifySuspend { messageRepository.sendMessage(any()) }
        }

    @Test
    fun `onMessageImageClicked should update state to show image pager with correct message and index`() =
        runTest {
            val message = messages.first().toUi(chatRequesterId)
            val index = 2
            advanceUntilIdle()

            viewModel.onMessageImageClicked(message, index)
            advanceUntilIdle()

            assertThat(viewModel.state.value.isImagePagerVisible).isTrue()
            assertThat(viewModel.state.value.selectedMessage).isEqualTo(message)
            assertThat(viewModel.state.value.currentImageIndexForPreview).isEqualTo(index)
        }

    @Test
    fun `onDownloadImageClicked should call imageDownloaderService`() = runTest {
        everySuspend { imageDownloaderService.downloadImageToGallery(imageUrl) } returns true
        advanceUntilIdle()

        viewModel.onDownloadImageClicked(imageUrl)
        advanceUntilIdle()

        verifySuspend { viewModel.onDownloadImageClicked(imageUrl) }
    }

    @Test
    fun `onDownloadImageClicked should emit error snackBar effect when downloadImageToGallery fails and return false`() = runTest {
        everySuspend { imageDownloaderService.downloadImageToGallery(imageUrl) } returns false
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onDownloadImageClicked(imageUrl)
            advanceUntilIdle()

            assertEquals(
                ChatScreenEffect.ShowSnackBar(
                    SnackBarData(
                        title = UiText.StringRes(Res.string.error),
                        message = UiText.StringRes(Res.string.error_failed_to_download_image),
                        isError = true
                    )
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDownloadImageClicked should emit success snackBar effect on success`() = runTest {
        everySuspend { imageDownloaderService.downloadImageToGallery(imageUrl) } returns true
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onDownloadImageClicked(imageUrl)
            advanceUntilIdle()

            assertEquals(
                ChatScreenEffect.ShowSnackBar(
                    SnackBarData(
                        title = UiText.StringRes(Res.string.success),
                        message = UiText.StringRes(Res.string.image_saved_successfully),
                        isError = false
                    )
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onDownloadImageClicked should emit error snackBar effect when downloadImageToGallery throws exception`() =
        runTest {
            everySuspend { imageDownloaderService.downloadImageToGallery(imageUrl) } throws Exception()
            advanceUntilIdle()

            viewModel.effect.test {
                viewModel.onDownloadImageClicked(imageUrl)
                advanceUntilIdle()

                assertEquals(
                    ChatScreenEffect.ShowSnackBar(
                        SnackBarData(
                            title = UiText.StringRes(Res.string.error),
                            message = UiText.StringRes(Res.string.error_failed_to_download_image),
                            isError = true
                        )
                    ), awaitItem()
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onCloseImageViewClicked should reset image pager state`() = runTest {
        advanceUntilIdle()

        viewModel.onCloseImageViewClicked()
        advanceUntilIdle()

        assertThat(viewModel.state.value.isImagePagerVisible).isFalse()
        assertThat(viewModel.state.value.selectedMessage).isNull()
        assertThat(viewModel.state.value.currentImageIndexForPreview).isEqualTo(0)
    }

    @Test
    fun `onCameraClicked should check for camera permission when called`() = runTest {
        viewModel.onCameraClicked()
        advanceUntilIdle()

        verifySuspend { permissionsController.providePermission(permission = Permission.CAMERA) }
    }

    @Test
    fun `onCameraClicked should open camera when permission is granted`() = runTest {
        everySuspend { permissionsController.providePermission(permission = Permission.CAMERA) } returns Unit
        advanceUntilIdle()

        viewModel.onCameraClicked()
        advanceUntilIdle()

        assertThat(viewModel.state.value.isCameraOpen).isTrue()
    }

    @Test
    fun `onCameraClicked should not open camera when camera permission is denied`() = runTest {
        everySuspend { permissionsController.providePermission(permission = Permission.CAMERA) } throws DeniedException(
            Permission.CAMERA
        )
        advanceUntilIdle()

        viewModel.onCameraClicked()
        advanceUntilIdle()

        assertThat(viewModel.state.value.isCameraOpen).isFalse()
    }

    @Test
    fun `onCameraResult should close camera when called`() = runTest {
        advanceUntilIdle()

        viewModel.onCameraResult(null)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isCameraOpen).isFalse()
    }

    @Test
    fun `onSendImageClicked should send image message`() = runTest {
        val imageBytes = listOf(byteArrayOf(1, 2, 3))
        everySuspend { messageRepository.sendMessage(any()) } returns Unit

        viewModel.onSendImageClicked(imageBytes)
        advanceUntilIdle()

        verifySuspend { messageRepository.sendMessage(any()) }
    }

    private fun List<ChatListItem>.currentUiMessages(): List<MessageUiState> =
        filterIsInstance<ChatListItem.Message>()
            .map { it.data }
            .sortedByDescending { it.sendTime }


    private fun createViewModel(): ChatViewModel {
        return ChatViewModel(
            chatRepository,
            messageRepository,
            userRepository,
            imageDownloaderService,
            chatArgs,
            permissionsController,
            testDispatcher
        )
    }

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

        val chat = Chat(
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

