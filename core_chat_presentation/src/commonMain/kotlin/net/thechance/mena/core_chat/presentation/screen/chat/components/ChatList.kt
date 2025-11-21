@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.core_chat.presentation.screen.chat.AudioMessageUiState
import net.thechance.mena.core_chat.presentation.screen.chat.AyahMessageUiState
import net.thechance.mena.core_chat.presentation.screen.chat.ChatListItem
import net.thechance.mena.core_chat.presentation.screen.chat.DateSeparator
import net.thechance.mena.core_chat.presentation.screen.chat.ImageMessageUiState
import net.thechance.mena.core_chat.presentation.screen.chat.ImagesGroupChatItem
import net.thechance.mena.core_chat.presentation.screen.chat.MessageUiState
import net.thechance.mena.core_chat.presentation.screen.chat.MoneyMessageUiState
import net.thechance.mena.core_chat.presentation.screen.chat.TextMessageUiState
import net.thechance.mena.core_chat.presentation.utils.rememberNetworkStatus
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun ChatList(
    chatName: String,
    items: List<ChatListItem>,
    chatAvatarUrl: String,
    chatListState: LazyListState,
    onMessageClick: (Uuid) -> Unit,
    onSurahClick :(Int) ->Unit,
    onAyahClick : (Int,Int) -> Unit,
    onMessageImageClick: (List<ImageMessageUiState>, Int) -> Unit,
    onFailedMessageClick: (MessageUiState) -> Unit,
    onMessageLongClick: (MessageUiState) -> Unit,
    onMessageVoiceClick: (Uuid) -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    ) {
    val isConnectedToNetwork by rememberNetworkStatus()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Theme.spacing._12),
        state = chatListState,
        reverseLayout = true,
        contentPadding = PaddingValues(top = Theme.spacing._16),
        verticalArrangement = Arrangement.Bottom
    ) {
        itemsIndexed(
            items = items,
            key = { _, item ->
                when (item) {
                    is TextMessageUiState -> item.messageDetails.id.toString()
                    is ImagesGroupChatItem -> item.imagesUiState.first().messageDetails.id.toString()
                    is ImageMessageUiState -> item.messageDetails.id.toString()
                    is AudioMessageUiState -> item.messageDetails.id.toString()
                    is AyahMessageUiState -> item.messageDetails.id.toString()
                    is DateSeparator -> item.label.toString()
                    is MoneyMessageUiState -> item.messageDetails.id.toString()
                }
            }
        ) { _ , item ->
            val isLastItem = items.indexOf(item) == 0
            val paddingBottom = if (isLastItem)
                0.dp
            else if (item is TextMessageUiState && item.messageDetails.isLastInSeries)
                Theme.spacing._16
            else if (item is ImagesGroupChatItem && item.imagesUiState.last().messageDetails.isLastInSeries)
                Theme.spacing._16
            else if (item is AudioMessageUiState && item.messageDetails.isLastInSeries)
                Theme.spacing._16
            else
                Theme.spacing._2

            ChatListItem(
                chatName = chatName,
                item = item,
                chatAvatarUrl = chatAvatarUrl,
                onMessageClick = onMessageClick,
                onMessageImageClick = onMessageImageClick,
                onMessageVoiceClick = onMessageVoiceClick,
                onFailedMessageClick = onFailedMessageClick,
                onMessageLongClick = onMessageLongClick,
                onLinkClick = onLinkClick,
                onSurahClick = onSurahClick,
                onAyahClick = onAyahClick,
                modifier = Modifier.padding(bottom = paddingBottom)
            )
        }

        if (isConnectedToNetwork.not()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    NoInternetConnection()
                }
            }
        }
    }
}
