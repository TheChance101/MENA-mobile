@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatHeader
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatInputBar
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatList
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatScreenOverlays
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = koinViewModel()
) {
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
            onFailedMessageClick = interactions::onFailedMessageClicked,
        )
    }
}