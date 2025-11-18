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
import mena.core_chat_presentation.generated.resources.Res
import mena.core_chat_presentation.generated.resources.aya
import net.thechance.mena.core_chat.domain.entity.MessageReaction
import net.thechance.mena.core_chat.domain.entity.MessageStatus
import net.thechance.mena.core_chat.presentation.designSystem.theme.QuranTheme
import net.thechance.mena.core_chat.presentation.designSystem.theme.quran
import net.thechance.mena.core_chat.presentation.screen.chat.AyahMessageUiState
import net.thechance.mena.core_chat.presentation.screen.chat.MessageDetailsUiState
import net.thechance.mena.core_chat.presentation.screen.contacts.components.CircularAvatar
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun AyahMessageLayout(
    onFailClick: () -> Unit = {},
    onMessageLongClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    message: AyahMessageUiState,
    showMessageInfo: Boolean,
    isMarkedLastInSeries: Boolean,
    modifier: Modifier = Modifier,
    chatAvatarUrl: String? = null,
) {
    val surahName = message.surahName
    val messageDetails = message.messageDetails

    val messageBackground =
        if (messageDetails.isMine) Theme.colorScheme.background.surfaceLow
        else Theme.colorScheme.brand.brandVariant

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
                        .padding(
                            start = messageBubblePaddingStart,
                            end = messageBubblePaddingEnd
                        )
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
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = surahName,
                                style = Theme.typography.label.small,
                                color = Theme.colorScheme.shadePrimary
                            )
                            Text(
                                text = "•",
                                style = Theme.typography.body.small,
                                color = Theme.colorScheme.shadeTertiary
                            )
                            Text(
                                text = stringResource(Res.string.aya, message.ayahNumber),
                                style = Theme.typography.label.small,
                                color = Theme.colorScheme.shadePrimary
                            )
                        }
                        QuranTheme {
                            Text(
                                text = message.ayahContent,
                                style = Theme.typography.quran.medium,
                                color = Theme.colorScheme.shadeSecondary,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

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

@Preview
@Composable
private fun AyahMessageLayoutPreview() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AyahMessageLayout(
            message = AyahMessageUiState(
                surahId = 4,
                ayahContent = "يَسْتَفْتُونَكَ قُلِ اللَّهُ يُفْتِيكُمْ فِي الْكَلَالَةِ ۚ إِنِ امْرُؤٌ " +
                        "هَلَكَ لَيْسَ لَهُ وَلَدٌ وَلَهُ أُخْتٌ فَلَهَا نِصْفُ مَا تَرَكَ ۚ وَهُوَ يَرِثُهَا إِن لَّمْ يَكُن لَّهَا وَلَدٌ ۚ فَإِن كَانَتَا اثْنَتَيْنِ فَلَهُمَا الثُّلُثَانِ مِمَّا تَرَكَ ۚ وَإِن كَانُوا إِخْوَةً رِّجَالًاوَنِسَاءً فَلِلذَّكَرِ مِثْلُ حَظِّ الْأُنْثَيَيْنِ ۗ يُبَيِّنُ اللَّهُ لَكُمْ أَنْ تَضِلُّوا ۗ وَاللَّهُ بِكُلِّ شَيْءٍ عَلِيمٌ",
                ayahNumber = 176,
                surahName = "An-Nisa",
                messageDetails = MessageDetailsUiState(
                    id = Uuid.random(),
                    chatId = Uuid.random(),
                    status = MessageStatus.READ,
                    isMine = false,
                    reactions = listOf(MessageReaction("❤️", Uuid.random(), Uuid.random()))
                )
            ),
            showMessageInfo = true,
            isMarkedLastInSeries = true,
        )
    }
}