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
import mena.core_chat_presentation.generated.resources.ic_message_read
import mena.core_chat_presentation.generated.resources.ic_message_sent
import net.thechance.mena.core_chat.presentation.screen.contacts.components.CircularAvatar
import net.thechance.mena.core_chat.presentation.screen.home.HomeScreenState.HomeUiState
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource

@Composable
fun ChatCard(
    chats: HomeUiState,
    onChatClicked: (HomeUiState) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onChatClicked(chats) },
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        CircularAvatar(
            contactImageUri = chats.imageUrl,
            size = 48.dp,
            modifier = Modifier.padding(end = Theme.spacing._8)
        )
        NameAndLastMessage(chats)
        TimeAndStatus(chats)
    }

}

@Composable
private fun TimeAndStatus(chats: HomeUiState) {
    Column(
        modifier = Modifier.padding(vertical = Theme.spacing._4),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._2, Alignment.Top)
    ) {
        Text(
            text = chats.time,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeSecondary
        )
        if (chats.isMine) {
            if (chats.status is HomeUiState.Status.Sent) {
                Icon(
                    painter = painterResource(Res.drawable.ic_message_sent),
                    contentDescription = null,
                    tint = Theme.colorScheme.shadeTertiary
                )
            } else if (chats.status is HomeUiState.Status.Read) {
                Icon(
                    painter = painterResource(Res.drawable.ic_message_read),
                    contentDescription = null,
                    tint = Theme.colorScheme.shadeTertiary
                )
            }
        } else {
            if (chats.status is HomeUiState.Status.UnRead) {
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
                        text = chats.status.count.toString(),
                        style = Theme.typography.label.small,
                        color = Theme.colorScheme.primary.onPrimary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.NameAndLastMessage(chats: HomeUiState) {
    Column(
        modifier = Modifier.padding(vertical = Theme.spacing._4).weight(1f)
            .padding(end = Theme.spacing._4),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._2)
    ) {
        Text(
            text = chats.name,
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = chats.lastMessage,
            style = Theme.typography.label.small,
            color = Theme.colorScheme.shadeTertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}