package net.thechance.mena.dukan.presentation.screen.productDetails.components.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import sv.lib.squircleshape.SquircleShape

@Composable
fun ShimmerBox(
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .background(
                color = Theme.colorScheme.shadeSecondary.copy(alpha = 0.3f),
                shape = SquircleShape(Theme.radius.xs)
            )
    )
}
