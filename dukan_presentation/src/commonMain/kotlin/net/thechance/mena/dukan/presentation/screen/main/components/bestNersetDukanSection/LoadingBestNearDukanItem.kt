package net.thechance.mena.dukan.presentation.screen.main.components.bestNersetDukanSection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoadingBestNearDukanItem(
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(76.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Theme.colorScheme.background.surfaceHigh)
        )

        Box(
            modifier = Modifier
                .padding(top = Theme.spacing._4)
                .width(56.dp)
                .size(height = Theme.spacing._16, width = 56.dp)
                .clip(CircleShape)
                .background(Theme.colorScheme.background.surfaceHigh)
        )
    }
}

@Preview
@Composable
private fun LoadingBestNearDukanItemPreview() {
    MenaTheme {
        LoadingBestNearDukanItem()
    }
}
