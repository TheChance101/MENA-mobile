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
import net.thechance.mena.core_chat.domain.entity.ImageData
import net.thechance.mena.core_chat.domain.entity.MessageContent
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.presentation.screen.chat.MessageUiState
import net.thechance.mena.core_chat.presentation.utils.now
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun ImageMessagesLayout(
    messages: List<MessageUiState>,
    showMessageInfo: Boolean,
    isMarkedLastInSeries: Boolean,
    modifier: Modifier = Modifier,
    chatAvatarUrl: String? = null,
    onFailClick: (MessageUiState) -> Unit = {},
    onMessageImageClick: (List<MessageUiState>, Int) -> Unit,
) {
    val messageBackground =
        if (messages.last().isMine) Theme.colorScheme.background.surfaceLow
        else Theme.colorScheme.brand.brandVariant

    val messagePaddingStart = if (messages.last().isMine)
        Theme.spacing._24
    else
        Theme.spacing._8

    val messagePaddingEnd = if (messages.last().isMine) 0.dp else Theme.spacing._8
    val maxRadius = Theme.radius.lg

    val messageShape = if (messages.last().isMine && isMarkedLastInSeries)
        RoundedCornerShape(
            topStart = maxRadius,
            topEnd = maxRadius,
            bottomStart = maxRadius,
            bottomEnd = Theme.radius.xxs
        )
    else if (!messages.last().isMine && isMarkedLastInSeries)
        RoundedCornerShape(
            topStart = maxRadius,
            topEnd = maxRadius,
            bottomStart = Theme.radius.xxs,
            bottomEnd = maxRadius
        )
    else
        RoundedCornerShape(size = maxRadius)

    val messageInfoAlignment = if (messages.last().isMine)
        Alignment.Start
    else
        Alignment.End
    val messageAlignment = if (messages.last().isMine) Alignment.End else Alignment.Start

    val verticalPadding = Theme.spacing._4
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._2),
        horizontalAlignment = messageAlignment
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
        ) {
            if (!messages.last().isMine) {
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
                    .background(
                        color = messageBackground,
                        shape = messageShape
                    )
                    .padding(
                        horizontal = verticalPadding,
                        vertical = Theme.spacing._4
                    )
            ) {

                val imageDataList = messages.map { imageData ->
                    when (imageData.content) {
                        is MessageContent.Image -> {
                            val images = imageData.content.data
                                when (images) {
                                    is ImageData.ImageUrl -> images.url
                                    is ImageData.ImageByteArray -> images.byteArray
                                }

                        }
                        is MessageContent.Text -> return@Column
                    }
                }

                ImageMessageContent(
                    images = imageDataList,
                    modifier = Modifier.size(156.dp, 162.dp).clip(messageShape),
                    onImageClick = { index -> onMessageImageClick(messages, index) }
                )
            }

        }
        AnimatedVisibility(
            visible = showMessageInfo,
            modifier = Modifier.align(messageInfoAlignment)
        ) {
            MessageInfo(
                messageTime = messages.last().sendTime,
                messageStatus = messages.last().status,
                messageIsMine = messages.last().isMine,
                onFailClick = { onFailClick(messages.last()) },
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
            ImageMessagesLayout(
                messages = listOf(MessageUiState(
                    Uuid.random(),
                    Uuid.random(),
                    sendTime = LocalDateTime.now(),
                    status = MessageStatus.READ,
                    isMine = false,
                    content = MessageContent.Text("Good Morning!")
                )),
                showMessageInfo = true,
                isMarkedLastInSeries = true,
                onMessageImageClick = { message,index -> }
            )
        }
    }
}

