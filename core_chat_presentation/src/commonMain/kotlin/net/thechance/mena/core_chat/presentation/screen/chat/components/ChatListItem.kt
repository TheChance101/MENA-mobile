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
import net.thechance.mena.core_chat.presentation.screen.chat.MessageUiState
import net.thechance.mena.core_chat.presentation.utils.asString
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun ChatListItem(
    item: ChatListItem,
    chatAvatarUrl: String,
    onMessageClick: (Uuid) -> Unit,
    onFailedMessageClick: (MessageUiState) -> Unit,
    modifier: Modifier = Modifier
) {
    when (item) {
        is ChatListItem.DateSeparator -> {
            Text(
                text = item.label.asString(),
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
                horizontalArrangement = if (markedMessage.isMine) Arrangement.End else Arrangement.Start
            ) {
                MessageLayout(
                    message = markedMessage,
                    chatAvatarUrl = chatAvatarUrl,
                    showMessageInfo = markedMessage.isVisibleMessageInfo,
                    isMarkedLastInSeries = markedMessage.isLastInSeries,
                    onMessageClick = { onMessageClick(markedMessage.id) },
                    onFailClick = { onFailedMessageClick(markedMessage) },
                    modifier = Modifier
                )
            }
        }
    }
}
