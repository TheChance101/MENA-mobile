package net.thechance.mena.dukan.presentation.screen.createDukan.content.component.dukanstyle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun VerticalItemStyle(
    cartBackgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
            .height(64.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Theme.radius.xs))
                .background(Theme.colorScheme.background.surface)
                .padding(
                    bottom = 20.dp,
                    top = Theme.spacing._2,
                    start = Theme.spacing._2,
                    end = Theme.spacing._2
                ),
        ) {
            DukanImageItemPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Theme.radius.xxs))
                    .background(Theme.colorScheme.background.surfaceLow)
                    .align(Alignment.TopCenter)
                    .padding(Theme.spacing._4)
            )
        }
        ShoppingCartPlaceholder(
            modifier = Modifier.padding(top = 36.dp)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(Theme.radius.full))
                .background(cartBackgroundColor)
        )
    }
}

@Preview
@Composable
private fun VerticalItemStylePreview() {
    MenaTheme {
        VerticalItemStyle(
            cartBackgroundColor = Theme.colorScheme.background.surfaceHigh,
        )
    }
}