package net.thechance.mena.dukan.presentation.screen.createDukan.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
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
fun ImageCroppingBox(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Theme.colorScheme.background.surfaceLow,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Theme.radius.lg))
                .background(backgroundColor)
                .wrapContentSize(),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

@Preview
@Composable
private fun ImageCroppingBoxPreview() {
    MenaTheme {
        ImageCroppingBox(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Red))
        }
    }
}