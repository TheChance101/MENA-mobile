package net.thechance.mena.dukan.presentation.screen.createDukan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun VerticalStyle(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.xs))
            .background(Theme.colorScheme.background.surface)
            .padding(top = 2.dp, bottom = 20.dp, start = 2.dp, end = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Theme.radius.xxs))
                .background(Theme.colorScheme.background.surfaceLow)
                .padding(9.dp),
//            contentAlignment = Alignment.Center
        ) {
//            MenaIcon()
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Theme.radius.full))
                    .background(Theme.colorScheme.background.surfaceHigh)
                    .padding(3.dp)
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {
//            MenaIcon()
            }
        }

    }
}

@Preview
@Composable
private fun VerticalStylePreview() {
    MenaTheme {
        VerticalStyle()
    }
}