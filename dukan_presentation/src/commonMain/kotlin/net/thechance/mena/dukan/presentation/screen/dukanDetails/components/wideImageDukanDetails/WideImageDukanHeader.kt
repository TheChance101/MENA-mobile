package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.wideImageDukanDetails

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.favorite_icon
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_favorite
import mena.dukan_presentation.generated.resources.ic_share
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

@Composable
fun WideImageDukanAppBar(
    onBackClicked: () -> Unit,
    onCartClicked: () -> Unit
) {
    AppBar(
        title = "",
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back_arrow)
            )
        },
        onLeadingClick = onBackClicked,
        trailingContent = {
            AppBarOptionContainer(onClick = { onCartClicked }) {
                Icon(
                    painter = painterResource(Res.drawable.ic_shopping_basket),
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
fun DukanHeader(state: DukanDetailsUiState.DukanInfo) {
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
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun DukanActionButtons(
    state: DukanDetailsUiState.DukanInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(top = Theme.spacing._8)) {
        DukanIconButton(
            icon = painterResource(Res.drawable.ic_share),
            iconColor = Color(state.color),
        )
        DukanIconButton(
            icon = painterResource(Res.drawable.ic_favorite),
            iconColor = Color(state.color),
        )
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
            .clip(RoundedCornerShape(Theme.radius.md)),
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
            color = Theme.colorScheme.primary.onPrimary,
            maxLines = 2,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = Theme.spacing._8, bottom = Theme.spacing._8)
        )
    }
}

@Composable
private fun DukanIconButton(
    icon: Painter,
    iconColor: Color,
    onIconClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Icon(
        painter = icon,
        contentDescription = stringResource(Res.string.favorite_icon),
        tint = iconColor,
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(Theme.radius.full))
            .clickable(
                onClick = onIconClick,
            )
            .background(color = Theme.colorScheme.background.surfaceLow)
            .border(
                width = 3.dp,
                color = Theme.colorScheme.background.surface,
                shape = RoundedCornerShape(Theme.radius.full)
            )
            .padding(Theme.spacing._8 + Theme.spacing._2)
    )
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
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}