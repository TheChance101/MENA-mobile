package net.thechance.mena.dukan.presentation.screen.createDukan.component.dukanstyle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun GridItemStyle(
    cartBackgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth()
                .clip(SquircleShape(Theme.radius.xs))
                .background(Theme.colorScheme.background.surface)
        ) {
            DukanImageItemPlaceholder(
                modifier = Modifier
                    .height(42.dp)
                    .padding(Theme.spacing._2)
                    .fillMaxWidth()
                    .clip(SquircleShape(Theme.radius.xxs))
                    .background(Theme.colorScheme.background.surfaceLow)
                    .align(Alignment.TopCenter)
                    .padding(Theme.spacing._8)
            )
        }
        ShoppingCartPlaceholder(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 22.dp)
                .clip(CircleShape)
                .background(cartBackgroundColor)
        )
    }
}

@Preview
@Composable
private fun VerticalItemStylePreview() {
    MenaTheme {
        GridItemStyle(
            cartBackgroundColor = Theme.colorScheme.background.surfaceHigh,
        )
    }
}