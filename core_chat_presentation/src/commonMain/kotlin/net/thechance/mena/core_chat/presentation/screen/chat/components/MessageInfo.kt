@file:OptIn(ExperimentalTime::class)
package net.thechance.mena.core_chat.presentation.screen.chat.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.am
import mena.core_chat_presentation.generated.resources.ic_close_circle
import mena.core_chat_presentation.generated.resources.ic_message_read
import mena.core_chat_presentation.generated.resources.ic_message_sent
import mena.core_chat_presentation.generated.resources.pm
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.presentation.utils.formatAsTime
import net.thechance.mena.core_chat.presentation.utils.noHoverClickable
import net.thechance.mena.core_chat.presentation.utils.now
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime


@Composable
fun MessageInfo(
    messageTime: LocalDateTime,
    messageStatus: MessageStatus,
    messageIsMine: Boolean,
    onFailClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val messageInfoColor = if (messageStatus == MessageStatus.FAILED)
        Theme.colorScheme.error
    else
        Theme.colorScheme.shadeTertiary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
        modifier = modifier.noHoverClickable(
            onClick = onFailClick,
            enabled = messageIsMine && messageStatus == MessageStatus.FAILED
        ),
    ) {
        Text(
            text = messageTime.formatAsTime(
                am = stringResource(Res.string.am),
                pm = stringResource(Res.string.pm),
            ),
            style = Theme.typography.label.extraSmall,
            color = messageInfoColor
        )

        if (messageIsMine) {
            when (messageStatus) {
                MessageStatus.LOADING -> {
                    DotsProgressIndicator(
                        numberOfDots = 3,
                        colors = listOf(
                            Theme.colorScheme.stroke,
                            Theme.colorScheme.shadeTertiary,
                            Theme.colorScheme.primary.primary
                        )
                    )
                }

                MessageStatus.SENT -> {
                    Icon(
                        painter = painterResource(Res.drawable.ic_message_sent),
                        contentDescription = "Sent",
                        tint = messageInfoColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                MessageStatus.READ -> {
                    Icon(
                        painter = painterResource(Res.drawable.ic_message_read),
                        contentDescription = "Read",
                        tint = messageInfoColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                MessageStatus.FAILED -> {
                    Icon(
                        painter = painterResource(Res.drawable.ic_close_circle),
                        contentDescription = "Failed",
                        tint = messageInfoColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}


@Composable
@Preview()
private fun PreviewReadMessageInfo() {
    MenaTheme {
        Box(
            modifier = Modifier
                .background(Theme.colorScheme.background.surface)
                .padding(12.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            MessageInfo(
                messageTime = LocalDateTime.now(),
                messageStatus = MessageStatus.READ,
                messageIsMine = true
            )
        }
    }
}

@Composable
@Preview()
private fun PreviewSentMessageInfo() {
    MenaTheme {
        Box(
            modifier = Modifier
                .background(Theme.colorScheme.background.surface)
                .padding(12.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            MessageInfo(
                messageTime = LocalDateTime.now(),
                messageStatus = MessageStatus.SENT,
                messageIsMine = true
            )
        }
    }
}

@Composable
@Preview()
private fun PreviewFailedMessageInfo() {
    MenaTheme {
        Box(
            modifier = Modifier
                .background(Theme.colorScheme.background.surface)
                .padding(12.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            MessageInfo(
                messageTime = LocalDateTime.now(),
                messageStatus = MessageStatus.FAILED,
                messageIsMine = true
            )
        }
    }
}

@Composable
@Preview()
private fun PreviewSendingMessageInfo() {
    MenaTheme {
        Box(
            modifier = Modifier
                .background(Theme.colorScheme.background.surface)
                .padding(12.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            MessageInfo(
                messageTime = LocalDateTime.now(),
                messageStatus = MessageStatus.LOADING,
                messageIsMine = true
            )
        }
    }
}
