package net.thechance.mena.dukan.presentation.screen.createDukan.component.dukanstyle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
fun ListItemStyle(
    cartBackgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(36.dp)
            .clip(SquircleShape(Theme.radius.xs))
            .background(Theme.colorScheme.background.surface)
            .padding(Theme.spacing._2)
    ) {
        DukanImageItemPlaceholder(
            modifier = Modifier.clip(SquircleShape(Theme.radius.xxs))
                .background(Theme.colorScheme.background.surfaceLow)
                .padding(Theme.spacing._4)
        )
        Spacer(Modifier.weight(1f))
        ShoppingCartPlaceholder(
            modifier = Modifier.align(Alignment.Bottom)
                .clip(CircleShape)
                .background(cartBackgroundColor)
        )
    }
}

@Preview
@Composable
private fun HorizontalItemStylePreview() {
    MenaTheme {
        ListItemStyle(
            cartBackgroundColor = Theme.colorScheme.background.surfaceHigh,
        )
    }
}