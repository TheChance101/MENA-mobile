package net.thechance.mena.designsystem.presentation.component.progressBar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun ProgressBar(
    modifier: Modifier = Modifier,
    color: Color = Theme.colorScheme.primary.primary,
    trackColor: Color = Theme.colorScheme.background.surfaceHigh,
    strokeCap: StrokeCap = StrokeCap.Round,
    gapSize: Dp = 4.dp,
) {
    LinearProgressIndicator(
        modifier = modifier,
        trackColor = trackColor,
        color = color,
        strokeCap = strokeCap,
        gapSize = gapSize,
    )
}

@Composable
fun ProgressBar(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = Theme.colorScheme.primary.primary,
    trackColor: Color = Theme.colorScheme.background.surfaceHigh,
    strokeCap: StrokeCap = StrokeCap.Round,
    gapSize: Dp = 4.dp,
) {
    LinearProgressIndicator(
        modifier = modifier,
        trackColor = trackColor,
        color = color,
        strokeCap = strokeCap,
        gapSize = gapSize,
        progress = progress
    )
}

@Preview(showBackground = true,backgroundColor = 0xFFF2F4F7)
@Composable
private fun ProgressBarPreview() {
    MenaTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxHeight(0.3f)
                .fillMaxWidth()
                .background(Theme.colorScheme.primary.onPrimary)
        ) {
            ProgressBar()
        }
    }
}