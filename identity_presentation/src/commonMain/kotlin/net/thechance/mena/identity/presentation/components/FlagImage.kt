package net.thechance.mena.identity.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.country_flag_image
import mena.identity_presentation.generated.resources.ps_flag
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape


@Composable
internal fun FlagImage(
    flagImage: Painter,
    modifier: Modifier = Modifier
) {
    Image(
        painter = flagImage,
        contentDescription = stringResource(Res.string.country_flag_image),
        contentScale = ContentScale.FillBounds,
        modifier = modifier
            .size(24.dp)
            .clip(SquircleShape(Theme.radius.full))
    )
}

@Preview
@Composable
private fun FlagImagePreview() {
    MenaTheme {
        FlagImage(
            flagImage = painterResource(Res.drawable.ps_flag)
        )
    }

}