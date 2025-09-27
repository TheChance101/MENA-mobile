@file:OptIn(ExperimentalTime::class)

package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.presentation.screen.chat.MessageStatus
import net.thechance.mena.core_chat.presentation.screen.chat.MessageUiState
import net.thechance.mena.core_chat.presentation.screen.chat.TextMessageUiState
import net.thechance.mena.core_chat.presentation.utils.now
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime

@Composable
fun BaseMessageLayout(
    message: MessageUiState,
    showMessageInfo: Boolean,
    isMarkedLastInSeries: Boolean,
    modifier: Modifier = Modifier,
    chatAvatarUrl: String? = null,
    onFailClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val messageBackground = if (message.isMine)
        Theme.colorScheme.background.surfaceLow
    else
        Theme.colorScheme.brand.brandVariant

    val showSenderAvatar = chatAvatarUrl != null

    val messagePaddingStart = if (message.isMine)
        Theme.spacing._24
    else if (showSenderAvatar)
        Theme.spacing._8
    else
        Theme.spacing._32

    val messagePaddingEnd = if (message.isMine) 0.dp else Theme.spacing._8

    val messageShape = if (message.isMine && isMarkedLastInSeries)
        RoundedCornerShape(
            topStart = Theme.radius.md,
            topEnd = Theme.radius.md,
            bottomStart = Theme.radius.md,
            bottomEnd = Theme.radius.xxs
        )
    else if (!message.isMine && isMarkedLastInSeries)
        RoundedCornerShape(
            topStart = Theme.radius.md,
            topEnd = Theme.radius.md,
            bottomStart = Theme.radius.xxs,
            bottomEnd = Theme.radius.md
        )
    else
        RoundedCornerShape(size = Theme.radius.md)

    val messageInfoAlignment = if (message.isMine)
        Alignment.Start
    else
        Alignment.End

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._2),
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            if (!message.isMine && showSenderAvatar) {
                AsyncImage(
                    modifier = Modifier
                        .size(Theme.spacing._24)
                        .clip(CircleShape),
                    model = chatAvatarUrl,
                    contentScale = ContentScale.Crop,
                    contentDescription = "Sender Avatar",
                )
            }

            Box(
                modifier = Modifier
                    .padding(start = messagePaddingStart, end = messagePaddingEnd)
                    .clip(messageShape)
                    .background(
                        color = messageBackground,
                        shape = messageShape
                    )
                    .padding(
                        horizontal = Theme.spacing._8,
                        vertical = Theme.spacing._4
                    )
            ) {
                content()
            }

        }
        AnimatedVisibility(
            visible = showMessageInfo || isMarkedLastInSeries,
            modifier = Modifier
                .align(messageInfoAlignment)
        ) {
            MessageInfo(
                messageTime = message.sendTime,
                messageStatus = message.status,
                messageIsMine = message.isMine,
                onFailClick = onFailClick,
                modifier = Modifier
                    .align(messageInfoAlignment)
                    .padding(start = messagePaddingStart, end = messagePaddingEnd)
            )
        }
    }
}

@Composable
@Preview()
private fun PreviewBaseMessageLayout() {
    MenaTheme {
        Box(
            modifier = Modifier
                .background(Theme.colorScheme.background.surface)
        ) {
            BaseMessageLayout(
                message = TextMessageUiState(
                    "0",
                    "1",
                    LocalDateTime.now(),
                    MessageStatus.READ,
                    false,
                    ""
                ),
                showMessageInfo = true,
                isMarkedLastInSeries = true
            ) {
                Text(
                    text = "Hello,\nBilal",
                    style = Theme.typography.body.small
                )
            }
        }

    }
}

