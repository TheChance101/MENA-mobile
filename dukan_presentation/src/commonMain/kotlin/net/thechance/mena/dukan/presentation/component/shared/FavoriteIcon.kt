package net.thechance.mena.dukan.presentation.component.shared

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.favorites
import mena.dukan_presentation.generated.resources.ic_favorite
import mena.dukan_presentation.generated.resources.ic_favorite_filled
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FavoriteIcon(
    isFavorite: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .background(
                color = Theme.colorScheme.primary.primary,
                shape = CircleShape
            )
            .clickable(
                interactionSource = null,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Crossfade(targetState = isFavorite) { isFavorite ->
            val favoriteIcon = if (isFavorite) Res.drawable.ic_favorite_filled
            else Res.drawable.ic_favorite

            Icon(
                painter = painterResource(favoriteIcon),
                contentDescription = stringResource(Res.string.favorites),
                modifier = Modifier.size(20.dp),
                tint = Theme.colorScheme.primary.onPrimary
            )
        }
    }
}

@Preview
@Composable
private fun FavoriteIconSelectedPreview() {
    MenaTheme {
        Box(
            modifier = Modifier.padding(Theme.spacing._16)
        ) {
            FavoriteIcon(
                isFavorite = true,
                onClick = {}
            )
        }
    }
}
