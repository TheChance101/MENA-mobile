@file:OptIn(ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.doesNotContain
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
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
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode.Companion.exactly
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
import mena.core_chat_presentation.generated.resources.chat_deleted_successfully
import mena.core_chat_presentation.generated.resources.could_not_delete_chat
import mena.core_chat_presentation.generated.resources.error
import mena.core_chat_presentation.generated.resources.error_failed_to_download_image
import mena.core_chat_presentation.generated.resources.error_failed_to_process_audio
import mena.core_chat_presentation.generated.resources.error_invalid_recording
import mena.core_chat_presentation.generated.resources.error_recording_failed
import mena.core_chat_presentation.generated.resources.image_saved_successfully
import mena.core_chat_presentation.generated.resources.permission_denied_title
import mena.core_chat_presentation.generated.resources.something_went_wrong
import mena.core_chat_presentation.generated.resources.success
import net.thechance.mena.core_chat.domain.entity.AudioData
import net.thechance.mena.core_chat.domain.entity.Chat
import net.thechance.mena.core_chat.domain.entity.ImageData
import net.thechance.mena.core_chat.domain.entity.Message
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.entity.MessageReaction
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.domain.entity.User
import net.thechance.mena.core_chat.domain.model.PagedData
import net.thechance.mena.core_chat.domain.repository.AudioRecordRepository
import net.thechance.mena.core_chat.domain.repository.ChatRepository
import net.thechance.mena.core_chat.domain.repository.MessageRepository
import net.thechance.mena.core_chat.domain.repository.UserRepository
import net.thechance.mena.core_chat.domain.service.ImageDownloaderService
import net.thechance.mena.core_chat.presentation.components.snackBarHost.SnackBarData
import net.thechance.mena.core_chat.presentation.screen.chat.components.NoInternetConnection
import net.thechance.mena.core_chat.presentation.utils.AudioPlayer
import net.thechance.mena.core_chat.presentation.utils.UiText
import net.thechance.mena.core_chat.presentation.utils.now
import net.thechance.mena.faith.domain.exception.FaithException
import net.thechance.mena.wallet.domain.exceptions.NoInternetException
import net.thechance.mena.wallet.domain.repository.TransactionRepository
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
    private val audioRecordRepository = mock<AudioRecordRepository>()
    private lateinit var chatArgs: ChatArgs
    private val imageDownloaderService = mock<ImageDownloaderService>()
    private val permissionsController = mock<PermissionsController>()
    private val transactionRepository = mock<TransactionRepository>()
    private val audioPlayer = mock<AudioPlayer>()
    private lateinit var viewModel: ChatViewModel

    private val testDispatcher = StandardTestDispatcher()


    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { audioPlayer.getDurationOfCurrentAudio() } returns 1000L
        chatArgs = object : ChatArgs {
            override val chatId: String = Companion.chatId.toString()
            override val chatName: String = Companion.chatName
        }
        everySuspend { chatRepository.getChatById(chatId) } returns chat
        everySuspend {
            messageRepository.loadMessages(chatId, any(), any())
        } returns PagedData(emptyList(), 0, true)
        everySuspend { messageRepository.observePendingMessagesByChatId(chatId) } returns flowOf(
            emptyList()
        )
        every { messageRepository.observeMessagesForChatOrAll(chatId) } returns flowOf()
        every { messageRepository.observeReadMessages() } returns flowOf()
        every { messageRepository.observeDeleteChat() } returns flowOf()
        every { messageRepository.observeMessageReactions() } returns flowOf()
        every { messageRepository.observeRemovedMessageReactions() } returns flowOf()
        everySuspend { messageRepository.markMessagesOfChatAsRead(any()) } returns Unit
        everySuspend { transactionRepository.addPendingTransaction(receiverId, amount) }
        viewModel = createViewModel()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
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
    fun `init should update user data when receive user data with null imageUrl from repository`() = runTest {
        everySuspend { userRepository.getUserInfo() } returns user.copy(imageUrl = null)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertThat(viewModel.state.value.userData.firstName).isEqualTo(user.firstName)
        assertThat(viewModel.state.value.userData.lastName).isEqualTo(user.lastName)
        assertThat(viewModel.state.value.userData.imageUrl).isEmpty()
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

            viewModel.onSendTextMessageClicked()
            advanceUntilIdle()

            assertThat(viewModel.state.value.inputMessage).isEmpty()
        }

    @Test
    fun `onSendMessageClicked should reset the user input when its call`() = runTest {
        advanceUntilIdle()
        val inputMessage = "hi"
        viewModel.onInputMessageChanged(inputMessage)

        everySuspend { messageRepository.sendMessage(any()) } throws Exception("Send failed")

        viewModel.onSendTextMessageClicked()
        advanceUntilIdle()

        assertThat(viewModel.state.value.inputMessage).isEmpty()
    }

    @Test
    fun `onFailedMessageClicked should update the failedMessageToResend to the failedMessage when its call`() =
        runTest {
            val failedMessage = messages.first().toUi()

            viewModel.onFailedMessageClicked(failedMessage)

            assertThat(viewModel.state.value.failedMessageToReSend).isEqualTo(failedMessage)
        }

    @Test
    fun `onFailedMessageClicked should update the isResendMessageDialogVisible to true when its call`() =
        runTest {
            advanceUntilIdle()
            val failedMessage = messages.first().toUi()

            viewModel.onFailedMessageClicked(failedMessage)

            assertThat(viewModel.state.value.isResendMessageDialogVisible).isEqualTo(true)
        }

    @Test
    fun `onDeleteFailedMessageClick should delete the clicked failed message when its call`() =
        runTest {
            everySuspend { messageRepository.deleteMessageById(any()) } returns Unit
            advanceUntilIdle()
            val msg = messages.first().copy(status = MessageStatus.FAILED)
            viewModel.onFailedMessageClicked(msg.toUi())

            viewModel.onDeleteFailedMessageClicked()
            advanceUntilIdle()

            assertThat(viewModel.state.value.chatListItems.fromChatItems()).doesNotContain(msg)
        }

    @Test
    fun `onResendMessageClick should remove the failed message when resend message success`() =
        runTest {
            everySuspend { messageRepository.sendMessage(any()) } returns Unit
            advanceUntilIdle()
            val failedMessage =
                messages.first().copy(status = MessageStatus.FAILED).toUi()
            viewModel.onFailedMessageClicked(failedMessage)

            viewModel.onResendMessageClicked()
            advanceUntilIdle()

            val finalMessages = viewModel.state.value.chatListItems.fromChatItems()
            assertThat(finalMessages.isEmpty()).isTrue()
            verifySuspend { messageRepository.sendMessage(any()) }
        }

    @Test
    fun `onMessageImageClicked should update state to show image pager with correct message and index`() =
        runTest {
            val imageMessage = Message(
                id = message1Id,
                senderId = chatRequesterId,
                chatId = chatId,
                sendAt = LocalDateTime.now(),
                status = MessageStatus.SENT,
                content = MessageContent.Image(ImageData.ImageUrl(imageUrl)),
                isMine = true
            ).toUi() as ImageMessageUiState
            val messages = listOf(imageMessage, imageMessage, imageMessage)
            val index = 2
            advanceUntilIdle()

            viewModel.onMessageImageClicked(messages, index)
            advanceUntilIdle()

            assertThat(viewModel.state.value.isImagePagerVisible).isTrue()
            assertThat(viewModel.state.value.selectedImageMessages).isEqualTo(messages)
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
    fun `onDownloadImageClicked should emit error snackBar effect when downloadImageToGallery fails and return false`() =
        runTest {
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
        assertThat(viewModel.state.value.selectedImageMessages).isEmpty()
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
    fun `onGalleryClicked should close attachments bottom sheet when called`() = runTest {
        advanceUntilIdle()

        viewModel.onGalleryClicked()
        advanceUntilIdle()

        assertThat(viewModel.state.value.isAttachmentsOverlayVisible).isFalse()
    }

    @Test
    fun `onCloseAttachmentClicked should close attachments bottom sheet when called`() = runTest {
        advanceUntilIdle()

        viewModel.onCloseAttachmentClicked()
        advanceUntilIdle()

        assertThat(viewModel.state.value.isAttachmentsOverlayVisible).isFalse()
    }

    @Test
    fun `onMessageLongClicked should open reaction dialog and set messageToReactTo value when called`() =
        runTest {
            val message = messages.first().toUi()
            advanceUntilIdle()

            viewModel.onMessageLongClicked(message)
            advanceUntilIdle()

            assertThat(viewModel.state.value.isReactionDialogVisible).isTrue()
            assertThat(viewModel.state.value.messageToReactTo).isEqualTo(message)
        }

    @Test
    fun `onReactionDialogDismissed should close reaction dialog and set messageToReactTo to null when called`() =
        runTest {
            advanceUntilIdle()

            viewModel.onReactionDialogDismissed()
            advanceUntilIdle()

            assertThat(viewModel.state.value.isReactionDialogVisible).isFalse()
            assertThat(viewModel.state.value.messageToReactTo).isEqualTo(null)
        }

    @Test
    fun `onMessageClicked should toggle message info when called`() = runTest {
        advanceUntilIdle()
        val oldChatListItems = viewModel.state.value.chatListItems

        viewModel.onMessageClicked(message1Id)
        advanceUntilIdle()

        assertThat(viewModel.state.value.chatListItems).isEqualTo(
            oldChatListItems.toggleMessageInfo(
                message1Id
            )
        )
    }

    @Test
    fun `onAttachmentClicked should open attachments bottom sheet when called`() = runTest {
        advanceUntilIdle()

        viewModel.onAttachmentClicked()
        advanceUntilIdle()

        assertThat(viewModel.state.value.isAttachmentsOverlayVisible).isTrue()
    }

    @Test
    fun `onDismissSendMoneyDialog should close attachments bottom sheet when called`() = runTest {
        advanceUntilIdle()

        viewModel.onDismissSendMoneyDialog()
        advanceUntilIdle()

        assertThat(viewModel.state.value.isSendMoneyDialogVisible).isFalse()
    }

    @Test
    fun `onValueChanged should update amountToTransfer when called`() = runTest {
        advanceUntilIdle()

        viewModel.onValueChanged(amountToTransfer)
        advanceUntilIdle()

        assertThat(viewModel.state.value.amountToTransfer).isEqualTo(amountToTransfer)
    }

    @Test
    fun `onSendMoneyClicked should openSendMoneyDialog bottom sheet when called`() = runTest {
        advanceUntilIdle()

        viewModel.onSendMoneyClicked()
        advanceUntilIdle()
        assertThat(viewModel.state.value.amountToTransfer).isEmpty()
        assertThat(viewModel.state.value.isAttachmentsOverlayVisible).isFalse()
        assertThat(viewModel.state.value.isSendMoneyDialogVisible).isTrue()
    }

    @Test
    fun `onSendClicked should show snackbar when transaction fails`() = runTest {
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onSendClicked()
            advanceUntilIdle()
            assertEquals(
                ChatScreenEffect.ShowSnackBar(
                    SnackBarData(
                        title = UiText.StringRes(Res.string.error),
                        message = UiText.StringRes(Res.string.something_went_wrong),
                        isError = true
                    )
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSendImageClicked should send image message`() = runTest {
        val imageBytes = listOf(byteArrayOf(1, 2, 3))
        everySuspend { messageRepository.sendMessage(any()) } returns Unit

        viewModel.onSendImageClicked(imageBytes)
        advanceUntilIdle()

        verifySuspend { messageRepository.sendMessage(any()) }
    }

    @Test
    fun `onChatActionsMenuClicked should show chat actions dialog`() = runTest {
        advanceUntilIdle()

        viewModel.onChatActionsMenuClicked()
        advanceUntilIdle()

        assertThat(viewModel.state.value.isChatActionsDialogVisible).isTrue()
    }

    @Test
    fun `onChatActionsMenuDialogDismissed should hide chat actions and confirm delete dialogs`() =
        runTest {
            advanceUntilIdle()
            viewModel.onChatActionsMenuClicked()
            advanceUntilIdle()

            viewModel.onChatActionsMenuDialogDismissed()
            advanceUntilIdle()

            assertThat(viewModel.state.value.isChatActionsDialogVisible).isFalse()
            assertThat(viewModel.state.value.isConfirmDeleteChatDialogVisible).isFalse()
        }

    @Test
    fun `onConfirmDeleteChatDialogDismissed should hide confirm delete chat dialog`() = runTest {
        advanceUntilIdle()
        viewModel.onDeleteChatClicked()
        advanceUntilIdle()

        viewModel.onConfirmDeleteChatDialogDismissed()
        advanceUntilIdle()

        assertThat(viewModel.state.value.isConfirmDeleteChatDialogVisible).isFalse()
    }

    @Test
    fun `onDeleteChatClicked should show confirm delete dialog and hide chat actions dialog`() =
        runTest {
            advanceUntilIdle()
            viewModel.onChatActionsMenuClicked()
            advanceUntilIdle()

            viewModel.onDeleteChatClicked()
            advanceUntilIdle()

            assertThat(viewModel.state.value.isChatActionsDialogVisible).isFalse()
            assertThat(viewModel.state.value.isConfirmDeleteChatDialogVisible).isTrue()
        }

    @Test
    fun `onConfirmDeleteChatClicked should call repository deleteChatById and emit success snackbar`() =
        runTest {
            everySuspend { chatRepository.deleteChatById(chatId) } returns Unit
            advanceUntilIdle()

            viewModel.effect.test {
                viewModel.onConfirmDeleteChatClicked()
                advanceUntilIdle()

                val expected = ChatScreenEffect.ShowSnackBar(
                    SnackBarData(
                        title = UiText.StringRes(Res.string.success),
                        message = UiText.StringRes(Res.string.chat_deleted_successfully),
                        isError = false
                    )
                )
                val item = awaitItem()
                assertThat(item).isEqualTo(expected)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onConfirmDeleteChatClicked should show error snackbar when deleteChatById fails`() =
        runTest {
            everySuspend { chatRepository.deleteChatById(chatId) } throws Exception("delete failed")
            advanceUntilIdle()

            viewModel.effect.test {
                viewModel.onConfirmDeleteChatClicked()
                advanceUntilIdle()

                val expected = ChatScreenEffect.ShowSnackBar(
                    SnackBarData(
                        title = UiText.StringRes(Res.string.error),
                        message = UiText.StringRes(Res.string.could_not_delete_chat),
                        isError = true
                    )
                )
                val item = awaitItem()
                assertThat(item).isEqualTo(expected)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onVoiceClicked should start recording when not already recording`() = runTest {
        every { audioRecordRepository.isRecording() } returns false
        everySuspend { permissionsController.providePermission(Permission.RECORD_AUDIO) } returns Unit
        every { audioRecordRepository.startRecording() } returns Unit

        viewModel.onRecordClicked()
        advanceUntilIdle()

        verifySuspend { permissionsController.providePermission(Permission.RECORD_AUDIO) }
        verify { audioRecordRepository.startRecording() }
        assertThat(viewModel.state.value.isRecordingVoice).isTrue()
    }

    @Test
    fun `onVoiceClicked should stop recording and play audio when already recording`() = runTest {
        val testFilePath = "/test/path/audio.mp4"
        every { audioRecordRepository.isRecording() } returns true
        every { audioRecordRepository.stopRecording() } returns testFilePath
        every { audioPlayer.play(any()) } returns Unit

        viewModel.onRecordClicked()
        advanceUntilIdle()

        verify { audioRecordRepository.stopRecording() }
        verify { audioPlayer.play(testFilePath) }
        assertThat(viewModel.state.value.isRecordingVoice).isFalse()
    }

    @Test
    fun `onVoiceClicked should show error snackbar when recording permission is denied`() =
        runTest {
            every { audioRecordRepository.isRecording() } returns false
            everySuspend { permissionsController.providePermission(Permission.RECORD_AUDIO) } throws DeniedException(
                Permission.RECORD_AUDIO
            )
            advanceUntilIdle()

            viewModel.effect.test {
                viewModel.onRecordClicked()
                advanceUntilIdle()

                assertEquals(
                    ChatScreenEffect.ShowSnackBar(
                        SnackBarData(
                            title = UiText.StringRes(Res.string.error),
                            message = UiText.StringRes(Res.string.permission_denied_title),
                            isError = true
                        )
                    ), awaitItem()
                )
                cancelAndIgnoreRemainingEvents()
            }

            assertThat(viewModel.state.value.isRecordingVoice).isFalse()
        }

    @Test
    fun `onVoiceClicked should show error snackbar when recording start fails`() = runTest {
        every { audioRecordRepository.isRecording() } returns false
        everySuspend { permissionsController.providePermission(Permission.RECORD_AUDIO) } returns Unit
        every { audioRecordRepository.startRecording() } throws Exception("Recording failed")
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onRecordClicked()
            advanceUntilIdle()

            assertEquals(
                ChatScreenEffect.ShowSnackBar(
                    SnackBarData(
                        title = UiText.StringRes(Res.string.error),
                        message = UiText.StringRes(Res.string.error_recording_failed),
                        isError = true
                    )
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }

        assertThat(viewModel.state.value.isRecordingVoice).isFalse()
    }

    @Test
    fun `onVoiceClicked should not update recording state when permission request fails`() =
        runTest {
            every { audioRecordRepository.isRecording() } returns false
            everySuspend { permissionsController.providePermission(Permission.RECORD_AUDIO) } throws Exception(
                "Permission error"
            )
            advanceUntilIdle()

            viewModel.onRecordClicked()
            advanceUntilIdle()

            verify(exactly(0)) { audioRecordRepository.startRecording() }
            assertThat(viewModel.state.value.isRecordingVoice).isFalse()
        }

    @Test
    fun `onCancelVoiceRecordClicked should stop recording and update state`() = runTest {
        every { audioRecordRepository.stopRecording() } returns ""

        viewModel.onCancelRecordClicked()
        advanceUntilIdle()

        verify { audioRecordRepository.stopRecording() }
        assertThat(viewModel.state.value.isRecordingVoice).isFalse()
    }

    @Test
    fun `onSendVoiceRecordClicked should stop recording and update state`() = runTest {
        val testFilePath = "/test/path/audio.mp4"
        every { audioRecordRepository.stopRecording() } returns testFilePath

        viewModel.onSendRecordClicked()
        advanceUntilIdle()

        verify { audioRecordRepository.stopRecording() }
        assertThat(viewModel.state.value.isRecordingVoice).isFalse()
    }

    @Test
    fun `onSendVoiceRecordClicked should show error when chatId is null`() = runTest {
        every { audioRecordRepository.stopRecording() } returns "/test/path/audio.mp4"

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onSendRecordClicked()
            advanceUntilIdle()

            assertEquals(
                ChatScreenEffect.ShowSnackBar(
                    SnackBarData(
                        title = UiText.StringRes(Res.string.error),
                        message = UiText.StringRes(Res.string.error_failed_to_process_audio),
                        isError = true
                    )
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSendVoiceRecordClicked should show error when filePath is empty`() = runTest {
        every { audioRecordRepository.stopRecording() } returns ""

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onSendRecordClicked()
            advanceUntilIdle()

            assertEquals(
                ChatScreenEffect.ShowSnackBar(
                    SnackBarData(
                        title = UiText.StringRes(Res.string.error),
                        message = UiText.StringRes(Res.string.error_invalid_recording),
                        isError = true
                    )
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSendVoiceRecordClicked should show error when audio processing fails`() = runTest {
        val testFilePath = "/test/path/audio.mp4"
        every { audioRecordRepository.stopRecording() } returns testFilePath

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.effect.test {
            viewModel.onSendRecordClicked()
            advanceUntilIdle()

            assertEquals(
                ChatScreenEffect.ShowSnackBar(
                    SnackBarData(
                        title = UiText.StringRes(Res.string.error),
                        message = UiText.StringRes(Res.string.error_failed_to_process_audio),
                        isError = true
                    )
                ), awaitItem()
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onMessageVoiceClicked should do nothing if message is not found`() =
        runTest {
            val nonExistentMessageId = Uuid.random()
            viewModel.onMessageVoiceClicked(nonExistentMessageId)
            advanceUntilIdle()
            verify(exactly(0)) { audioPlayer.play(any()) }
        }

    @Test
    fun `onMessageVoiceClicked should play voice message if not playing`() = runTest {
        val voiceMessage = voiceMessage(voiceMessageId)
        everySuspend { messageRepository.loadMessages(any(), any(), any()) } returns PagedData(
            listOf(voiceMessage), 1, true
        )
        setAudioPlayerPlaybackMocks()

        val viewModel = createViewModel()
        viewModel.onMessagesScrolled()
        advanceUntilIdle()

        viewModel.onMessageVoiceClicked(voiceMessageId)
        advanceUntilIdle()

        verify { audioPlayer.play("filePath") }
        val updatedItem =
            viewModel.state.value.chatListItems.first() as AudioMessageUiState
        assertThat(updatedItem.isPlaying).isFalse()
    }

    @Test
    fun `onMessageVoiceClicked should stop other playing messages and play the new one`() =
        runTest {
            val voiceMessage1 = voiceMessage(voiceMessageId)
            val voiceMessage2 = voiceMessage(Uuid.random())
            everySuspend { messageRepository.loadMessages(any(), any(), any()) } returns PagedData(
                listOf(voiceMessage1, voiceMessage2), 1, true
            )
            setAudioPlayerPlaybackMocks()

            val viewModel = createViewModel()
            viewModel.onMessagesScrolled()
            advanceUntilIdle()

            viewModel.onMessageVoiceClicked(voiceMessage1.id)
            advanceUntilIdle()
            viewModel.onMessageVoiceClicked(voiceMessage2.id)
            advanceUntilIdle()

            val items = viewModel.state.value.chatListItems
            val vm1 =
                items.find { it is AudioMessageUiState && it.messageDetails.id == voiceMessage1.id } as AudioMessageUiState?
            val vm2 =
                items.find { it is AudioMessageUiState && it.messageDetails.id == voiceMessage2.id } as AudioMessageUiState?
            assertThat(vm1?.isPlaying != false).isFalse()
            assertThat(vm2?.isPlaying != false).isFalse()
        }

    @Test
    fun `onReactionSelected should add reaction if user has not reacted`() = runTest {
        val reaction = "👍"
        val message = messages.first().copy(id = message1Id, reactions = emptyList())
        everySuspend { messageRepository.loadMessages(chatId, any(), any()) } returns PagedData(listOf(message), 1, true)
        everySuspend { messageRepository.addMessageReaction(message.id, reaction) } returns Unit
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onReactionSelected(message.id, reaction)
        advanceUntilIdle()

        verifySuspend { messageRepository.addMessageReaction(message.id, reaction) }
    }

    @Test
    fun `onReactionSelected should remove reaction if user already reacted`() = runTest {
        val reaction = "👍"
        val message = messages.first()
            .copy(reactions = listOf(MessageReaction(reaction, chatRequesterId, message1Id)))
        everySuspend { messageRepository.loadMessages(chatId, any(), any()) } returns PagedData(listOf(message), 0, true)
        everySuspend { messageRepository.removeMessageReaction(message.id, reaction) } returns Unit
        viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onMessageLongClicked(message.toUi())
        advanceUntilIdle()

        viewModel.onReactionSelected(message.id, reaction)
        advanceUntilIdle()

        verifySuspend { messageRepository.removeMessageReaction(message.id, reaction) }
    }

    @Test
    fun `onCollectAddReaction should add reaction to message when reaction is received`() =
        runTest {
            val reaction = "👍"
            val otherUserId = Uuid.parse("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
            val messageReaction = MessageReaction(reaction, otherUserId, message1Id)
            val message = messages.first().copy(id = message1Id, reactions = emptyList())

            everySuspend {
                messageRepository.loadMessages(chatId, any(), any())
            } returns PagedData(listOf(message), 1, true)
            every { messageRepository.observeMessageReactions() } returns flowOf(messageReaction)

            val viewModel = createViewModel()
            viewModel.onMessagesScrolled()
            advanceUntilIdle()

            val chatListItems = viewModel.state.value.chatListItems
            val updatedMessage = chatListItems.find { item ->
                when (item) {
                    is TextMessageUiState -> item.messageDetails.id == message1Id
                    is ImageMessageUiState -> item.messageDetails.id == message1Id
                    is AudioMessageUiState -> item.messageDetails.id == message1Id
                    is OrderMessageUiState -> item.messageDetails.id == message1Id
                    is ImagesGroupChatItem -> item.imagesUiState.any { it.messageDetails.id == message1Id }
                    else -> false
                }
            }

            val actualMessage = when (updatedMessage) {
                is TextMessageUiState -> updatedMessage.messageDetails
                is ImageMessageUiState -> updatedMessage.messageDetails
                is AudioMessageUiState -> updatedMessage.messageDetails
                is OrderMessageUiState -> updatedMessage.messageDetails
                is ImagesGroupChatItem -> updatedMessage.imagesUiState.firstOrNull { it.messageDetails.id == message1Id }?.messageDetails
                else -> null
            }

            assertThat(actualMessage?.reactions?.size).isEqualTo(1)
            assertThat(actualMessage?.reactions?.first()?.emoji).isEqualTo(reaction)
            assertThat(actualMessage?.reactions?.first()?.userId).isEqualTo(otherUserId)
        }

    @Test
    fun `onCollectAddReaction should replace existing reaction from same user`() = runTest {
        val oldReaction = "👍"
        val newReaction = "❤️"
        val otherUserId = Uuid.parse("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
        val existingReaction = MessageReaction(oldReaction, otherUserId, message1Id)
        val newMessageReaction = MessageReaction(newReaction, otherUserId, message1Id)
        val message = messages.first().copy(id = message1Id, reactions = listOf(existingReaction))

        everySuspend {
            messageRepository.loadMessages(chatId, any(), any())
        } returns PagedData(listOf(message), 0, true)
        every { messageRepository.observeMessageReactions() } returns flowOf(newMessageReaction)

        val viewModel = createViewModel()
        viewModel.onMessagesScrolled()
        advanceUntilIdle()

        val chatListItems = viewModel.state.value.chatListItems
        val updatedMessage = chatListItems.find { item ->
            when (item) {
                is TextMessageUiState -> item.messageDetails.id == message1Id
                is ImageMessageUiState -> item.messageDetails.id == message1Id
                is AudioMessageUiState -> item.messageDetails.id == message1Id
                is OrderMessageUiState -> item.messageDetails.id == message1Id
                else -> false
            }
        }

        val actualMessage = when (updatedMessage) {
            is TextMessageUiState -> updatedMessage.messageDetails
            is ImageMessageUiState -> updatedMessage.messageDetails
            is AudioMessageUiState -> updatedMessage.messageDetails
            is OrderMessageUiState -> updatedMessage.messageDetails
            else -> null
        }

        assertThat(actualMessage?.reactions?.size).isEqualTo(1)
        assertThat(actualMessage?.reactions?.first()?.emoji).isEqualTo(newReaction)
        assertThat(actualMessage?.reactions?.first()?.userId).isEqualTo(otherUserId)
    }

    @Test
    fun `onCollectAddReaction should update selected image messages with reaction`() = runTest {
        val reaction = "👍"
        val otherUserId = Uuid.parse("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
        val messageReaction = MessageReaction(reaction, otherUserId, message1Id)
        val imageMessage = Message(
            id = message1Id,
            senderId = chatRequesterId,
            chatId = chatId,
            sendAt = LocalDateTime.now(),
            status = MessageStatus.SENT,
            content = MessageContent.Image(ImageData.ImageUrl(imageUrl)),
            isMine = true
        )

        everySuspend {
            messageRepository.loadMessages(chatId, any(), any())
        } returns PagedData(listOf(imageMessage), 0, true)
        every { messageRepository.observeMessageReactions() } returns flowOf(messageReaction)

        val viewModel = createViewModel()
        viewModel.onMessagesScrolled()
        advanceUntilIdle()

        val chatListItems = viewModel.state.value.chatListItems
        val updatedMessages = chatListItems.fromChatItems()

        assertThat(updatedMessages).isNotEmpty()
        val imageMessages = updatedMessages.map { it.toUi() as ImageMessageUiState }

        viewModel.onMessageImageClicked(imageMessages, 0)
        advanceUntilIdle()

        val selectedImageMessages = viewModel.state.value.selectedImageMessages
        assertThat(selectedImageMessages).isNotEmpty()
        assertThat(selectedImageMessages.first().messageDetails.reactions.size).isEqualTo(1)
        assertThat(selectedImageMessages.first().messageDetails.reactions.first().emoji)
            .isEqualTo(reaction)
        assertThat(selectedImageMessages.first().messageDetails.reactions.first().userId)
            .isEqualTo(otherUserId)
    }

    @Test
    fun `onCollectRemoveReaction should remove reaction from message when reaction is removed`() =
        runTest {
            val reaction = "👍"
            val otherUserId = Uuid.parse("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
            val messageReaction = MessageReaction(reaction, otherUserId, message1Id)
            val message =
                messages.first().copy(id = message1Id, reactions = listOf(messageReaction))

            everySuspend {
                messageRepository.loadMessages(chatId, any(), any())
            } returns PagedData(listOf(message), 0, true)
            every { messageRepository.observeRemovedMessageReactions() } returns flowOf(
                messageReaction
            )

            val viewModel = createViewModel()
            viewModel.onMessagesScrolled()
            advanceUntilIdle()

            val chatListItems = viewModel.state.value.chatListItems
            val updatedMessage = chatListItems.find { item ->
                when (item) {
                    is TextMessageUiState -> item.messageDetails.id == message1Id
                    is ImageMessageUiState -> item.messageDetails.id == message1Id
                    is AudioMessageUiState -> item.messageDetails.id == message1Id
                    is OrderMessageUiState -> item.messageDetails.id == message1Id
                    is ImagesGroupChatItem -> item.imagesUiState.any { it.messageDetails.id == message1Id }
                    else -> false
                }
            }

            val actualMessage = when (updatedMessage) {
                is TextMessageUiState -> updatedMessage.messageDetails
                is ImageMessageUiState -> updatedMessage.messageDetails
                is AudioMessageUiState -> updatedMessage.messageDetails
                is OrderMessageUiState -> updatedMessage.messageDetails
                is ImagesGroupChatItem -> updatedMessage.imagesUiState.firstOrNull { it.messageDetails.id == message1Id }?.messageDetails
                else -> null
            }

            assertThat(actualMessage?.reactions?.size).isEqualTo(0)
        }

    @Test
    fun `onCollectRemoveReaction should only remove matching reaction by user and emoji`() =
        runTest {
            val reactionToRemove = "👍"
            val reactionToKeep = "❤️"
            val userId1 = Uuid.parse("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
            val userId2 = Uuid.parse("cccccccc-cccc-cccc-cccc-cccccccccccc")
            val reaction1 = MessageReaction(reactionToRemove, userId1, message1Id)
            val reaction2 = MessageReaction(reactionToKeep, userId2, message1Id)
            val message =
                messages.first().copy(id = message1Id, reactions = listOf(reaction1, reaction2))

            everySuspend {
                messageRepository.loadMessages(chatId, any(), any())
            } returns PagedData(listOf(message), 0, true)
            every { messageRepository.observeRemovedMessageReactions() } returns flowOf(reaction1)

            val viewModel = createViewModel()
            viewModel.onMessagesScrolled()
            advanceUntilIdle()

            val chatListItems = viewModel.state.value.chatListItems
            val updatedMessage = chatListItems.find { item ->
                when (item) {
                    is TextMessageUiState -> item.messageDetails.id == message1Id
                    is ImageMessageUiState -> item.messageDetails.id == message1Id
                    is AudioMessageUiState -> item.messageDetails.id == message1Id
                    is OrderMessageUiState -> item.messageDetails.id == message1Id
                    is ImagesGroupChatItem -> item.imagesUiState.any { it.messageDetails.id == message1Id }
                    else -> false
                }
            }

            val actualMessage = when (updatedMessage) {
                is TextMessageUiState -> updatedMessage.messageDetails
                is ImageMessageUiState -> updatedMessage.messageDetails
                is AudioMessageUiState -> updatedMessage.messageDetails
                is OrderMessageUiState -> updatedMessage.messageDetails
                is ImagesGroupChatItem -> updatedMessage.imagesUiState.firstOrNull { it.messageDetails.id == message1Id }?.messageDetails
                else -> null
            }

            assertThat(actualMessage?.reactions?.size).isEqualTo(1)
            assertThat(actualMessage?.reactions?.first()?.emoji).isEqualTo(reactionToKeep)
            assertThat(actualMessage?.reactions?.first()?.userId).isEqualTo(userId2)
        }

    @Test
    fun `onCollectAddReaction should not update non-matching messages`() = runTest {
        val reaction = "👍"
        val otherUserId = Uuid.parse("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
        val messageReaction = MessageReaction(reaction, otherUserId, message1Id)
        val message1 = messages.first().copy(reactions = emptyList())
        val message2 = messages[1].copy(reactions = emptyList())

        everySuspend {
            messageRepository.loadMessages(chatId, any(), any())
        } returns PagedData(listOf(message1, message2), 0, true)
        every { messageRepository.observeMessageReactions() } returns flowOf(messageReaction)

        val viewModel = createViewModel()
        viewModel.onMessagesScrolled()
        advanceUntilIdle()

        val chatListItems = viewModel.state.value.chatListItems

        val updatedMessage1 = chatListItems.find { item ->
            when (item) {
                is TextMessageUiState -> item.messageDetails.id == message1Id
                is ImageMessageUiState -> item.messageDetails.id == message1Id
                is AudioMessageUiState -> item.messageDetails.id == message1Id
                is OrderMessageUiState -> item.messageDetails.id == message1Id
                is ImagesGroupChatItem -> item.imagesUiState.any { it.messageDetails.id == message1Id }
                else -> false
            }
        }


        val updatedMessage2 = chatListItems.find { item ->
            when (item) {
                is TextMessageUiState -> item.messageDetails.id == message2Id
                is ImageMessageUiState -> item.messageDetails.id == message2Id
                is AudioMessageUiState -> item.messageDetails.id == message2Id
                is OrderMessageUiState -> item.messageDetails.id == message2Id
                is ImagesGroupChatItem -> item.imagesUiState.any { it.messageDetails.id == message2Id }
                else -> false
            }
        }

        val actualMessage1 = when (updatedMessage1) {
            is TextMessageUiState -> updatedMessage1.messageDetails
            is ImageMessageUiState -> updatedMessage1.messageDetails
            is AudioMessageUiState -> updatedMessage1.messageDetails
            is OrderMessageUiState -> updatedMessage1.messageDetails
            is ImagesGroupChatItem -> updatedMessage1.imagesUiState.firstOrNull { it.messageDetails.id == message1Id }?.messageDetails
            else -> null
        }

        val actualMessage2 = when (updatedMessage2) {
            is TextMessageUiState -> updatedMessage2.messageDetails
            is ImageMessageUiState -> updatedMessage2.messageDetails
            is AudioMessageUiState -> updatedMessage2.messageDetails
            is OrderMessageUiState -> updatedMessage2.messageDetails
            is ImagesGroupChatItem -> updatedMessage2.imagesUiState.firstOrNull { it.messageDetails.id == message2Id }?.messageDetails

            else -> null
        }

        assertThat(actualMessage1?.reactions?.size).isEqualTo(1)
        assertThat(actualMessage2?.reactions?.size).isEqualTo(0)
    }

    @Test
    fun `onCollectRemoveReaction should not update non-matching messages`() = runTest {
        val reaction = "👍"
        val otherUserId = Uuid.parse("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
        val messageReaction = MessageReaction(reaction, otherUserId, message1Id)
        val message1 = messages.first().copy(reactions = listOf(messageReaction))
        val message2Reaction = MessageReaction(reaction, otherUserId, message2Id)
        val message2 = messages[1].copy(reactions = listOf(message2Reaction))

        everySuspend {
            messageRepository.loadMessages(chatId, any(), any())
        } returns PagedData(listOf(message1, message2), 0, true)
        every { messageRepository.observeRemovedMessageReactions() } returns flowOf(messageReaction)

        val viewModel = createViewModel()
        viewModel.onMessagesScrolled()
        advanceUntilIdle()

        val chatListItems = viewModel.state.value.chatListItems

        val updatedMessage1 = chatListItems.find { item ->
            when (item) {
                is TextMessageUiState -> item.messageDetails.id == message1Id
                is ImageMessageUiState -> item.messageDetails.id == message1Id
                is AudioMessageUiState -> item.messageDetails.id == message1Id
                is OrderMessageUiState -> item.messageDetails.id == message1Id
                is ImagesGroupChatItem -> item.imagesUiState.any { it.messageDetails.id == message1Id }
                else -> false
            }
        }

        val updatedMessage2 = chatListItems.find { item ->
            when (item) {
                is TextMessageUiState -> item.messageDetails.id == message2Id
                is ImageMessageUiState -> item.messageDetails.id == message2Id
                is AudioMessageUiState -> item.messageDetails.id == message2Id
                is OrderMessageUiState -> item.messageDetails.id == message2Id
                is ImagesGroupChatItem -> item.imagesUiState.any { it.messageDetails.id == message2Id }
                else -> false
            }
        }

        val actualMessage1 = when (updatedMessage1) {
            is TextMessageUiState -> updatedMessage1.messageDetails
            is ImageMessageUiState -> updatedMessage1.messageDetails
            is AudioMessageUiState -> updatedMessage1.messageDetails
            is OrderMessageUiState -> updatedMessage1.messageDetails
            is ImagesGroupChatItem -> updatedMessage1.imagesUiState.firstOrNull { it.messageDetails.id == message1Id }?.messageDetails

            else -> null
        }

        val actualMessage2 = when (updatedMessage2) {
            is TextMessageUiState -> updatedMessage2.messageDetails
            is ImageMessageUiState -> updatedMessage2.messageDetails
            is AudioMessageUiState -> updatedMessage2.messageDetails
            is OrderMessageUiState -> updatedMessage2.messageDetails
            is ImagesGroupChatItem -> updatedMessage2.imagesUiState.firstOrNull { it.messageDetails.id == message2Id }?.messageDetails

            else -> null
        }

        assertThat(actualMessage1?.reactions?.size).isEqualTo(0)
        assertThat(actualMessage2?.reactions?.size).isEqualTo(1)
    }


    /**
     * Reverse everything applied by:
     *
     * List<Message>.toChatItems()
     *
     * Steps reversed:
     *  1. Remove DateSeparator items
     *  2. Ungroup ImagesGroupChatItem into ImageMessageUiState items
     *  3. Convert MessageUiState → Message
     */
    fun List<ChatListItem>.fromChatItems(): List<Message> {
        val flattened = this.flatMap { item ->
            when (item) {
                is DateSeparator -> emptyList()
                is ImagesGroupChatItem -> item.imagesUiState
                else -> listOf(item)
            }
        }

        val messages = flattened.mapNotNull { item ->
            when (item) {
                is MessageUiState -> item.toEntity()
                else -> null
            }
        }

        return messages
    }

    private fun createViewModel(): ChatViewModel {
        return ChatViewModel(
            chatRepository = chatRepository,
            messageRepository = messageRepository,
            audioRecordRepository = audioRecordRepository,
            userRepository = userRepository,
            imageDownloaderService = imageDownloaderService,
            permissionsController = permissionsController,
            audioPlayer = audioPlayer,
            chatArgs = chatArgs,
            dispatcher = testDispatcher,
            transactionRepository = transactionRepository,
        )
    }

    private fun setAudioPlayerPlaybackMocks(duration: Long = 1000L) {
        everySuspend { audioRecordRepository.getAudioFilePath(any()) } returns "filePath"
        every { audioPlayer.getDuration(any()) } returns duration
        every { audioPlayer.getDurationOfCurrentAudio() } returns duration
        every { audioPlayer.getCurrentPosition() } returns duration
        every { audioPlayer.play(any()) } returns Unit
        every { audioPlayer.pause() } returns Unit
        every { audioPlayer.stop() } returns Unit
    }

    private companion object {

        val user: User = User(
            firstName = "ali",
            lastName = "nawar",
            imageUrl = "https://image.com",
        )
        val chatId = Uuid.parse("11111111-1111-1111-1111-111111111111")
        val chatRequesterId = Uuid.parse("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
        val receiverId = Uuid.parse("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
        val chatName = "Noor"
        val chatImage = "https://image.com/noor.jpg"

        val chat = Chat(
            id = chatId,
            name = chatName,
            imageUrl = chatImage,
            requesterId = chatRequesterId,
            receiverId = receiverId
        )

        val message1Id = Uuid.parse("22222222-2222-2222-2222-222222222222")
        val message2Id = Uuid.parse("33333333-3333-3333-3333-333333333333")
        val voiceMessageId = Uuid.parse("44444444-4444-4444-4444-444444444444")
        const val amountToTransfer = "22.2"
        const val amount = 22.22

        const val imageUrl = "https://test.com/image.jpg"
        const val audioUrl = "https://test.com/audio.mp3"


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

        fun voiceMessage(id: Uuid) = Message(
            id,
            chatRequesterId,
            chatId,
            LocalDateTime.now(),
            MessageStatus.SENT,
            MessageContent.Audio(AudioData.AudioUrl(audioUrl)),
            true
        )
    }
}