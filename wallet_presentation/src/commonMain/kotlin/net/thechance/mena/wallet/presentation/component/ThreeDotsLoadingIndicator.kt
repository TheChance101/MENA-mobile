package net.thechance.mena.wallet.presentation.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ThreeDotsLoadingIndicator(
    modifier: Modifier = Modifier,
    dotSize: Dp = 7.dp,
    spacing: Dp = 2.dp,
    animationDuration: Int = 1000,
    colors: List<Color> = listOf(
        Theme.colorScheme.stroke,
        Theme.colorScheme.shadeTertiary,
        Theme.colorScheme.shadeSecondary
    )
) {
    val infiniteTransition = rememberInfiniteTransition(label = "colorRotation")
    val colorCount = colors.size

    val colorOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = colorCount.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = animationDuration,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "colorOffset"
    )

    val currentColors by remember {
        derivedStateOf {
            val offset = colorOffset.toInt()
            List(colorCount) { index ->
                val colorIndex = (colorCount - offset + index) % colorCount
                colors[colorIndex]
            }
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        currentColors.forEachIndexed { index, color ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .background(color = color, shape = CircleShape)
            )
        }
    }
}

@Preview
@Composable
fun LoadingDotsPreview() {
    ThreeDotsLoadingIndicator(
        dotSize = 10.dp,
        spacing = 4.dp,
    )
}