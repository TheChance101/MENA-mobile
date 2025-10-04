@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import net.thechance.mena.core_chat.presentation.screen.chat.ChatListItem
import net.thechance.mena.core_chat.presentation.screen.chat.ChatUiState
import net.thechance.mena.core_chat.presentation.screen.chat.MessageUiState
import net.thechance.mena.core_chat.presentation.screen.chat.TextMessageUiState
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun ChatListItem(
    item: ChatListItem,
    chat: ChatUiState,
    onMessageClick: (Uuid) -> Unit,
    onFailedMessageClick: (MessageUiState) -> Unit,
    modifier: Modifier = Modifier
) {
    when (item) {
        is ChatListItem.DateSeparator -> {
            Text(
                text = item.label,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeTertiary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Theme.spacing._8),
                textAlign = TextAlign.Center
            )
        }

        is ChatListItem.Message -> {
            val markedMessage = item.data
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = if (markedMessage.message.isMine) Arrangement.End else Arrangement.Start
            ) {
                TextMessageItem(
                    message = markedMessage.message as TextMessageUiState, // temporal casting until more MessageTypes involved
                    chatAvatarUrl = chat.avatarUrl,
                    showMessageInfo = markedMessage.showMessageInfo,
                    isMarkedLastInSeries = markedMessage.isMarkedLastInSeries,
                    onClick = { onMessageClick(markedMessage.message.id) },
                    onFailClick = { onFailedMessageClick(markedMessage.message) },
                    modifier = Modifier
                )
            }
        }
    }
}
