package net.thechance.mena.dukan.presentation.screen.CreateDukan.content.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
fun HorizontalItemStyle(
    modifier: Modifier = Modifier,
    addToCartBackgroundColor: Color
) {
    Row(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(Theme.radius.xs))
            .background(Theme.colorScheme.background.surface)
            .padding(Theme.spacing._2)
    ) {
        DukanItemPlaceholder(
            contentPadding = Theme.spacing._4
        )
        Spacer(Modifier.weight(1f))
        AddToCartIcon(addToCartBackgroundColor =addToCartBackgroundColor , modifier = Modifier.align(Alignment.Bottom).size(16.dp))
    }
}

@Preview
@Composable
private fun HorizontalItemStylePreview() {
    MenaTheme {
        HorizontalItemStyle(
            addToCartBackgroundColor = Theme.colorScheme.background.surfaceHigh,
        )
    }
}