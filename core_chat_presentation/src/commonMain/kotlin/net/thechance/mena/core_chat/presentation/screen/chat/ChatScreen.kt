package net.thechance.mena.core_chat.presentation.screen.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatHeader
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatInputBar
import net.thechance.mena.core_chat.presentation.screen.chat.components.ChatList
import net.thechance.mena.core_chat.presentation.screen.chat.components.messagingScreenOverlays
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatScreen() {
    var state by remember { mutableStateOf(ChatScreenState()) }
    ChatScreenContent(state) { messageId -> // temp until handling the view model
        state = state.copy(
            chatListItems = state.chatListItems.map { item ->
                if (item is ChatListItem.Message && item.data.message.id == messageId) {
                    item.copy(
                        data = item.data.copy(
                            showMessageInfo = !item.data.showMessageInfo
                        )
                    )
                } else {
                    item
                }
            }
        )
    }

}

@Composable
fun ChatScreenContent(
    state: ChatScreenState = ChatScreenState(),
    onMessageClick: (String) -> Unit
) {
    var showChatActionsDialog by remember { mutableStateOf(false) }
    var showResendMessageDialog by remember { mutableStateOf(false) }
    var showDeleteChatDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ChatHeader(
                chatName = state.chat.name,
                onMenuClick = { showChatActionsDialog = true },
                onBackClick = { },
                modifier = Modifier
                    .fillMaxWidth()
            )
        },
        bottomBar = {
            ChatInputBar(
                userInput = state.inputMessage,
                onTextChange = { },
                onSendButtonClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Theme.colorScheme.background.surface)
            )
        },
        overlays = {
            messagingScreenOverlays(
                showChatActionsDialog = showChatActionsDialog,
                showResendMessageDialog = showResendMessageDialog,
                showDeleteChatDialog = showDeleteChatDialog,
                onDeleteChatClick = { showDeleteChatDialog = true },
                onDismissChatActionsDialog = { showChatActionsDialog = false },
                onDismissDeleteChatDialog = { showDeleteChatDialog = false },
                onDismissResendMessageDialog = { showResendMessageDialog = false }
            )
        }
    ) {
        ChatList(
            items = state.chatListItems,
            chat = state.chat,
            onMessageClick = onMessageClick,
            onFailedMessageClick = { showResendMessageDialog = true },
        )
    }
}

@Composable
@Preview()
private fun PreviewMessagingScreenDark() {

    MenaTheme {
        ChatScreen()
    }
}
