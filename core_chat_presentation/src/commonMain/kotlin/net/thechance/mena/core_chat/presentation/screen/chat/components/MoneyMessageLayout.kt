package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_coin
import mena.core_chat_presentation.generated.resources.mine_send_message
import mena.core_chat_presentation.generated.resources.mine_wallet
import mena.core_chat_presentation.generated.resources.other_send_message
import mena.core_chat_presentation.generated.resources.other_wallet
import mena.core_chat_presentation.generated.resources.to
import net.thechance.mena.core_chat.domain.entity.MessageReaction
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.presentation.screen.chat.MessageDetailsUiState
import net.thechance.mena.core_chat.presentation.screen.chat.MoneyMessageUiState
import net.thechance.mena.core_chat.presentation.screen.contacts.components.CircularAvatar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun MoneyMessageLayout(
    chatName: String,
    message: MoneyMessageUiState,
    showMessageInfo: Boolean,
    isMarkedLastInSeries: Boolean,
    onMessageClick: () -> Unit = {},
    onFailClick: () -> Unit = {},
    onMessageLongClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    chatAvatarUrl: String? = null,
) {
    val amount = message.amount
    val messageDetails = message.messageDetails

    val messageBackground = if (messageDetails.isMine) Theme.colorScheme.background.surfaceLow else
        Theme.colorScheme.brand.brandVariant

    val maxRadius = Theme.radius.md
    val messageShape = if (messageDetails.isMine && isMarkedLastInSeries)
        RoundedCornerShape(
            topStart = maxRadius,
            topEnd = maxRadius,
            bottomStart = maxRadius,
            bottomEnd = Theme.radius.xxs
        )
    else if (!messageDetails.isMine && isMarkedLastInSeries)
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
    val messageInfoAlignment = if (messageDetails.isMine) Alignment.Start else Alignment.End

    val messageBubblePaddingStart = if (messageDetails.isMine) myMessageMarginStart else 0.dp
    val messageBubblePaddingEnd = if (messageDetails.isMine) 0.dp else otherMessageMarginEnd

    val infoRowPaddingStart = if (messageDetails.isMine) {
        myMessageMarginStart
    } else {
        avatarSize + avatarSpacing
    }
    val infoRowPaddingEnd = if (messageDetails.isMine) 0.dp else otherMessageMarginEnd

    val messageAlignment = if (messageDetails.isMine) Alignment.End else Alignment.Start

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = if (messageDetails.isMine) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._2),
            horizontalAlignment = messageAlignment
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(avatarSpacing)
            ) {
                if (!messageDetails.isMine) {
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
                        .sizeIn(minWidth = 120.dp)
                        .combinedClickable(
                            onClick = onMessageClick,
                            onLongClick = onMessageLongClick
                        )
                        .background(color = messageBackground, shape = messageShape)
                        .padding(
                            horizontal = Theme.spacing._8,
                            vertical = Theme.spacing._4
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (!messageDetails.isMine) {
                        MessageContent(amount, chatName, messageDetails)
                    } else {
                        MessageContent(amount, chatName, messageDetails)
                    }
                }
            }

            Row(
                modifier = Modifier.align(messageInfoAlignment)
                    .padding(start = infoRowPaddingStart, end = infoRowPaddingEnd),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!messageDetails.isMine && messageDetails.reactions.isNotEmpty()) {
                    ReactionBubble(
                        reactions = messageDetails.reactions,
                        modifier = Modifier.offset(y = (-8).dp)
                    )
                }

                AnimatedVisibility(visible = showMessageInfo) {
                    MessageInfo(
                        messageTime = messageDetails.sendTime,
                        messageStatus = messageDetails.status,
                        messageIsMine = messageDetails.isMine,
                        onFailClick = onFailClick,
                    )
                }

                if (messageDetails.isMine && messageDetails.reactions.isNotEmpty()) {
                    ReactionBubble(
                        reactions = messageDetails.reactions,
                        modifier = Modifier.offset(y = (-8).dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageContent(
    amount: Double,
    chatName: String,
    messageDetailsUiState: MessageDetailsUiState
) {
    val sendMessage =
        if (messageDetailsUiState.isMine) Res.string.mine_send_message else Res.string.other_send_message
    val walletMessage =
        if (messageDetailsUiState.isMine) Res.string.mine_wallet else Res.string.other_wallet

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
    ) {
        if (!messageDetailsUiState.isMine) {
            Text(
                text = chatName,
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadeSecondary
            )
        }
        MessageText(sendMessage)

        Text(
            text = "$amount",
            style = Theme.typography.label.large,
            color = Theme.colorScheme.shadePrimary
        )

        Icon(
            painter = painterResource(Res.drawable.ic_coin),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        MessageText(Res.string.to)
        MessageText(walletMessage)
        if (messageDetailsUiState.isMine) {
            Text(
                text = chatName,
                style = Theme.typography.body.small,
                color = Theme.colorScheme.shadeSecondary
            )
        }
    }
}

@Composable
private fun MessageText(content: StringResource) {
    Text(
        text = stringResource(content),
        style = Theme.typography.body.small,
        color = Theme.colorScheme.shadeSecondary
    )
}

@OptIn(ExperimentalUuidApi::class)
@Preview
@Composable
private fun MoneyMessageLayoutPreview() {
    MenaTheme {
        Column(
            modifier = Modifier.fillMaxWidth(0.88f),
            verticalArrangement = Arrangement.Center
        ) {
            MoneyMessageLayout(
                chatName = "The princess\uD83D\uDC51 \uD83D\uDC78\uD83C\uDFFC",
                message = MoneyMessageUiState(
                    amount = 11.522,
                    messageDetails = MessageDetailsUiState(
                        id = Uuid.random(),
                        chatId = Uuid.random(),
                        status = MessageStatus.READ,
                        isMine = true,
                        reactions = listOf(MessageReaction("❤️", Uuid.random(), Uuid.random()))
                    ),
                ),
                showMessageInfo = true,
                isMarkedLastInSeries = true,
            )
        }
    }
}