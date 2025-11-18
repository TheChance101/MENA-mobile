package net.thechance.mena.faith.presentation.feature.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_quran
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CardIcon(
    icon: Painter,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = icon,
        contentDescription = contentDescription,
        tint = Theme.colorScheme.primary.primary,
        modifier = modifier
            .clip(RoundedCornerShape(Theme.radius.md))
            .background(Theme.colorScheme.background.surfaceHigh)
            .padding(Theme.spacing._8)
            .size(24.dp)
    )
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            CardIcon(
                icon = painterResource(Res.drawable.ic_quran),
                contentDescription = "Quran Kareem"
            )
        }
    }
}
