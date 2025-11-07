@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import net.thechance.mena.core_chat.domain.entity.MessageStatus
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
    onMessageImageClick: (List<MessageUiState>, Int) -> Unit,
    onMessageVoiceClick: (Uuid) -> Unit,
    onFailedMessageClick: (MessageUiState) -> Unit,
    onMessageLongClick: (MessageUiState) -> Unit,
    modifier: Modifier = Modifier
) {
    when (item) {
        is ChatListItem.DateSeparator -> {
            Text(
                text = item.label.asString(),
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadeTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Theme.spacing._8),
            )
        }

        is ChatListItem.TextMessage -> {
            val markedMessage = item.data
            TextMessageLayout(
                modifier = modifier,
                message = markedMessage,
                chatAvatarUrl = chatAvatarUrl,
                showMessageInfo = (markedMessage.isVisibleMessageInfo || markedMessage.isLastInSeries || markedMessage.status == MessageStatus.FAILED),
                isMarkedLastInSeries = markedMessage.isLastInSeries,
                onMessageClick = { onMessageClick(markedMessage.id) },
                onMessageLongClick = { onMessageLongClick(markedMessage) },
                onFailClick = { onFailedMessageClick(markedMessage) },
            )
        }

        is ChatListItem.ImageMessages -> {
            val markedMessage = item.data
            ImageMessagesLayout(
                modifier = modifier,
                messages = markedMessage,
                chatAvatarUrl = chatAvatarUrl,
                showMessageInfo = (markedMessage.last().isVisibleMessageInfo || markedMessage.last().isLastInSeries || markedMessage.last().status == MessageStatus.FAILED),
                isMarkedLastInSeries = markedMessage.last().isLastInSeries,
                onMessageImageClick = onMessageImageClick,
                onFailClick = onFailedMessageClick,
            )
        }

        is ChatListItem.VoiceMessage -> {
            val markedMessage = item.data
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = if (markedMessage.isMine) Arrangement.End else Arrangement.Start
            ) {
                VoiceMessageLayout(
                    message = markedMessage,
                    chatAvatarUrl = chatAvatarUrl,
                    showMessageInfo = (markedMessage.isVisibleMessageInfo || markedMessage.isLastInSeries || markedMessage.status == MessageStatus.FAILED),
                    isMarkedLastInSeries = markedMessage.isLastInSeries,
                    isMessageLoading = item.isLoading || item.isPlaying,
                    progress = item.progress,
                    totalSeconds = item.duration.div(1000),
                    waveformData = item.waveformData,
                    onMessageClick = { onMessageClick(markedMessage.id) },
                    onMessageLongClick = { onMessageLongClick(markedMessage) },
                    onPlayClick = { onMessageVoiceClick(markedMessage.id) },
                    onFailClick = { onFailedMessageClick(markedMessage) },
                )
            }
        }
    }
}
