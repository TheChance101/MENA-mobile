package net.thechance.mena.dukan.presentation.util.animation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import net.thechance.mena.designsystem.presentation.theme.theme.Theme

@Composable
fun Modifier.skeletonLoading(
    isLoading: Boolean
): Modifier {
    val color = Theme.colorScheme.background.surfaceHigh
    return this.drawWithContent {
        if (isLoading) {
            drawRect(
                color = color,
            )
        }else{
            drawContent()
        }
    }
}