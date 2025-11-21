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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.you
import net.thechance.mena.core_chat.presentation.components.snackBarHost.LocalSnackBarHostController
import net.thechance.mena.core_chat.presentation.navigation.ConfirmPaymentRoute
import net.thechance.mena.core_chat.presentation.navigation.AyahRoute
import net.thechance.mena.core_chat.presentation.navigation.LocalNavController
import net.thechance.mena.core_chat.presentation.navigation.SurahRoute
import net.thechance.mena.core_chat.presentation.navigation.OrderDetailsRoute
import net.thechance.mena.core_chat.presentation.screen.chat.components.AttachmentsBottomSheet
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatHeader
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatInputBar
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatList
import net.thechance.mena.core_chat.presentation.screen.chat.components.FullImagePagerView
import net.thechance.mena.core_chat.presentation.screen.chat.components.MessageReactionDialog
import net.thechance.mena.core_chat.presentation.screen.chat.components.RecordingBar
import net.thechance.mena.core_chat.presentation.screen.chat.components.attachmentsSendMoneyBottomSheet
import net.thechance.mena.core_chat.presentation.screen.chat.components.chatActionsMenuDialog
import net.thechance.mena.core_chat.presentation.screen.chat.components.resendFailedMessageDialog
import net.thechance.mena.core_chat.presentation.utils.EffectHandler
import net.thechance.mena.core_chat.presentation.utils.PaginationTrigger
import net.thechance.mena.core_chat.presentation.utils.rememberCameraManager
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(onClickBackFromChat: () -> Unit = {}) {
    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) { factory.createPermissionsController() }

    val viewModel: ChatViewModel = koinViewModel(parameters = { parametersOf(controller) })

    BindEffect(viewModel.permissionsController)

    BackHandler(enabled = true) {
        viewModel.onBackClicked()
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val effects = viewModel.effect

    val chatLazyListState = rememberLazyListState()

    AudioLifecycleObserver(viewModel)

    EffectsHandler(
        effects = effects,
        chatLazyListState = chatLazyListState,
        onClickBackFromChat = onClickBackFromChat
    )

    ChatScreenContent(
        state = state,
        chatLazyListState = chatLazyListState,
        interactions = viewModel
    )
}

@Composable
fun ChatScreenContent(
    state: ChatScreenState,
    chatLazyListState: LazyListState,
    interactions: ChatInteractionListener
) {
    val cameraManager = rememberCameraManager(
        onResult = interactions::onCameraResult
    )

    LaunchedEffect(state.isCameraOpen) {
        if (state.isCameraOpen) {
            cameraManager.launch()
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            statusBarColor = Theme.colorScheme.background.surfaceLow,
            backgroundColor = Theme.colorScheme.background.surface,
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
                ChatInputBarContent(
                    state = state,
                    interactions = interactions,
                    modifier = Modifier.fillMaxWidth()
                )
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

                attachmentsSendMoneyBottomSheet(
                    isVisible = state.isSendMoneyDialogVisible,
                    attachmentsInteractionListener = interactions,
                    value = state.amountToTransfer,
                    isLoading = state.isLoadingSendMoneyButton,
                )
            },
            modifier = Modifier.imePadding()
        ) {
            ChatList(
                chatName = state.chatName,
                items = state.chatListItems,
                chatAvatarUrl = state.chatAvatarUrl,
                chatListState = chatLazyListState,
                onMessageClick = interactions::onMessageClicked,
                onMessageImageClick = interactions::onMessageImageClicked,
                onMessageVoiceClick = interactions::onMessageVoiceClicked,
                onViewOrderDetailsClick = interactions::onViewOrderDetailsClicked,
                onFailedMessageClick = interactions::onFailedMessageClicked,
                onMessageLongClick = interactions::onMessageLongClicked,
                onLinkClick = interactions::onLinkClicked,
                onSurahClick = interactions::onSurahClicked,
                onAyahClick = interactions::onAyahClicked,
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) { keyboardController?.hide() }
            )
        }

        AnimatedVisibility(
            visible = state.isImagePagerVisible,
            modifier = Modifier.fillMaxSize(),
        ) {
            val isMine =
                state.selectedImageMessages.isNotEmpty() && state.selectedImageMessages[0].messageDetails.isMine
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
        listState = chatLazyListState,
        remainingItemsToLoadNextPage = 15,
        loadNextItems = interactions::onMessagesScrolled
    )

    MessageReactionDialog(
        isVisible = state.isReactionDialogVisible,
        message = state.messageToReactTo,
        currentUserId = state.chatRequesterId,
        onDismiss = interactions::onReactionDialogDismissed,
        onReactionClicked = interactions::onReactionSelected
    )
}

@Composable
fun AudioLifecycleObserver(viewModel: ChatViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                viewModel.onStopAudioPlayback()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.onStopAudioPlayback()
        }
    }
}

@Composable
private fun ChatInputBarContent(
    state: ChatScreenState,
    interactions: ChatInteractionListener,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = state.isRecordingVoice,
        modifier = modifier,
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
                onSendClick = interactions::onSendRecordClicked,
                onCancelClick = interactions::onCancelRecordClicked,
            )
        } else {
            ChatInputBar(
                userInput = state.inputMessage,
                onTextChange = interactions::onInputMessageChanged,
                onSendButtonClick = interactions::onSendTextMessageClicked,
                onAttachButtonClick = interactions::onAttachmentClicked,
                onVoiceRecordClick = interactions::onRecordClicked,
            )
        }
    }
}

@Composable
private fun EffectsHandler(
    effects: SharedFlow<ChatScreenEffect>,
    chatLazyListState: LazyListState,
    onClickBackFromChat: () -> Unit
) {
    val snackBarHostController = LocalSnackBarHostController.current
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    EffectHandler(effects, key1 = navController.currentBackStackEntry) { effect ->
        when (effect) {
            is ChatScreenEffect.NavigateBack -> {
                onClickBackFromChat()
                navController.popBackStack()
            }

            is ChatScreenEffect.ShowSnackBar -> {
                snackBarHostController.showSnackBar(effect.snackBarData)
            }

            is ChatScreenEffect.ScrollToBottom -> {
                scope.launch { chatLazyListState.animateScrollToItem(0) }
            }

            is ChatScreenEffect.NavigateToOrderDetails -> {
                navController.navigate(OrderDetailsRoute(effect.orderId.toString()))
            }

            is ChatScreenEffect.OpenUrl -> {
                uriHandler.openUri(effect.url)
            }

            is ChatScreenEffect.NavigateToSurah -> {
                navController.navigate(SurahRoute(effect.surahId))
            }

            is ChatScreenEffect.NavigateToAyah -> {
                navController.navigate(AyahRoute(effect.surahId, effect.ayahId))
            }

            is ChatScreenEffect.NavigateToConfirmPayment -> {
                navController.navigate(
                    ConfirmPaymentRoute(
                        effect.amount,
                        effect.transactionId.toString()
                    )
                )
            }
        }
    }
}