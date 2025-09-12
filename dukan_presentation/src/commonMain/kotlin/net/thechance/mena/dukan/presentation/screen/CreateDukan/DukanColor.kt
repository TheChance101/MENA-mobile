package net.thechance.mena.dukan.presentation.screen.createDukan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
fun DukanColor(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = modifier.size(48.dp).clip(RoundedCornerShape(Theme.radius.full))
            .background(Theme.colorScheme.background.surface)
            .border(1.dp, backgroundColor, RoundedCornerShape(Theme.radius.full))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(if (isSelected) 42.dp else 48.dp).clip(RoundedCornerShape(Theme.radius.full))
                .background(backgroundColor)
        )
    }
}

@Preview
@Composable
private fun DukanColorPreview() {
    MenaTheme {
        Column {
            DukanColor(
                backgroundColor = Theme.colorScheme.error,
                onClick = {},
                isSelected = true
            )

            DukanColor(
                backgroundColor = Theme.colorScheme.error,
                onClick = {},
                isSelected = false
            )
        }
    }
}
