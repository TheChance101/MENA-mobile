@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_profile_placeholder
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.presentation.screen.chat.MessageUiState
import net.thechance.mena.core_chat.presentation.utils.noHoverClickable
import net.thechance.mena.core_chat.presentation.utils.now
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun TextMessageLayout(
    message: MessageUiState,
    showMessageInfo: Boolean,
    isMarkedLastInSeries: Boolean,
    modifier: Modifier = Modifier,
    chatAvatarUrl: String? = null,
    onFailClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
) {
    if (message.content !is MessageContent.Text) return

    val messageBackground =
        if (message.isMine) Theme.colorScheme.background.surfaceLow
        else Theme.colorScheme.brand.brandVariant

    val messagePaddingStart = if (message.isMine)
        Theme.spacing._24
    else
        Theme.spacing._8

    val messagePaddingEnd = if (message.isMine) 0.dp else Theme.spacing._8
    val maxRadius = Theme.radius.md

    val messageShape = if (message.isMine && isMarkedLastInSeries)
        RoundedCornerShape(
            topStart = maxRadius,
            topEnd = maxRadius,
            bottomStart = maxRadius,
            bottomEnd = Theme.radius.xxs
        )
    else if (!message.isMine && isMarkedLastInSeries)
        RoundedCornerShape(
            topStart = maxRadius,
            topEnd = maxRadius,
            bottomStart = Theme.radius.xxs,
            bottomEnd = maxRadius
        )
    else
        RoundedCornerShape(size = maxRadius)

    val messageInfoAlignment = if (message.isMine)
        Alignment.Start
    else
        Alignment.End
    val messageAlignment = if (message.isMine) Alignment.End else Alignment.Start

    val verticalPadding = Theme.spacing._8
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._2),
        horizontalAlignment = messageAlignment
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
        ) {
            if (!message.isMine) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isMarkedLastInSeries) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = chatAvatarUrl,
                            placeholder = painterResource(Res.drawable.ic_profile_placeholder),
                            error = painterResource(Res.drawable.ic_profile_placeholder),
                            contentScale = ContentScale.Crop,
                            contentDescription = "Contact photo",
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .padding(start = messagePaddingStart, end = messagePaddingEnd)
                    .clip(messageShape)
                    .noHoverClickable(onClick = onMessageClick)
                    .background(color = messageBackground, shape = messageShape)
                    .padding(
                        horizontal = verticalPadding,
                        vertical = Theme.spacing._4
                    )
            ) {
                Text(
                    text = message.content.text,
                    style = Theme.typography.body.small,
                    color = Theme.colorScheme.shadeSecondary
                )
            }

        }
        AnimatedVisibility(
            visible = showMessageInfo,
            modifier = Modifier.align(messageInfoAlignment)
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
            modifier = Modifier.fillMaxWidth()
        ) {
            TextMessageLayout(
                message = MessageUiState(
                    Uuid.random(),
                    Uuid.random(),
                    sendTime = LocalDateTime.now(),
                    status = MessageStatus.READ,
                    isMine = false,
                    content = MessageContent.Text("Good Morning!")
                ),
                showMessageInfo = true,
                isMarkedLastInSeries = true,
            )
        }
    }
}

