package net.thechance.mena.faith.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.bookmark
import mena.faith_presentation.generated.resources.remove_bookmark_icon
import mena.faith_presentation.generated.resources.swipe_animation
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt


@Composable
fun SwappableCard(
    id: Int,
    onClick: () -> Unit,
    currentSwipedCardId: Int,
    onSwipeStateChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    backgroundIcon: Painter = painterResource(Res.drawable.bookmark),
    contentDescription: String = stringResource(Res.string.remove_bookmark_icon),
    swipeThreshold: Float = 130f,
    cardContent: @Composable (Modifier) -> Unit,
) {
    var rawOffsetX by remember(currentSwipedCardId, id) {
        mutableFloatStateOf(if (currentSwipedCardId == id) -swipeThreshold else 0f)
    }

    val isCurrentCardSwiped = currentSwipedCardId == id

    LaunchedEffect(isCurrentCardSwiped) {
        rawOffsetX = if (isCurrentCardSwiped) -swipeThreshold else 0f
    }

    val animatedOffsetX by animateFloatAsState(
        targetValue = rawOffsetX,
        animationSpec = tween(durationMillis = 300),
        label = stringResource(Res.string.swipe_animation)
    )

    Box(modifier.fillMaxWidth()) {
        AnimatedVisibility(
            visible = animatedOffsetX < 0f,
            enter = fadeIn(tween()),
            exit = fadeOut(tween()),
            modifier = Modifier
                .matchParentSize()
                .padding(vertical = Theme.spacing._4)
        ) {
            SwipeBackground(
                painter = backgroundIcon,
                contentDescription = contentDescription,
                onClick = {
                    onSwipeStateChange(-1)
                    onClick()
                }
            )
        }
        cardContent(
            Modifier
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .pointerInput(id) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (rawOffsetX <= -swipeThreshold) {
                                onSwipeStateChange(id)
                                rawOffsetX = -swipeThreshold
                            } else {
                                onSwipeStateChange(-1)
                                rawOffsetX = 0f
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
private fun SwipeBackground(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(Res.drawable.bookmark),
    contentDescription: String = stringResource(Res.string.remove_bookmark_icon),
    tintColor: Color = Theme.colorScheme.error,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.bgError)
            .clickable(onClick = onClick)
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = tintColor,
            modifier = modifier
                .padding(horizontal = Theme.spacing._12)
                .align(Alignment.CenterEnd)
        )
    }
}
