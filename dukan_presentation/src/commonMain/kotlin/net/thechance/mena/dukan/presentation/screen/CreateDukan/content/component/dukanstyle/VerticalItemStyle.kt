package net.thechance.mena.dukan.presentation.screen.CreateDukan.content.component

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
    modifier: Modifier = Modifier,
    addToCartBackgroundColor: Color
) {
    Box(
        modifier = modifier.fillMaxWidth().height(64.dp)
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
            DukanItemPlaceholder(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                contentPadding = 9.dp
            )
        }
        AddToCartIcon(
            addToCartBackgroundColor = addToCartBackgroundColor,
            modifier = Modifier.padding(top = 24.dp).align(Alignment.Center)
        )
    }
}

@Preview
@Composable
private fun VerticalItemStylePreview() {
    MenaTheme {
        VerticalItemStyle(
            addToCartBackgroundColor = Theme.colorScheme.background.surfaceHigh,
        )
    }
}