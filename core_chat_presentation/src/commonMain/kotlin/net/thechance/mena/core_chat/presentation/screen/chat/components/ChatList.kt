@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.core_chat.presentation.screen.chat.ChatListItem
import net.thechance.mena.core_chat.presentation.screen.chat.MessageUiState
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun ChatList(
    items: List<ChatListItem>,
    chatAvatarUrl: String,
    chatListState: LazyListState,
    onMessageClick: (Uuid) -> Unit,
    onMessageImageClick: (List<MessageUiState>, Int) -> Unit,
    onFailedMessageClick: (MessageUiState) -> Unit
) {
    if (items.isNotEmpty()) {
        LaunchedEffect(items[0]) {
            chatListState.animateScrollToItem(0)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Theme.spacing._12),
        state = chatListState,
        reverseLayout = true,
        contentPadding = PaddingValues(top = Theme.spacing._16)
    ) {
        itemsIndexed(
            items = items,
            key = { index,_ -> index }
        ) { _ , item ->
            val isLastItem = items.indexOf(item) == 0
            val paddingBottom = if (isLastItem)
                0.dp
            else if (item is ChatListItem.TextMessage && item.data.isLastInSeries)
                Theme.spacing._16
            else if (item is ChatListItem.ImageMessages && item.data.last().isLastInSeries)
                Theme.spacing._16
            else
                Theme.spacing._2

            ChatListItem(
                item = item,
                chatAvatarUrl = chatAvatarUrl,
                onMessageClick = onMessageClick,
                onMessageImageClick = onMessageImageClick,
                onFailedMessageClick = onFailedMessageClick,
                modifier = Modifier.padding(bottom = paddingBottom)
            )
        }
    }
}
