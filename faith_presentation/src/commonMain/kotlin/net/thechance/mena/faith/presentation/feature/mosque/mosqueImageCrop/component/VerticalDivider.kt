package net.thechance.mena.faith.presentation.feature.mosque.mosqueImageCrop.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun VerticalDivider(
    color: Color,
    thickness: Dp,
    height: Dp = 20.dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(thickness)
            .height(height)
            .background(color)
    )
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            VerticalDivider(
                color = Color.Gray,
                thickness = 2.dp,
                height = 40.dp
            )
        }
    }
}
