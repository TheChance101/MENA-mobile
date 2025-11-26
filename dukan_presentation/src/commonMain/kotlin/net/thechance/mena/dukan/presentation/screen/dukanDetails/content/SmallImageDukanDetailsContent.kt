package net.thechance.mena.dukan.presentation.screen.dukanDetails.content

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.cash.paging.compose.collectAsLazyPagingItems
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.dukan_location
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_favorite
import mena.dukan_presentation.generated.resources.ic_favorite_filled
import mena.dukan_presentation.generated.resources.ic_shopping_basket
import mena.dukan_presentation.generated.resources.shopping_basket_icon
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.component.state.NoInternetContent
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.smallImageDukanDetails.SmallImageDukanIconButton
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.smallImageDukanDetails.SmallImageDukanImageAndTitle
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.smallImageDukanDetails.SmallImageDukanShelvesContent
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukanDetails
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SmallImageDukanDetailsContent(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener
) {
    val shelves = state.shelves.collectAsLazyPagingItems()

    OnSystemBackPressed(listener::onBackClicked)
    Scaffold(
        topBar = {
            SmallImageDukanAppBar(
                isBadgeVisible = state.hasProductInCart,
                listener = listener
            )
        },
        snakeBar = {
            state.snackBarState?.let { snackBarState ->
                SnackBar(
                    snackBarUiState = snackBarState,
                    onDismiss = listener::onDismissSnackBar,
                    onClick = listener::onDismissSnackBar
                )
            }
        }
    ) {
        if (state.dukanDetailsState == DukanDetailsUiState.DukanDetailsState.ERROR) {
            NoInternetContent(
                onRetry = listener::onRetryClicked,
                modifier = Modifier.fillMaxSize()
            )
            return@Scaffold
        }
        Column {
            SmallImageDukanImageAndTitle(
                dukanInfoState = state.dukanInfo,
                modifier = Modifier.padding(
                    start = Theme.spacing._16,
                    end = Theme.spacing._16,
                    top = Theme.spacing._4,
                    bottom = Theme.spacing._16
                )
            )
            Row(
                modifier = Modifier.padding(horizontal = Theme.spacing._16),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
            ) {
                Crossfade(
                    targetState = state.dukanInfo.isFavorite,
                    label = "favoriteCrossfade",
                    modifier = Modifier.weight(1f)
                ) { isFavorite ->
                    val favoriteIcon = if (isFavorite) Res.drawable.ic_favorite_filled
                    else Res.drawable.ic_favorite

                    SmallImageDukanIconButton(
                        icon = painterResource(favoriteIcon),
                        iconColor = Color(state.dukanInfo.color),
                        onIconClick = {
                            listener.onFavoriteDukanClicked(
                                dukanId = state.dukanInfo.dukanId,
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                SmallImageDukanIconButton(
                    icon = painterResource(Res.drawable.dukan_location),
                    iconColor = Color(state.dukanInfo.color),
                    modifier = Modifier.weight(1f),
                    onIconClick = {
                        listener.onViewDukanOnMapClicked(
                            latitude = state.dukanInfo.coordinates.latitude,
                            longitude = state.dukanInfo.coordinates.longitude
                        )
                    }
                )
            }
            SmallImageDukanShelvesContent(
                state = state,
                listener = listener,
                shelves = shelves,
                modifier = Modifier.padding(top = Theme.spacing._16)
            )
        }
    }
}

@Composable
private fun SmallImageDukanAppBar(
    isBadgeVisible: Boolean,
    listener: DukanDetailsInteractionListener
) {
    AppBar(
        title = "",
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back_arrow),
                tint = Theme.colorScheme.primary.primary
            )
        },
        onLeadingClick = listener::onBackClicked,
        trailingContent = {
            AppBarOptionContainer(
                isBadgeVisible = isBadgeVisible,
                onClick = listener::onViewCartClicked
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_shopping_basket),
                    contentDescription = stringResource(Res.string.shopping_basket_icon),
                    tint = Theme.colorScheme.primary.primary
                )
            }
        }
    )
}

@Preview
@Composable
private fun SmallImageDukanDetailsPreview() {
    MenaTheme {
        SmallImageDukanDetailsContent(
            state = fakeDukanDetails,
            listener = PreviewDukanDetailsInteractionListener,
        )
    }
}
