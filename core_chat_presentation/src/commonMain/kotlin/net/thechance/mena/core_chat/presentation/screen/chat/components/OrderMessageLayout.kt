package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.ic_coin
import mena.core_chat_presentation.generated.resources.ic_package
import mena.core_chat_presentation.generated.resources.ic_profile_placeholder
import mena.core_chat_presentation.generated.resources.new_order
import mena.core_chat_presentation.generated.resources.number_of_items
import mena.core_chat_presentation.generated.resources.to
import mena.core_chat_presentation.generated.resources.total_price
import mena.core_chat_presentation.generated.resources.view_order_details
import net.thechance.mena.core_chat.presentation.screen.chat.MessageDetailsUiState
import net.thechance.mena.core_chat.presentation.screen.chat.MessageUiState
import net.thechance.mena.core_chat.presentation.screen.chat.OrderMessageUiState
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun OrderMessageLayout(
    message: OrderMessageUiState,
    showMessageInfo: Boolean,
    isMarkedLastInSeries: Boolean,
    onViewOrderDetailsClick: () -> Unit,
    modifier: Modifier = Modifier,
    chatAvatarUrl: String? = null,
    onMessageClick: () -> Unit = {},
    onMessageLongClick: () -> Unit = {},
    onFailClick: (OrderMessageUiState) -> Unit = {},
) {

    val messageBackground =
        if (message.messageDetails.isMine) Theme.colorScheme.background.surfaceLow
        else Theme.colorScheme.brand.brandVariant

    val messagePaddingStart = if (message.messageDetails.isMine)
        Theme.spacing._24
    else
        Theme.spacing._8

    val messagePaddingEnd = if (message.messageDetails.isMine) 0.dp else Theme.spacing._8

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
    else RoundedCornerShape(size = maxRadius)

    val messageInfoAlignment = if (message.messageDetails.isMine) Alignment.Start else Alignment.End

    Column(
        modifier = modifier.padding(start = messagePaddingStart, end = messagePaddingEnd),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._2),
        horizontalAlignment = messageInfoAlignment
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            if (!message.messageDetails.isMine) {
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

            Column(
                modifier = Modifier
                    .clip(messageShape)
                    .background(color = messageBackground, shape = messageShape)
                    .combinedClickable(
                        onClick = onMessageClick,
                        onLongClick = onMessageLongClick
                    )
                    .padding(
                        horizontal = Theme.spacing._8,
                        vertical = Theme.spacing._4
                    )
            ) {

                OrderDetails(
                    orderNumber = message.orderId.toString().take(8),
                    numberOfItems = message.numberOfItems.toString(),
                    deliverTo = message.deliverTo,
                    totalPrice = message.totalPrice.toString()
                )

                PrimaryButton(
                    text = stringResource(Res.string.view_order_details),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onViewOrderDetailsClick,
                    trailingIcon = painterResource(Res.drawable.ic_package)
                )
            }
        }

        Row(
            modifier = Modifier.align(messageInfoAlignment),
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
                    onFailClick = { onFailClick(message) }
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

@Composable
private fun OrderDetails(
    orderNumber: String,
    numberOfItems: String,
    deliverTo: String,
    totalPrice: String
) {
    val colon = ": "
    val newOrder = stringResource(Res.string.new_order) + colon
    val numberOfItems = stringResource(Res.string.number_of_items) + colon + numberOfItems
    val location = stringResource(Res.string.to) + colon + deliverTo
    val totalPrice = stringResource(Res.string.total_price) + colon + totalPrice

    Row {
        OrderInfo(text = newOrder)
        OrderInfo(
            text = "#$orderNumber",
            style = Theme.typography.label.medium,
            color = Theme.colorScheme.shadeSecondary
        )
    }
    OrderInfo(text = numberOfItems)
    OrderInfo(text = location)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._2),
        modifier = Modifier.padding(top = Theme.spacing._2, bottom = Theme.spacing._8)
    ) {
        OrderInfo(text = totalPrice)
        Image(
            painter = painterResource(Res.drawable.ic_coin),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun OrderInfo(
    text: String,
    style: TextStyle = Theme.typography.body.small,
    color: Color = Theme.colorScheme.shadeSecondary
) {
    Text(
        text = text,
        style = style,
        color = color
    )
}

@OptIn(ExperimentalUuidApi::class)
@Composable
@Preview
private fun DukanOrderMessageLayoutPreview() {
    MenaTheme {
        OrderMessageLayout(
            message = OrderMessageUiState(
                orderId = Uuid.random(),
                numberOfItems = 3,
                deliverTo = "Egypt, Cairo, Cairo",
                totalPrice = 12452.65,
                messageDetails = MessageDetailsUiState()
            ),
            showMessageInfo = true,
            isMarkedLastInSeries = true,
            onViewOrderDetailsClick = { }
        )
    }
}