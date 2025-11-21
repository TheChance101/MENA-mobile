package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.favorite_icon
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_favorite
import mena.dukan_presentation.generated.resources.ic_favorite_filled
import mena.dukan_presentation.generated.resources.ic_shopping_basket
import mena.dukan_presentation.generated.resources.wide_dukan_image
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.modifiers.fillWidthOfParent
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukanInfo
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sv.lib.squircleshape.SquircleShape

@Composable
fun WideImageDukanAppBar(
    isBadgeVisible: Boolean,
    onBackClicked: () -> Unit,
    onCartClicked: () -> Unit
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
        onLeadingClick = onBackClicked,
        trailingContent = {
            AppBarOptionContainer(
                isBadgeVisible = isBadgeVisible,
                onClick = { onCartClicked() }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_shopping_basket),
                    contentDescription = null,
                    tint = Theme.colorScheme.primary.primary
                )
            }
        }
    )
}

@Composable
fun WideImageDukanHeader(
    state: DukanDetailsUiState.DukanInfo,
    onFavoriteClicked: (dukanId: String) -> Unit,
) {
    Box(
        modifier = Modifier.fillWidthOfParent(16.dp)
    ) {
        DukanImageAndTitle(
            state = state,
            modifier = Modifier.padding(
                end = Theme.spacing._4 + Theme.spacing._2,
            ).padding(horizontal = Theme.spacing._16)
        )
        DukanActionButtons(
            state = state,
            modifier = Modifier.align(Alignment.TopEnd),
            onFavoriteClicked = onFavoriteClicked
        )
    }
}

@Composable
private fun DukanActionButtons(
    state: DukanDetailsUiState.DukanInfo,
    onFavoriteClicked: (dukanId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(top = Theme.spacing._8)) {
        DukanIconButton(
            onIconClick = { onFavoriteClicked(state.dukanId) }
        ) {
            Crossfade(
                targetState = state.isFavorite,
                label = "favoriteCrossfade"
            ) { isFavorite ->
                Icon(
                    painter = painterResource(
                        if (isFavorite) Res.drawable.ic_favorite_filled
                        else Res.drawable.ic_favorite
                    ),
                    contentDescription = stringResource(Res.string.favorite_icon),
                    tint = Color(state.color)
                )
            }
        }
    }
}

@Composable
private fun DukanImageAndTitle(
    state: DukanDetailsUiState.DukanInfo,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(188.dp)
            .clip(SquircleShape(Theme.radius.md)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = state.imageUrl,
            contentDescription = stringResource(Res.string.wide_dukan_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(state.color).copy(alpha = 0.5f)
                        ),
                        startY = 100f
                    )
                )
        )

        Text(
            text = state.name,
            style = Theme.typography.title.medium,
            color = Color.White,
            maxLines = 2,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = Theme.spacing._8, bottom = Theme.spacing._8)
        )
    }
}

@Composable
private fun DukanIconButton(
    onIconClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(
                onClick = onIconClick,
                indication = null,
                interactionSource = null
            )
            .background(color = Color.White)
            .border(
                width = 3.dp,
                color = Theme.colorScheme.background.surface,
                shape = CircleShape
            )
            .padding(Theme.spacing._8 + Theme.spacing._2),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Preview(showBackground = true, name = "Action Buttons")
@Composable
private fun DukanActionButtonsPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.spacing._16)
        ) {
            DukanActionButtons(
                state = fakeDukanInfo,
                onFavoriteClicked = {},
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}
