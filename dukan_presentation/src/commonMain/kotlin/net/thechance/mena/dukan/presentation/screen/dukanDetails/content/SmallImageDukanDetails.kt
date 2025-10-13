package net.thechance.mena.dukan.presentation.screen.dukanDetails.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.dukan_location
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_favorite
import mena.dukan_presentation.generated.resources.ic_share
import mena.dukan_presentation.generated.resources.ic_shopping_basket
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.SmallImageProductContent
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SmallImageDukanDetails(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    pager: Pager<Int, DukanDetailsUiState.ShelfUiState>
) {
    OnSystemBackPressed { listener::onBackClicked }

    Scaffold(
        topBar = {
            AppBar(
                // no title
                title = "",
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.back_arrow)
                    )
                },
                onLeadingClick = { listener::onBackClicked },
                trailingContent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4)
                    ) {
                        DukanHeaderIcon(
                            icon = painterResource(Res.drawable.ic_share),
                            onIconClick = {}
                        )
                        DukanHeaderIcon(
                            icon = painterResource(Res.drawable.ic_favorite),
                            onIconClick = {}
                        )
                        DukanHeaderIcon(
                            icon = painterResource(Res.drawable.ic_shopping_basket),
                            onIconClick = {}
                        )
                    }
                }
            )
        }
    ) {
        Column {
            DukanImageAndTitle(
                state.dukanInfo,
                modifier = Modifier.padding(
                    start = Theme.spacing._16,
                    end = Theme.spacing._16,
                    top = Theme.spacing._4,
                    bottom = Theme.spacing._16
                )
            )
            Row(
                modifier = Modifier.padding(horizontal = Theme.spacing._16),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing._4),
            ) {
                DukanIconButton(
                    icon = painterResource(Res.drawable.ic_favorite),
                    iconColor = Color(state.dukanInfo.color),
                    modifier = Modifier.weight(1f)
                )
                DukanIconButton(
                    icon = painterResource(Res.drawable.ic_share),
                    iconColor = Color(state.dukanInfo.color),
                    modifier = Modifier.weight(1f)
                )
                DukanIconButton(
                    icon = painterResource(Res.drawable.dukan_location),
                    iconColor = Color(state.dukanInfo.color),
                    modifier = Modifier.weight(1f)
                )
            }
            SmallImageProductContent(
                shelves = state.shelves,
                state = state,
                listener = listener,
                pager = pager,
                modifier = Modifier.padding(top = Theme.spacing._16)
            )
        }
    }

}

@Composable
private fun DukanImageAndTitle(
    state: DukanDetailsUiState.DukanInfo,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        AsyncImage(
            model = state.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(56.dp)
                .clip(RoundedCornerShape(Theme.radius.full))
        )
        Text(
            text = state.name,
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
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
        contentDescription = "dukan details icon",
        tint = iconColor,
        modifier = modifier.clip(RoundedCornerShape(Theme.radius.full))
            .background(iconColor.copy(alpha = 0.04f))
            .clickable(
                onClick = onIconClick,
                indication = null,
                interactionSource = MutableInteractionSource()
            )
            .padding(vertical = Theme.spacing._12 + Theme.spacing._2, horizontal = 43.dp)
    )
}

@Composable
private fun DukanHeaderIcon(
    icon: Painter,
    isBadgeVisible: Boolean = false,
    onIconClick: () -> Unit,
) {
    AppBarOptionContainer(
        onClick = { onIconClick() },
        isBadgeVisible = isBadgeVisible
    ) {
        Icon(
            painter = icon,
            contentDescription = "header icon"
        )
    }
}
