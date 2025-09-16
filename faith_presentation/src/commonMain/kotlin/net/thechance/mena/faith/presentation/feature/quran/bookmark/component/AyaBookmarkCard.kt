package net.thechance.mena.faith.presentation.feature.quran.bookmark.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.aya
import mena.faith_presentation.generated.resources.bookmark
import mena.faith_presentation.generated.resources.history
import mena.faith_presentation.generated.resources.remove_bookmark_icon
import mena.faith_presentation.generated.resources.swipe_animation
import mena.faith_presentation.generated.resources.time_icon
import net.thechance.mena.designsystem.presentation.component.icon.MenaIcon
import net.thechance.mena.designsystem.presentation.component.text.MenaText
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.quran
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Composable
fun AyaBookmarkCard(
    id: Int,
    surahName: String,
    ayaNumber: Int,
    timeAgo: Instant,
    ayaText: String,
    isSwiped: Boolean,
    onSwipe: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    onRemoveBookmarkClick: () -> Unit
) {
    SwipeableCard(
        id = id,
        isSwiped = isSwiped,
        onSwipe = onSwipe,
        backgroundContent = {
            RemoveBookmarkBackground(onRemoveBookmarkClick = {
                onSwipe(null)
                onRemoveBookmarkClick()
            })
        },
        cardContent = { contentModifier ->
            AyaBookmarkContent(
                surahName = surahName,
                ayaNumber = ayaNumber,
                createdAt = timeAgo.toString(),
                ayaText = ayaText,
                modifier = contentModifier
            )
        },
        modifier = modifier
    )
}

@Composable
fun SwipeableCard(
    id: Int,
    isSwiped: Boolean,
    swipeThreshold: Float = 130f,
    onSwipe: (Int?) -> Unit,
    backgroundContent: @Composable () -> Unit,
    cardContent: @Composable (Modifier) -> Unit,
    modifier: Modifier
) {
    var rawOffsetX by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isSwiped) {
        rawOffsetX = if (isSwiped) -swipeThreshold else 0f
    }

    val animatedOffsetX by animateFloatAsState(
        targetValue = rawOffsetX,
        animationSpec = tween(durationMillis = 300),
        label = stringResource(Res.string.swipe_animation)
    )

    Box(modifier.fillMaxWidth()) {
        AnimatedVisibility(
            visible = animatedOffsetX < 0f,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300)),
            modifier = Modifier.matchParentSize()
        ) {
            backgroundContent()
        }
        cardContent(
            Modifier
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            rawOffsetX = if (rawOffsetX <= -swipeThreshold) {
                                onSwipe(id)
                                -swipeThreshold
                            } else {
                                onSwipe(null)
                                0f
                            }
                        }
                    ) { _, dragAmount ->
                        rawOffsetX = (rawOffsetX + dragAmount).coerceIn(-swipeThreshold, 0f)
                    }
                }
        )
    }
}

@Composable
private fun RemoveBookmarkBackground(onRemoveBookmarkClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.bgError)
            .clickable { onRemoveBookmarkClick() }
    ) {
        MenaIcon(
            painter = painterResource(Res.drawable.bookmark),
            contentDescription = stringResource(Res.string.remove_bookmark_icon),
            tint = Theme.colorScheme.error,
            modifier = Modifier
                .padding(horizontal = Theme.spacing._12)
                .align(Alignment.CenterEnd)
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun AyaBookmarkContent(
    surahName: String,
    ayaNumber: Int,
    createdAt: String,
    ayaText: String,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Theme.colorScheme.background.surfaceLow,
                shape = RoundedCornerShape(Theme.radius.md)
            )
    ) {
        Column(
            modifier = Modifier.padding(Theme.spacing._12),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
        ) {
            BookmarkHeader(
                surahName = surahName,
                ayaNumber = ayaNumber,
                createdAt = createdAt
            )

            BookmarkAyaText(ayaText = ayaText)
        }
    }
}

@Composable
private fun BookmarkHeader(
    surahName: String,
    ayaNumber: Int,
    createdAt: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SurahAndAyaInfo(
            surahName = surahName,
            ayaNumber = ayaNumber
        )

        TimeInfo(createdAt = createdAt)
    }
}

@Composable
private fun SurahAndAyaInfo(
    surahName: String,
    ayaNumber: Int
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        MenaText(
            text = surahName,
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.label.medium,
        )

        DotSeparator()

        MenaText(
            text = stringResource(Res.string.aya, ayaNumber),
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.label.medium,
        )
    }
}

@Composable
private fun DotSeparator() {
    Box(
        modifier = Modifier
            .padding(horizontal = Theme.spacing._8)
            .size(3.dp)
            .background(
                color = Theme.colorScheme.shadeTertiary,
                shape = RoundedCornerShape(Theme.radius.full)
            )
    )
}

@Composable
private fun TimeInfo(createdAt: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        MenaIcon(
            painter = painterResource(Res.drawable.history),
            contentDescription = stringResource(Res.string.time_icon),
            tint = Theme.colorScheme.shadeSecondary,
        )
        MenaText(
            text = createdAt,
            color = Theme.colorScheme.shadeSecondary,
            modifier = Modifier.padding(start = Theme.spacing._2),
            style = Theme.typography.label.small
        )
    }
}

@Composable
private fun BookmarkAyaText(ayaText: String) {
    MenaText(
        text = ayaText,
        color = Theme.colorScheme.shadeSecondary,
        style = Theme.typography.quran.medium
    )
}

@OptIn(ExperimentalTime::class)
@Preview
@Composable
private fun AyahBookmarkCardPreview() {
    MenaTheme {
        AyaBookmarkCard(
            surahName = "Al-Maidah",
            ayaNumber = 3,
            timeAgo = Instant.DISTANT_PAST,
            ayaText = "حُرِّمَتْ عَلَيْكُمُ الْمَيْتَةُ وَالدَّمُ وَلَحْمُ الْخِنْزِيرِ وَمَا أُهِلَّ بِهِ لِغَيْرِ اللَّهِ وَالْمُنْخَنِقَةُ وَالْمَوْقُوذَةُ وَالْمُتَرَدِّيَةُ وَالنَّطِيحَةُ وَمَا أَكَلَ السَّبُعُ إِلَّا مَا ذَكَّيْتُمْ وَمَا ذُبِحَ عَلَى النُّصُبِ وَأَن تَسْتَقْسِمُوا بِالْأَزْلَامِ ۚ ذَٰلِكُمْ فِسْقٌ ۗ الْيَوْمَ يَئِسَ الَّذِينَ كَفَرُوا مِن دِينِكُمْ فَلَا تَخْشَوْهُمْ وَاخْشَوْنِ ۚ الْيَوْمَ أَكْمَلْتُ لَكُمْ دِينَكُمْ وَأَتْمَمْتُ عَلَيْكُمْ نِعْمَتِي وَرَضِيتُ لَكُمُ الْإِسْلَامَ دِينًا ۚ فَمَنِ اضْطُرَّ فِي مَخْمَصَةٍ غَيْرَ مُتَجَانِفٍ لِّإِثْمٍ فَإِنَّ اللَّهَ غَفُورٌ رَّحِيمٌ",
            onRemoveBookmarkClick = {},
            id = 1,
            onSwipe = {},
            isSwiped = false,
        )
    }
}
