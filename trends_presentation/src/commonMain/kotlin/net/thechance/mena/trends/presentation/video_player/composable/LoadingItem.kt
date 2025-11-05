package net.thechance.mena.trends.presentation.video_player.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun LoadingItem(
    modifier: Modifier = Modifier,
    dotsSize: Dp = 7.dp
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        DotsProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            dotSize = dotsSize,
            colors = listOf(
                Theme.colorScheme.stroke,
                Theme.colorScheme.shadeTertiary,
                Theme.colorScheme.shadeTertiary
            )
        )
    }
}