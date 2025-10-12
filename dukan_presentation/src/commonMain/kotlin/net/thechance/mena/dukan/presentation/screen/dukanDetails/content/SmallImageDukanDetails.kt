package net.thechance.mena.dukan.presentation.screen.dukanDetails.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import mena.dukan_presentation.generated.resources.ic_store_location
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.FakeDukanPagingSource
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.SmallImageProductContent
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.shimmerLoading
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

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
                state.isDukanInfoLoading,
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
                    isLoading = state.isDukanInfoLoading,
                    modifier = Modifier.weight(1f)
                )
                DukanIconButton(
                    icon = painterResource(Res.drawable.ic_share),
                    iconColor = Color(state.dukanInfo.color),
                    isLoading = state.isDukanInfoLoading,
                    modifier = Modifier.weight(1f)
                )
                DukanIconButton(
                    icon = painterResource(Res.drawable.dukan_location),
                    iconColor = Color(state.dukanInfo.color),
                    isLoading = state.isDukanInfoLoading,
                    modifier = Modifier.weight(1f)
                )
            }
            SmallImageProductContent(
                shelves = state.shelves,
                state = state,
                listener = listener,
                pager = pager,
            )
        }
    }

}

@Composable
private fun DukanImageAndTitle(
    state: DukanDetailsUiState.DukanInfo,
    isLoading: Boolean,
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
                .shimmerLoading(isLoading, Theme.radius.full)
        )
        Text(
            text = state.name,
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
                .shimmerLoading(isLoading, Theme.radius.lg)
        )
    }
}

@Composable
private fun DukanIconButton(
    icon: Painter,
    iconColor: Color,
    isLoading: Boolean,
    onIconClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Icon(
        painter = icon,
        contentDescription = "dukan details icon",
        tint = iconColor,
        modifier = modifier.clip(RoundedCornerShape(Theme.radius.full))
            .then(
                if (isLoading) {
                    Modifier.shimmerLoading(true, Theme.radius.full)
                } else {
                    Modifier.background(iconColor.copy(alpha = 0.04f))
                }
                    .padding(vertical = Theme.spacing._12+Theme.spacing._2, horizontal = 43.dp)
                    .clickable(onClick = onIconClick)
            )
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

@Preview
@Composable
private fun SmallImageDukanDetailsPreview() {
    MenaTheme {
        SmallImageDukanDetails(
            state = DukanDetailsUiState(
                dukanInfo = DukanDetailsUiState.DukanInfo(
                    name = "Fashion House",
                    imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400",
                    style = DukanDetailsUiState.Style.WIDE_IMAGE,
                    color = 0xFFFB5B5D,
                    coordinates = DukanDetailsUiState.Coordinates(
                        latitude = 30.0444,
                        longitude = 31.2357
                    )
                ),
                isDukanInfoLoading = false,
                shelvesState = DukanDetailsUiState.ShelvesState.LOADED,
                productsState = DukanDetailsUiState.ProductsState.LOADED,
                shelves = PagingData(
                    items = listOf(
                        DukanDetailsUiState.ShelfUiState(
                            id = "s1",
                            name = "Dresses",
                            products = listOf(
                                DukanDetailsUiState.ProductUiState(
                                    id = "p1",
                                    name = "Black Maxi Dress",
                                    imageUrl = "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400",
                                    price = 89.99,
                                    description = "A perfect fit for formal occasions."
                                ),
                                DukanDetailsUiState.ProductUiState(
                                    id = "p2",
                                    name = "Floral Summer Dress",
                                    imageUrl = "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?w=400",
                                    price = 69.99,
                                    description = "Light and colorful summer dress."
                                ),
                                DukanDetailsUiState.ProductUiState(
                                    id = "p1",
                                    name = "Black Maxi Dress",
                                    imageUrl = "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400",
                                    price = 89.99,
                                    description = "A perfect fit for formal occasions."
                                ),
                                DukanDetailsUiState.ProductUiState(
                                    id = "p2",
                                    name = "Floral Summer Dress",
                                    imageUrl = "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?w=400",
                                    price = 69.99,
                                    description = "Light and colorful summer dress."
                                ), DukanDetailsUiState.ProductUiState(
                                    id = "p1",
                                    name = "Black Maxi Dress",
                                    imageUrl = "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400",
                                    price = 89.99,
                                    description = "A perfect fit for formal occasions."
                                ),
                                DukanDetailsUiState.ProductUiState(
                                    id = "p2",
                                    name = "Floral Summer Dress",
                                    imageUrl = "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?w=400",
                                    price = 69.99,
                                    description = "Light and colorful summer dress."
                                )
                            )
                        ),
                        DukanDetailsUiState.ShelfUiState(
                            id = "s2",
                            name = "Shoes",
                            products = listOf(
                                DukanDetailsUiState.ProductUiState(
                                    id = "p3",
                                    name = "Leather Sandals",
                                    imageUrl = "https://images.unsplash.com/photo-1544441893-675973e31985?w=400",
                                    price = 49.99,
                                    description = "Handmade brown leather sandals."
                                ),
                                DukanDetailsUiState.ProductUiState(
                                    id = "p4",
                                    name = "Sport Trainers",
                                    imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400",
                                    price = 89.99,
                                    description = "Durable sports trainers for daily wear."
                                )
                            )
                        ),
                        DukanDetailsUiState.ShelfUiState(
                            id = "s2",
                            name = "Shoes",
                            products = listOf(
                                DukanDetailsUiState.ProductUiState(
                                    id = "p3",
                                    name = "Leather Sandals",
                                    imageUrl = "https://images.unsplash.com/photo-1544441893-675973e31985?w=400",
                                    price = 49.99,
                                    description = "Handmade brown leather sandals."
                                ),
                                DukanDetailsUiState.ProductUiState(
                                    id = "p4",
                                    name = "Sport Trainers",
                                    imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400",
                                    price = 89.99,
                                    description = "Durable sports trainers for daily wear."
                                )
                            )
                        ),
                        DukanDetailsUiState.ShelfUiState(
                            id = "s2",
                            name = "Shoes",
                            products = listOf(
                                DukanDetailsUiState.ProductUiState(
                                    id = "p3",
                                    name = "Leather Sandals",
                                    imageUrl = "https://images.unsplash.com/photo-1544441893-675973e31985?w=400",
                                    price = 49.99,
                                    description = "Handmade brown leather sandals."
                                ),
                                DukanDetailsUiState.ProductUiState(
                                    id = "p4",
                                    name = "Sport Trainers",
                                    imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400",
                                    price = 89.99,
                                    description = "Durable sports trainers for daily wear."
                                )
                            )
                        ),
                    )
                )
            ),
            listener = object : DukanDetailsInteractionListener {
                override fun onBackClicked() {}
                override fun onShelfClicked(id: String) {}
                override fun onViewAllShelfProductsClicked(id: String, name: String) {}
                override fun onViewDukanOnMapClicked(
                    latitude: Double,
                    longitude: Double
                ) {
                }
            },
            pager = Pager(
                PagingConfig()
            ) { FakeDukanPagingSource() }
        )
    }
}