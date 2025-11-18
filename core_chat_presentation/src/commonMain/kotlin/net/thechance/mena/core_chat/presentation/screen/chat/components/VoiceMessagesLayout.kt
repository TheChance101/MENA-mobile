@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.core_chat.presentation.screen.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import mena.core_chat_presentation.generated.resources.ic_play
import mena.core_chat_presentation.generated.resources.ic_profile_placeholder
import net.thechance.mena.core_chat.domain.entity.AudioData
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.presentation.components.CustomInfiniteCircularLoader
import net.thechance.mena.core_chat.presentation.screen.chat.AudioMessageUiState
import net.thechance.mena.core_chat.presentation.screen.chat.MessageDetailsUiState
import net.thechance.mena.core_chat.presentation.utils.now
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun VoiceMessageLayout(
    message: AudioMessageUiState,
    showMessageInfo: Boolean,
    isMarkedLastInSeries: Boolean,
    modifier: Modifier = Modifier,
    isMessageLoading: Boolean = false,
    progress: Float = 0f,
    totalSeconds: Long = 0,
    waveformData: List<Float> = emptyList(),
    chatAvatarUrl: String? = null,
    onMessageClick: () -> Unit = {},
    onMessageLongClick: () -> Unit = {},
    onPlayClick: (AudioMessageUiState) -> Unit = {},
    onFailClick: (AudioMessageUiState) -> Unit = {},
) {
    val messageBackground =
        if (message.messageDetails.isMine) Theme.colorScheme.background.surfaceLow
        else Theme.colorScheme.brand.brandVariant


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
    val messageAlignment = if (message.messageDetails.isMine) Alignment.End else Alignment.Start


    Column(
        modifier = modifier.padding( end = messagePaddingEnd),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._2),
        horizontalAlignment = messageAlignment
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
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
            Box(
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
                ) {
                    PLayButton(
                        onClick = { onPlayClick(message) },
                        isLoading = isMessageLoading
                    )

                    VoiceMessageWaveform(
                        waveData = waveformData.ifEmpty { generateRandomWaveformData() },
                        progress = progress,
                        modifier = Modifier.weight(1f).height(44.dp)
                            .padding(vertical = Theme.spacing._4)
                    )

                    Text(
                        text = formatTime(totalSeconds),
                        style = Theme.typography.label.extraSmall,
                        color = Theme.colorScheme.shadePrimary
                    )
                }
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
                    modifier= Modifier.offset(y = (-8).dp)
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
                    modifier= Modifier.offset(y = (-8).dp)
                )
            }
        }
    }
}

fun generateRandomWaveformData(size: Int = 50): List<Float> {
    return List(size) { Random.nextFloat() * 0.8f + 0.2f }
}

@Composable
fun PLayButton(
    onClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        containerColor = Theme.colorScheme.primary.primary,
        contentColor = Theme.colorScheme.primary.onPrimary,
        shape = RoundedCornerShape(Theme.radius.full),
        modifier = modifier.size(32.dp)
    ) {
        if (isLoading) {
            CustomInfiniteCircularLoader(
                modifier = Modifier.size(18.dp),
                color = Theme.colorScheme.primary.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                painter = painterResource(Res.drawable.ic_play),
                tint = it,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview
@Composable
fun VoiceMessagesLayoutPreview() {
    MenaTheme {
        VoiceMessageLayout(
            message = AudioMessageUiState(
                messageDetails = MessageDetailsUiState(
                    sendTime = LocalDateTime.now(),
                    status = MessageStatus.SENT,
                    isMine = true,
                    isVisibleMessageInfo = true,
                    isLastInSeries = true
                ),
                data = AudioData.AudioUrl(""),
                isPlaying = false,
                isLoading = false,
                progress = 0.5f,
                duration = 5,
                waveformData = generateRandomWaveformData(),
            ),
            showMessageInfo = false,
            isMarkedLastInSeries = false
        )
    }
}