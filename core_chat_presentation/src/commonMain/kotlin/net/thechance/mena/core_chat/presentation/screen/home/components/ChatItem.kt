package net.thechance.mena.core_chat.presentation.screen.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.am
import mena.core_chat_presentation.generated.resources.ic_message_read
import mena.core_chat_presentation.generated.resources.ic_message_sent
import mena.core_chat_presentation.generated.resources.pm
import mena.core_chat_presentation.generated.resources.you
import net.thechance.mena.core_chat.presentation.screen.contacts.components.CircularAvatar
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.ChatUiState
import net.thechance.mena.core_chat.presentation.utils.asString
import net.thechance.mena.core_chat.presentation.utils.getFormattedTimeWithTodayTimeOrYesterdayTextOrSimpleDate
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ChatItem(
    chat: ChatUiState,
    onChatClicked: (ChatUiState) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onChatClicked(chat) }
            .padding(horizontal = Theme.spacing._16),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularAvatar(
            contactImageUri = chat.imageUrl,
            size = 48.dp,
            modifier = Modifier.padding(end = Theme.spacing._8)
        )
        NameAndLastMessage(chat)
        TimeAndStatus(chat)
    }

}

@Composable
private fun TimeAndStatus(chat: ChatUiState) {
    Column(
        modifier = Modifier.padding(vertical = Theme.spacing._4),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._2, Alignment.Top)
    ) {
        chat.lastMessage ?: return

        val uiTime = getFormattedTimeWithTodayTimeOrYesterdayTextOrSimpleDate(
            dateTime = chat.lastMessage.time,
            am = stringResource(Res.string.am),
            pm = stringResource(Res.string.pm)
        )
        Text(
            text = uiTime.asString(),
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeSecondary
        )

        if (chat.lastMessage.isMine) {
            if (chat.status is ChatUiState.Status.Sent) {
                Icon(
                    painter = painterResource(Res.drawable.ic_message_sent),
                    contentDescription = null,
                    tint = Theme.colorScheme.shadeTertiary
                )
            } else if (chat.status is ChatUiState.Status.Read) {
                Icon(
                    painter = painterResource(Res.drawable.ic_message_read),
                    contentDescription = null,
                    tint = Theme.colorScheme.shadeTertiary
                )
            }
        } else {
            if (chat.status is ChatUiState.Status.UnRead) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            color = Theme.colorScheme.brand.brand,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = chat.status.count.toString(),
                        style = Theme.typography.label.extraSmall,
                        color = Theme.colorScheme.primary.onPrimary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.NameAndLastMessage(chat: ChatUiState) {
    val lastMessage = if (chat.lastMessage == null) ""
    else if (chat.lastMessage.isMine) "${stringResource(Res.string.you)}: ${chat.lastMessage.text.asString()}"
    else chat.lastMessage.text.asString()

    Column(
        modifier = Modifier.padding(vertical = Theme.spacing._4).weight(1f)
            .padding(end = Theme.spacing._4),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._2)
    ) {
        Text(
            text = chat.name,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = lastMessage,
            style = Theme.typography.label.extraSmall,
            color = Theme.colorScheme.shadeTertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}