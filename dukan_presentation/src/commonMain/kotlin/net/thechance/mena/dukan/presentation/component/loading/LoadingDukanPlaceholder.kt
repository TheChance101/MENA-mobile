package net.thechance.mena.dukan.presentation.component.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun LoadingDukanPlaceholder(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(156.dp)
            .clip(SquircleShape(Theme.radius.lg))
            .background(Theme.colorScheme.background.surfaceLow)
            .padding(Theme.spacing._8)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(SquircleShape(Theme.radius.lg))
                .background(Theme.colorScheme.background.surfaceHigh)
        )
    }
}

@Preview
@Composable
private fun LoadingDukanPlaceholderPreview() {
    MenaTheme {
        LoadingDukanPlaceholder()
    }
}
