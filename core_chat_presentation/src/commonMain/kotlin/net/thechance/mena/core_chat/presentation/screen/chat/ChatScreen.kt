@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.you
import net.thechance.mena.core_chat.presentation.camera.rememberCameraManager
import net.thechance.mena.core_chat.presentation.screen.chat.components.AttachmentsBottomSheet
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatHeader
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatInputBar
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatList
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatScreenOverlays
import net.thechance.mena.core_chat.presentation.screen.chat.components.FullImagePagerView
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun ChatScreen() {
    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) { factory.createPermissionsController() }

    val viewModel: ChatViewModel = koinViewModel(parameters = { parametersOf(controller) })

    BindEffect(controller)

    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatScreenContent(
        state = state,
        interactions = viewModel
    )
}

@Composable
fun ChatScreenContent(
    state: ChatScreenState,
    interactions: ChatInteractionListener
) {
    val cameraManager = rememberCameraManager(
        onResult = { sharedImageByteArray  ->
            sharedImageByteArray?.let {
                interactions.onSendImageClicked(listOf(sharedImageByteArray))
            }
            interactions.onCameraClosed()
        }
    )

    LaunchedEffect(state.isCameraOpen) {
        if (state.isCameraOpen) {
            cameraManager.launch()
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            topBar = {
                ChatHeader(
                    chatName = state.chatName,
                    onMenuClick = {},
                    onBackClick = interactions::onBackClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            },
            bottomBar = {
                ChatInputBar(
                    userInput = state.inputMessage,
                    onTextChange = interactions::onInputMessageChanged,
                    onSendButtonClick = interactions::onSendMessageClicked,
                    onAttachButtonClick = interactions::onAttachmentClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Theme.colorScheme.background.surface)
                )
            },
            overlays = {
                ChatScreenOverlays(
                    showResendMessageDialog = state.isResendMessageDialogVisible,
                    onDismissResendMessageDialog = interactions::onResendMessageDialogDismissed,
                    onDeleteFailedMessageClick = interactions::onDeleteFailedMessageClicked,
                    onResendFailedMessageClick = interactions::onResendMessageClicked,
                )
            }
        ) {
            ChatList(
                items = state.chatListItems,
                chatAvatarUrl = state.chatAvatarUrl,
                onMessageClick = interactions::onMessageClicked,
                onMessageImageClick = interactions::onMessageImageClicked,
                onFailedMessageClick = interactions::onFailedMessageClicked,
            )
        }

        AnimatedVisibility(
            visible = state.isImagePagerVisible,
            modifier = Modifier.fillMaxSize(),
        ) {
            val isMine = state.selectedMessage?.isMine == true
            val senderName = if (isMine) stringResource(Res.string.you) else state.chatName
            val senderImageUrl = if (isMine) "" else state.chatAvatarUrl // todo: add user image

            FullImagePagerView(
                message = state.selectedMessage,
                senderName = senderName,
                senderImageUrl = senderImageUrl,
                initialPage = state.currentImageIndexForPreview,
                onCloseClick = interactions::onCloseImageViewClicked,
                onDownloadClick = interactions::onDownloadImageClicked,
            )
        }

        AnimatedVisibility(
            visible = state.isAttachmentsOverlayVisible,
            enter = slideInVertically(initialOffsetY = { it }),
            exit= slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            AttachmentsBottomSheet(
                attachmentsInteractionListener = interactions
            )
        }
    }
}