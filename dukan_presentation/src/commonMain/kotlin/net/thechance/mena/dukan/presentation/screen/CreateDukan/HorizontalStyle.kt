package net.thechance.mena.dukan.presentation.screen.createDukan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
fun HorizontalStyle(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.xs))
            .background(Theme.colorScheme.background.surface)
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Theme.radius.xxs))
                .background(Theme.colorScheme.background.surfaceLow)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
//            MenaIcon()
        }
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .align(Alignment.Bottom)
                .clip(RoundedCornerShape(Theme.radius.full))
                .background(Theme.colorScheme.background.surfaceHigh)
                .padding(3.dp),
            contentAlignment = Alignment.Center
        ){
//            MenaIcon()
        }
    }
}

@Preview
@Composable
private fun HorizontalStylePreview(){
    MenaTheme {
        HorizontalStyle()
    }
}