@file:OptIn(ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.doesNotContain
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
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
import mena.core_chat_presentation.generated.resources.success
import net.thechance.mena.core_chat.domain.entity.AudioData
import net.thechance.mena.core_chat.domain.entity.Chat
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
import net.thechance.mena.core_chat.presentation.utils.AudioPlayer
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
    private val audioRecordRepository = mock<AudioRecordRepository>()
    private val chatArgs = mock<ChatArgs>()
    private val imageDownloaderService = mock<ImageDownloaderService>()
    private val permissionsController = mock<PermissionsController>()
    private val audioPlayer = mock<AudioPlayer>()
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
        every { messageRepository.observeDeleteChat() } returns flowOf()
        everySuspend { messageRepository.markMessagesOfChatAsRead(any()) } returns Unit

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
            everySuspend { messageRepository.deleteMessage(any()) } returns Unit
            advanceUntilIdle()
            val msgUi = messages.first().copy(status = MessageStatus.FAILED).toUi()
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
                messages.first().copy(status = MessageStatus.FAILED).toUi()
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
            val messages = messages.map(Message::toUi)
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
    fun `onMessageLongClicked should open reaction dialog and set messageToReactTo value when called`() = runTest {
        val message = messages.first().toUi()
        advanceUntilIdle()

        viewModel.onMessageLongClicked(message)
        advanceUntilIdle()

        assertThat(viewModel.state.value.isReactionDialogVisible).isTrue()
        assertThat(viewModel.state.value.messageToReactTo).isEqualTo(message)
    }

    @Test
    fun `onReactionDialogDismissed should close reaction dialog and set messageToReactTo to null when called`() = runTest {
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

        assertThat(viewModel.state.value.chatListItems).isEqualTo(oldChatListItems.toggleMessageInfo(message1Id))
    }

    @Test
    fun `onAttachmentClicked should open attachments bottom sheet when called`() = runTest {
        advanceUntilIdle()

        viewModel.onAttachmentClicked()
        advanceUntilIdle()

        assertThat(viewModel.state.value.isAttachmentsOverlayVisible).isTrue()
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
    fun `onVoiceClicked should show error snackbar when recording permission is denied`() = runTest {
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
    fun `onVoiceClicked should not update recording state when permission request fails`() = runTest {
        every { audioRecordRepository.isRecording() } returns false
        everySuspend { permissionsController.providePermission(Permission.RECORD_AUDIO) } throws Exception("Permission error")
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
        every { chatArgs.chatId } returns ""

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
    fun `onSendVoiceRecordClicked should show error when filePath is empty`() = runTest {
        every { audioRecordRepository.stopRecording() } returns ""
        every { chatArgs.chatId } returns chatId.toString()

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
        every { chatArgs.chatId } returns chatId.toString()

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

        everySuspend { audioRecordRepository.getAudioFilePath(any()) } returns "filePath"
        every { audioPlayer.getDuration(any()) } returns 1000L
        every { audioPlayer.getCurrentPosition() } returns 1000L
        every { audioPlayer.play(any()) } returns Unit
        every { audioPlayer.pause() } returns Unit
        every { audioPlayer.stop() } returns Unit

        val viewModel = createViewModel()
        viewModel.onMessagesScrolled()
        advanceUntilIdle()

        viewModel.onMessageVoiceClicked(voiceMessageId)
        advanceUntilIdle()

        verify { audioPlayer.play("filePath") }
        val updatedItem =
            viewModel.state.value.chatListItems.first() as ChatListItem.VoiceMessage
        assertThat(updatedItem.isPlaying).isFalse()
    }

    @Test
    fun `onMessageVoiceClicked should stop other playing messages and play the new one`() = runTest {
        val voiceMessage1 = voiceMessage(voiceMessageId)
        val voiceMessage2 = voiceMessage(Uuid.random())
        everySuspend { messageRepository.loadMessages(any(), any(), any()) } returns PagedData(
            listOf(voiceMessage1, voiceMessage2), 1, true
        )
        everySuspend { audioRecordRepository.getAudioFilePath(any()) } returns "filePath"
        every { audioPlayer.getDuration(any()) } returns 1000L
        every { audioPlayer.getCurrentPosition() } returns 1000L
        every { audioPlayer.play(any()) } returns Unit
        every { audioPlayer.pause() } returns Unit
        every { audioPlayer.stop() } returns Unit

        val viewModel = createViewModel()
        viewModel.onMessagesScrolled()
        advanceUntilIdle()

        viewModel.onMessageVoiceClicked(voiceMessage1.id)
        advanceUntilIdle()
        viewModel.onMessageVoiceClicked(voiceMessage2.id)
        advanceUntilIdle()

        val items = viewModel.state.value.chatListItems
        val vm1 = items.find { it is ChatListItem.VoiceMessage && it.data.id == voiceMessage1.id } as ChatListItem.VoiceMessage?
        val vm2 = items.find { it is ChatListItem.VoiceMessage && it.data.id == voiceMessage2.id } as ChatListItem.VoiceMessage?
        assertThat(vm1?.isPlaying ?: true).isFalse()
        assertThat(vm2?.isPlaying ?: true).isFalse()
    }

    @Test
    fun `onReactionSelected should add reaction if user has not reacted`() = runTest {
        val reaction = "👍"
        val message = messages.first().copy(reactions = emptyList())
        everySuspend {
            messageRepository.loadMessages(chatId, any(), any())
        } returns PagedData(listOf(message), 0, true)
        everySuspend { messageRepository.addMessageReaction(message.id, reaction) } returns Unit
        viewModel.onMessageLongClicked(message.toUi())
        advanceUntilIdle()

        viewModel.onReactionSelected(message.id, reaction)
        advanceUntilIdle()

        verifySuspend { messageRepository.addMessageReaction(message.id, reaction) }
    }

    @Test
    fun `onReactionSelected should remove reaction if user already reacted`() = runTest {
        val reaction = "👍"
        val message = messages.first().copy(reactions = listOf(MessageReaction(reaction, chatRequesterId, message1Id)))
        everySuspend {
            messageRepository.loadMessages(chatId, any(), any())
        } returns PagedData(listOf(message), 0, true)
        everySuspend { messageRepository.removeMessageReaction(message.id, reaction) } returns Unit
        viewModel.onMessageLongClicked(message.toUi())
        advanceUntilIdle()

        viewModel.onReactionSelected(message.id, reaction)
        advanceUntilIdle()

        verifySuspend { messageRepository.removeMessageReaction(message.id, reaction) }
    }

    private fun List<ChatListItem>.currentUiMessages(): List<MessageUiState> =
        filterIsInstance<ChatListItem.ImageMessages>()
            .flatMap { it.data }
            .sortedByDescending { it.sendTime }

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
            dispatcher = testDispatcher
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
        val voiceMessageId = Uuid.parse("44444444-4444-4444-4444-444444444444")


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