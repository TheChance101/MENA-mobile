@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatHeader
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatInputBar
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatList
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatScreenOverlays
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview
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
    state: ChatScreenState = ChatScreenState(),
    interactions: ChatInteractionListener
) {
    val scope = rememberCoroutineScope()

    val imagePickerLauncher = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Multiple(maxSelection = 10),
        scope = scope,
        onResult = { byteArrays ->
            interactions.onSendImageClicked(byteArrays)
        }
    )

    val cameraState = rememberPeekabooCameraState(onCapture = { capturedBytes ->
        capturedBytes?.let {
            interactions.onSendImageClicked(listOf(it))
            interactions.onCameraDismissed()
        }
    })

    if (state.isCameraOpen) {
        Box(modifier = Modifier.fillMaxSize()) {
            PeekabooCamera(
                state = cameraState,
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
        Scaffold(
            topBar = {
                ChatHeader(
                    chatName = state.chat.name,
                    onMenuClick = {},
                    onBackClick = interactions::onBackClicked,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            bottomBar = {
                ChatInputBar(
                    userInput = state.inputMessage,
                    onTextChange = interactions::onInputMessageChanged,
                    onSendButtonClick = interactions::onSendMessageClicked,
                    onAttachButtonClick = { imagePickerLauncher.launch() },
                    onCameraButtonClick = interactions::onCameraClicked,
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
                chat = state.chat,
                onMessageClick = interactions::onMessageClicked,
                onFailedMessageClick = interactions::onFailedMessageClicked,
            )
        }
    }
}

@Composable
@Preview()
private fun PreviewMessagingScreenDark() {

    MenaTheme {
        ChatScreen()
    }
}