@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.flow.SharedFlow
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.you
import net.thechance.mena.core_chat.presentation.components.snackBarHost.LocalSnackBarHostController
import net.thechance.mena.core_chat.presentation.navigation.LocalNavController
import net.thechance.mena.core_chat.presentation.screen.chat.components.AttachmentsBottomSheet
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatHeader
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatInputBar
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatList
import net.thechance.mena.core_chat.presentation.screen.chat.components.FullImagePagerView
import net.thechance.mena.core_chat.presentation.screen.chat.components.RecordingBar
import net.thechance.mena.core_chat.presentation.screen.chat.components.chatActionsMenuDialog
import net.thechance.mena.core_chat.presentation.screen.chat.components.messageReactionDialog
import net.thechance.mena.core_chat.presentation.screen.chat.components.resendFailedMessageDialog
import net.thechance.mena.core_chat.presentation.utils.EffectHandler
import net.thechance.mena.core_chat.presentation.utils.PaginationTrigger
import net.thechance.mena.core_chat.presentation.utils.rememberCameraManager
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(onClickBackFromChat: () -> Unit = {}) {
    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) { factory.createPermissionsController() }
    val navController = LocalNavController.current

    val viewModel: ChatViewModel = koinViewModel(parameters = { parametersOf(controller) })

    BindEffect(controller)

    BackHandler(enabled = true) {
        onClickBackFromChat()
        navController.popBackStack()
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val effects = viewModel.effect

    EffectsHandler(effects = effects, onClickBackFromChat = onClickBackFromChat)

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
    val chatListState = rememberLazyListState()

    val cameraManager = rememberCameraManager(
        onResult = interactions::onCameraResult
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
                    onMenuClick = interactions::onChatActionsMenuClicked,
                    onBackClick = interactions::onBackClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            },
            bottomBar = {
                AnimatedContent(
                    targetState = state.isRecordingVoice,
                    modifier = Modifier
                        .fillMaxWidth(),
                    transitionSpec = {
                        if (targetState) {
                            slideInVertically(animationSpec = tween(300)) { fullHeight -> fullHeight } + fadeIn() togetherWith
                                    slideOutVertically(animationSpec = tween(300)) { fullHeight -> -fullHeight } + fadeOut()
                        } else {
                            slideInVertically(animationSpec = tween(300)) { fullHeight -> -fullHeight } + fadeIn() togetherWith
                                    slideOutVertically(animationSpec = tween(300)) { fullHeight -> fullHeight } + fadeOut()
                        }
                    },
                    label = "ChatBarAnimation"
                ) { isRecording ->
                    if (isRecording) {
                        RecordingBar(
                            onSendClick =interactions::onSendRecordClicked,
                            onCancelClick = interactions::onCancelRecordClicked
                        )
                    } else {
                        ChatInputBar(
                            userInput = state.inputMessage,
                            onTextChange = interactions::onInputMessageChanged,
                            onSendButtonClick = interactions::onSendMessageClicked,
                            onAttachButtonClick = interactions::onAttachmentClicked,
                            onVoiceRecordClick = interactions::onRecordClicked
                        )
                    }
                }
            },
            overlays = {
                resendFailedMessageDialog(
                    showResendMessageDialog = state.isResendMessageDialogVisible,
                    onDismissResendMessageDialog = interactions::onResendMessageDialogDismissed,
                    onDeleteFailedMessageClick = interactions::onDeleteFailedMessageClicked,
                    onResendFailedMessageClick = interactions::onResendMessageClicked,
                )

                chatActionsMenuDialog(
                    showChatActionsDialog = state.isChatActionsDialogVisible,
                    showConfirmDeleteChatDialog = state.isConfirmDeleteChatDialogVisible,
                    actionsMenuInteractionListener = interactions as ActionsMenuInteractionListener
                )
            }
        ) {
            ChatList(
                items = state.chatListItems,
                chatAvatarUrl = state.chatAvatarUrl,
                chatListState = chatListState,
                onMessageClick = interactions::onMessageClicked,
                onMessageImageClick = interactions::onMessageImageClicked,
                onMessageVoiceClick = interactions::onMessageVoiceClicked,
                onFailedMessageClick = interactions::onFailedMessageClicked,
                paginationError = state.paginationError,
                onMessageLongClick = interactions::onMessageLongClicked,
            )
        }

        AnimatedVisibility(
            visible = state.isImagePagerVisible,
            modifier = Modifier.fillMaxSize(),
        ) {
            val isMine = state.selectedMessage?.isMine == true
            val senderName = if (isMine) stringResource(Res.string.you) else state.chatName
            val senderImageUrl = if (isMine) state.userData.imageUrl else state.chatAvatarUrl

            FullImagePagerView(
                messages = state.selectedImageMessages,
                senderName = senderName,
                senderImageUrl = senderImageUrl,
                initialPage = state.currentImageIndexForPreview,
                onCloseClick = interactions::onCloseImageViewClicked,
                onImageLongClick = interactions::onMessageLongClicked,
                onDownloadClick = interactions::onDownloadImageClicked,
            )
        }

        AnimatedVisibility(
            visible = state.isAttachmentsOverlayVisible,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            AttachmentsBottomSheet(
                attachmentsInteractionListener = interactions
            )
        }
    }

    PaginationTrigger(
        list = state.chatListItems,
        listState = chatListState,
        remainingItemsToLoadNextPage = 15,
        loadNextItems = interactions::onMessagesScrolled
    )

    messageReactionDialog(
        isVisible = state.isReactionDialogVisible,
        message = state.messageToReactTo,
        currentUserId = state.chatRequesterId,
        onDismiss = interactions::onReactionDialogDismissed,
        onReactionClicked = interactions::onReactionSelected
    )
}

@Composable
private fun EffectsHandler(
    effects: SharedFlow<ChatScreenEffect>,
    onClickBackFromChat: () -> Unit
) {
    val snackBarHostController = LocalSnackBarHostController.current
    val navController = LocalNavController.current
    EffectHandler(effects, key1 = navController.currentBackStackEntry) { effect ->
        when (effect) {
            is ChatScreenEffect.NavigateBack -> {
                onClickBackFromChat()
                navController.popBackStack()
            }

            is ChatScreenEffect.ShowSnackBar -> {
                snackBarHostController.showSnackBar(effect.snackBarData)
            }
        }
    }
}