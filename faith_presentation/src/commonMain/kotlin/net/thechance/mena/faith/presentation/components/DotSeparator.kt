package net.thechance.mena.faith.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DotSeparator(
    modifier: Modifier = Modifier,
    size: Int = 3,
    color: Color = Theme.colorScheme.shadeTertiary
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .background(
                color = color,
                shape = RoundedCornerShape(Theme.radius.full)
            )
    )
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            DotSeparator()
        }
    }
}
