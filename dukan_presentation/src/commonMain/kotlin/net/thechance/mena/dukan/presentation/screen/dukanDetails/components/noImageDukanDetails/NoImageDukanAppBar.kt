package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageDukanDetails

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.favorite_icon
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_favorite
import mena.dukan_presentation.generated.resources.ic_favorite_filled
import mena.dukan_presentation.generated.resources.ic_shopping_basket
import mena.dukan_presentation.generated.resources.shopping_basket_icon
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukanInfo
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.DukanInfo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoImageDukanAppBar(
    state: DukanInfo,
    isBadgeVisible: Boolean,
    listener: DukanDetailsInteractionListener
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._8)
    ) {
        AppBarIcon(
            painter = painterResource(Res.drawable.ic_arrow_left),
            contentDescription = stringResource(Res.string.back_arrow),
            onClick = listener::onBackClicked
        )
        Text(
            text = state.name,
            color = Theme.colorScheme.shadePrimary,
            style = Theme.typography.title.medium,
            modifier = Modifier.padding(start = Theme.spacing._8, end = Theme.spacing._4)
                .weight(1f),
            maxLines = 1
        )
        Row(
            modifier = Modifier.padding(start = Theme.spacing._4),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
        ) {
            Crossfade(
                targetState = state.isFavorite,
                label = "favoriteCrossfade"
            ) { isFavorite ->
                val favoriteIcon = if (isFavorite) Res.drawable.ic_favorite_filled
                else Res.drawable.ic_favorite

                AppBarIcon(
                    painter = painterResource(favoriteIcon),
                    contentDescription = stringResource(Res.string.favorite_icon),
                    onClick = {
                        listener.onFavoriteDukanClicked(
                            dukanId = state.dukanId,
                        )
                    }
                )
            }
            AppBarIcon(
                painter = painterResource(Res.drawable.ic_shopping_basket),
                contentDescription = stringResource(Res.string.shopping_basket_icon),
                isBadgeVisible = isBadgeVisible,
                onClick = listener::onViewCartClicked
            )
        }
    }
}

@Composable
private fun AppBarIcon(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isBadgeVisible: Boolean = false
) {
    AppBarOptionContainer(
        onClick = onClick,
        isBadgeVisible = isBadgeVisible
    ) {
        Icon(
            painter = painter,
            tint = Theme.colorScheme.primary.primary,
            contentDescription = contentDescription,
            modifier = modifier.size(40.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF2F4F7)
@Composable
private fun AppBarNoImageDukanPreview() {
    MenaTheme {
        NoImageDukanAppBar(
            state = fakeDukanInfo,
            isBadgeVisible = true,
            listener = PreviewDukanDetailsInteractionListener
        )
    }
}