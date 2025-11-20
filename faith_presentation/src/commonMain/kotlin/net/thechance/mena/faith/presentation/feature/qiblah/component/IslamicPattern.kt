package net.thechance.mena.faith.presentation.feature.qiblah.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.ic_islamic_pattern
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.faith.presentation.designSystem.theme.QuranTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun IslamicPattern(
    modifier: Modifier = Modifier,
    colorFilter: ColorFilter? = ColorFilter.tint(Theme.colorScheme.secondary.secondary)
) {
    Image(
        painter = painterResource(Res.drawable.ic_islamic_pattern),
        contentDescription = stringResource(Res.string.ic_islamic_pattern),
        modifier = modifier,
        contentScale = ContentScale.Fit,
        colorFilter = colorFilter
    )
}

@Preview
@Composable
private fun Preview() {
    MenaTheme {
        QuranTheme {
            IslamicPattern()
        }
    }
}
