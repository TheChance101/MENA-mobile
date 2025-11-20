@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import net.thechance.mena.core_chat.domain.entity.MessageReaction
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.presentation.screen.chat.MessageDetailsUiState
import net.thechance.mena.core_chat.presentation.screen.chat.TextMessageUiState
import net.thechance.mena.core_chat.presentation.screen.contacts.components.CircularAvatar
import net.thechance.mena.core_chat.presentation.utils.containsUrl
import net.thechance.mena.core_chat.presentation.utils.detectAndStyleUrls
import net.thechance.mena.core_chat.presentation.utils.now
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun TextMessageLayout(
    message: TextMessageUiState,
    showMessageInfo: Boolean,
    isMarkedLastInSeries: Boolean,
    modifier: Modifier = Modifier,
    chatAvatarUrl: String? = null,
    onFailClick: () -> Unit = {},
    onMessageLongClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onLinkClick: (String) -> Unit = {},
) {
    val messageBackground =
        if (message.messageDetails.isMine) Theme.colorScheme.background.surfaceLow
        else Theme.colorScheme.brand.brandVariant

    val maxRadius = Theme.radius.md

    val messageShape = if (message.messageDetails.isMine && isMarkedLastInSeries)
        RoundedCornerShape(
            topStart = maxRadius,
            topEnd = maxRadius,
            bottomStart = maxRadius,
            bottomEnd = Theme.radius.xxs
        )
    else if (!message.messageDetails.isMine && isMarkedLastInSeries)
        RoundedCornerShape(
            topStart = maxRadius,
            topEnd = maxRadius,
            bottomStart = Theme.radius.xxs,
            bottomEnd = maxRadius
        )
    else
        RoundedCornerShape(size = maxRadius)

    val avatarSize = 24.dp
    val avatarSpacing = Theme.spacing._8
    val myMessageMarginStart = Theme.spacing._24
    val otherMessageMarginEnd = Theme.spacing._8
    val messageInfoAlignment = if (message.messageDetails.isMine) Alignment.Start else Alignment.End

    val messageBubblePaddingStart = if (message.messageDetails.isMine) myMessageMarginStart else 0.dp
    val messageBubblePaddingEnd = if (message.messageDetails.isMine) 0.dp else otherMessageMarginEnd

    val infoRowPaddingStart = if (message.messageDetails.isMine) {
        myMessageMarginStart
    } else {
        avatarSize + avatarSpacing
    }
    val infoRowPaddingEnd = if (message.messageDetails.isMine) 0.dp else otherMessageMarginEnd

    val messageAlignment = if (message.messageDetails.isMine) Alignment.End else Alignment.Start

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = if (message.messageDetails.isMine) Alignment.CenterEnd else Alignment.CenterStart
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._2),
            horizontalAlignment = messageAlignment
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(avatarSpacing)
            ) {
                if (!message.messageDetails.isMine) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(avatarSize),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isMarkedLastInSeries) {
                            CircularAvatar(
                                contactImageUri = chatAvatarUrl,
                                size = 24.dp,
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .padding(start = messageBubblePaddingStart, end = messageBubblePaddingEnd)
                        .clip(messageShape)
                        .sizeIn(minWidth = 56.dp, minHeight = 30.dp)
                        .combinedClickable(
                            onClick = { if (!containsUrl(message.text)) onMessageClick() },
                            onLongClick = onMessageLongClick
                        )
                        .background(color = messageBackground, shape = messageShape)
                        .padding(
                            horizontal = Theme.spacing._8,
                            vertical = Theme.spacing._4
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    if (containsUrl(message.text)) {
                        val styledText = detectAndStyleUrls(text = message.text, linkColor = Theme.colorScheme.brand.brand)
                        ClickableUrlText(
                            text = styledText,
                            style = Theme.typography.body.small,
                            color = Theme.colorScheme.shadeSecondary,
                            onUrlClick = onLinkClick,
                            onTextClick = onMessageClick
                        )
                    } else {
                        Text(
                            text = message.text,
                            style = Theme.typography.body.small,
                            color = Theme.colorScheme.shadeSecondary
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.align(messageInfoAlignment)
                    .padding(start = infoRowPaddingStart, end = infoRowPaddingEnd),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!message.messageDetails.isMine && message.messageDetails.reactions.isNotEmpty()) {
                    ReactionBubble(
                        reactions = message.messageDetails.reactions,
                        modifier = Modifier.offset(y = (-8).dp)
                    )
                }

                AnimatedVisibility(visible = showMessageInfo) {
                    MessageInfo(
                        messageTime = message.messageDetails.sendTime,
                        messageStatus = message.messageDetails.status,
                        messageIsMine = message.messageDetails.isMine,
                        onFailClick = onFailClick,
                    )
                }

                if (message.messageDetails.isMine && message.messageDetails.reactions.isNotEmpty()) {
                    ReactionBubble(
                        reactions = message.messageDetails.reactions,
                        modifier = Modifier.offset(y = (-8).dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextMessageLayout(
                message = TextMessageUiState(
                    text = "Good Morning!",
                    messageDetails = MessageDetailsUiState(
                        Uuid.random(),
                        Uuid.random(),
                        sendTime = LocalDateTime.now(),
                        status = MessageStatus.READ,
                        isMine = false,
                        reactions = listOf(MessageReaction("❤️", Uuid.random(), Uuid.random())),
                    )
                ),
                showMessageInfo = true,
                isMarkedLastInSeries = true,
            )

            TextMessageLayout(
                message = TextMessageUiState(
                    text = "Good Morning!",
                    messageDetails = MessageDetailsUiState(
                        Uuid.random(),
                        Uuid.random(),
                        sendTime = LocalDateTime.now(),
                        status = MessageStatus.READ,
                        isMine = true,
                    )
                ),
                showMessageInfo = true,
                isMarkedLastInSeries = true,
            )
        }
    }
}