@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.presentation.screen.chat.AudioMessageUiState
import net.thechance.mena.core_chat.presentation.screen.chat.AyahMessageUiState
import net.thechance.mena.core_chat.presentation.screen.chat.ChatListItem
import net.thechance.mena.core_chat.presentation.screen.chat.DateSeparator
import net.thechance.mena.core_chat.presentation.screen.chat.ImageMessageUiState
import net.thechance.mena.core_chat.presentation.screen.chat.ImagesGroupChatItem
import net.thechance.mena.core_chat.presentation.screen.chat.MessageUiState
import net.thechance.mena.core_chat.presentation.screen.chat.MoneyMessageUiState
import net.thechance.mena.core_chat.presentation.screen.chat.TextMessageUiState
import net.thechance.mena.core_chat.presentation.utils.asString
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun ChatListItem(
    chatName: String,
    item: ChatListItem,
    chatAvatarUrl: String,
    onMessageClick: (Uuid) -> Unit,
    onMessageImageClick: (List<ImageMessageUiState>, Int) -> Unit,
    onMessageVoiceClick: (Uuid) -> Unit,
    onFailedMessageClick: (MessageUiState) -> Unit,
    onMessageLongClick: (MessageUiState) -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (item) {
        is DateSeparator -> {
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

        is TextMessageUiState -> {
            TextMessageLayout(
                modifier = modifier,
                message = item,
                chatAvatarUrl = chatAvatarUrl,
                showMessageInfo = (
                        item.messageDetails.isVisibleMessageInfo
                                || item.messageDetails.isLastInSeries
                                || item.messageDetails.status == MessageStatus.FAILED
                        ),
                isMarkedLastInSeries = item.messageDetails.isLastInSeries,
                onMessageClick = { onMessageClick(item.messageDetails.id) },
                onMessageLongClick = { onMessageLongClick(item) },
                onFailClick = { onFailedMessageClick(item) },
                onLinkClick = onLinkClick,
            )
        }

        is ImagesGroupChatItem -> {
            val imageMessages = item.imagesUiState
            ImageMessagesLayout(
                modifier = modifier,
                messages = imageMessages,
                chatAvatarUrl = chatAvatarUrl,
                showMessageInfo = (
                        imageMessages.first().messageDetails.isVisibleMessageInfo
                                || imageMessages.first().messageDetails.isLastInSeries
                                || imageMessages.first().messageDetails.status == MessageStatus.FAILED
                        ),
                isMarkedLastInSeries = imageMessages.first().messageDetails.isLastInSeries,
                onMessageImageClick = onMessageImageClick,
                onFailClick = onFailedMessageClick,
            )
        }

        is AudioMessageUiState -> {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = if (item.messageDetails.isMine) Arrangement.End else Arrangement.Start
            ) {
                VoiceMessageLayout(
                    modifier = Modifier.width(280.dp),
                    message = item,
                    chatAvatarUrl = chatAvatarUrl,
                    showMessageInfo = (
                            item.messageDetails.isVisibleMessageInfo
                                    || item.messageDetails.isLastInSeries
                                    || item.messageDetails.status == MessageStatus.FAILED
                            ),
                    isMarkedLastInSeries = item.messageDetails.isLastInSeries,
                    isMessageLoading = item.isLoading || item.isPlaying,
                    progress = item.progress,
                    totalSeconds = item.duration.div(1000),
                    waveformData = item.waveformData,
                    onMessageClick = { onMessageClick(item.messageDetails.id) },
                    onMessageLongClick = { onMessageLongClick(item) },
                    onPlayClick = { onMessageVoiceClick(item.messageDetails.id) },
                    onFailClick = { onFailedMessageClick(item) },
                )
            }
        }

        is ImageMessageUiState -> {
            ImageMessagesLayout(
                modifier = modifier,
                messages = listOf(item),
                chatAvatarUrl = chatAvatarUrl,
                showMessageInfo = (
                        item.messageDetails.isVisibleMessageInfo
                                || item.messageDetails.isLastInSeries
                                || item.messageDetails.status == MessageStatus.FAILED
                        ),
                isMarkedLastInSeries = item.messageDetails.isLastInSeries,
                onMessageImageClick = onMessageImageClick,
                onFailClick = onFailedMessageClick,
            )
        }

        is AyahMessageUiState -> {
            AyahMessageLayout(
                message = item,
                showMessageInfo = (
                        item.messageDetails.isVisibleMessageInfo
                                || item.messageDetails.isLastInSeries
                                || item.messageDetails.status == MessageStatus.FAILED
                        ),
                isMarkedLastInSeries = item.messageDetails.isLastInSeries,
                chatAvatarUrl = chatAvatarUrl,
                onFailClick = { onFailedMessageClick(item) },
                onMessageLongClick = { onMessageLongClick(item) },
                onMessageClick = { onMessageClick(item.messageDetails.id) },
                modifier = modifier
            )
        }

        is MoneyMessageUiState ->
            MoneyMessageLayout(
                chatName =chatName,
                message = item,
                showMessageInfo = (
                        item.messageDetails.isVisibleMessageInfo
                                || item.messageDetails.isLastInSeries
                                || item.messageDetails.status == MessageStatus.FAILED),
                isMarkedLastInSeries = item.messageDetails.isLastInSeries,
                chatAvatarUrl = chatAvatarUrl,
                onFailClick = { onFailedMessageClick(item) },
                onMessageLongClick = { onMessageLongClick(item) },

                )
    }
}
