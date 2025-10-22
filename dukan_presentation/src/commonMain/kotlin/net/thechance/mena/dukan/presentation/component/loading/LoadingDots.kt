package net.thechance.mena.dukan.presentation.component.loading


import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoadingDots(
    modifier: Modifier = Modifier,
    dotSize: Dp = 8.dp,
    spacing: Dp = 3.dp,
) {
    val dotColors = listOf(
        Theme.colorScheme.shadeTertiary,
        Theme.colorScheme.shadeSecondary,
        Theme.colorScheme.shadePrimary,
    )
    val infiniteTransition = rememberInfiniteTransition("colorRotation")
    val colorCount = dotColors.size

    val colorOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = colorCount.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "colorOffset"
    )
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val offset = colorOffset.toInt()
        for (i in 0 until colorCount) {
            val colorIndex = (colorCount - offset + i) % colorCount
            val color = dotColors[colorIndex]
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .background(color = color, shape = CircleShape)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoadingDotsPreview() {
    MenaTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoadingDots()
        }

    }
}