@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.screen.search.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.dukans
import mena.dukan_presentation.generated.resources.img_not_found_search
import mena.dukan_presentation.generated.resources.img_not_found_search_dark
import mena.dukan_presentation.generated.resources.no_result_found
import mena.dukan_presentation.generated.resources.no_result_found_body
import mena.dukan_presentation.generated.resources.products
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.LoadingDukanPlaceholder
import net.thechance.mena.dukan.presentation.component.loading.LoadingProductCard
import net.thechance.mena.dukan.presentation.component.product.ProductCard
import net.thechance.mena.dukan.presentation.component.shared.DukanCard
import net.thechance.mena.dukan.presentation.navigation.LocalDarkTheme
import net.thechance.mena.dukan.presentation.util.animation.fadeTransitionSpec
import net.thechance.mena.dukan.presentation.util.animation.fadeWithSlideHorizontalTransition
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewSearchInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.previewDukansFlow
import net.thechance.mena.dukan.presentation.util.stubPreviews.previewProductsFlow
import net.thechance.mena.dukan.presentation.viewModel.search.SearchInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.search.SearchUiState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun SearchCompleteContent(
    state: SearchUiState,
    listener: SearchInteractionListener
) {
    val dukanPagingItems = state.dukanPagingFlow.collectAsLazyPagingItems()
    val productPagingItems = state.productPagingFlow.collectAsLazyPagingItems()

    val isDark = LocalDarkTheme.current
    val icon = if (isDark) Res.drawable.img_not_found_search_dark else Res.drawable.img_not_found_search


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchChips(
            isDukanSelected = state.userSelectionSearchList == SearchUiState.UserSelectionSearchList.Dukans,
            isProductSelected = state.userSelectionSearchList == SearchUiState.UserSelectionSearchList.Products,
            onDukansSelected = listener::onDukansSelected,
            onProductsSelected = listener::onProductsSelected,
        )
        AnimatedContent(
            modifier = Modifier.padding(top = Theme.spacing._12),
            targetState = state.userSelectionSearchList,
            label = "Search Complete Content",
            transitionSpec = {
                val toRight = targetState.ordinal > initialState.ordinal
                fadeWithSlideHorizontalTransition(toRight)
            }
        ) { selectedList ->
            when (selectedList) {
                SearchUiState.UserSelectionSearchList.Dukans -> DukansList(
                    dukanPagingItems = dukanPagingItems,
                    onDukanClicked = listener::onDukanClicked,
                    onDukanFavoriteClicked = listener::onDukanFavoriteToggled,
                    icon = icon
                )

                SearchUiState.UserSelectionSearchList.Products -> ProductsList(
                    productPagingItems = productPagingItems,
                    onProductClicked = listener::onProductClicked,
                    icon = icon
                )
            }
        }
    }
}

@Composable
private fun SearchChips(
    isDukanSelected: Boolean,
    isProductSelected: Boolean,
    onDukansSelected: () -> Unit,
    onProductsSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(top = Theme.spacing._12, start = Theme.spacing._16)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
    ) {
        Chip(
            text = stringResource(resource = Res.string.dukans),
            modifier = Modifier,
            isSelected = isDukanSelected,
            onClick = onDukansSelected,
        )
        Chip(
            text = stringResource(resource = Res.string.products),
            modifier = Modifier,
            isSelected = isProductSelected,
            onClick = onProductsSelected,
        )
    }
}

@Composable
private fun DukansList(
    dukanPagingItems: LazyPagingItems<SearchUiState.DukanUiState>,
    onDukanClicked: (dukanId: Uuid) -> Unit,
    icon: DrawableResource,
    onDukanFavoriteClicked: (dukan: Uuid, isFavorite: Boolean) -> Unit
) {

    AnimatedContent(
        targetState = dukanPagingItems.loadState.refresh,
        label = "Search Success Content",
        transitionSpec = { fadeTransitionSpec() }
    ) { resultState ->
        when (resultState) {
            LoadState.Loading -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
                    contentPadding = PaddingValues(horizontal = Theme.spacing._16)
                ) {
                    items(count = 5) {
                        LoadingDukanPlaceholder()
                    }
                }
            }

            is LoadState.Error -> SearchEmptyContent(
                icon = painterResource(resource = icon),
                title = stringResource(resource = Res.string.no_result_found),
                body = stringResource(resource = Res.string.no_result_found_body)
            )

            is LoadState.NotLoading -> {
                if (dukanPagingItems.itemCount == 0) {
                    SearchEmptyContent(
                        icon = painterResource(resource = icon),
                        title = stringResource(resource = Res.string.no_result_found),
                        body = stringResource(resource = Res.string.no_result_found_body)
                    )
                    return@AnimatedContent
                }
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(328.dp),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
                    contentPadding = PaddingValues(horizontal = Theme.spacing._16)
                ) {
                    items(
                        count = dukanPagingItems.itemCount,
                        key = { index -> dukanPagingItems[index]?.id ?: index },
                        contentType = { "Dukan Search Card" }
                    ) { index ->
                        dukanPagingItems[index]?.let { dukan ->
                            DukanCard(
                                title = dukan.title,
                                imageUrl = dukan.imageUrl,
                                onClick = { onDukanClicked(dukan.id) },
                                isFavorite = dukan.isFavorite,
                                onFavoriteClick = {
                                    onDukanFavoriteClicked(
                                        dukan.id,
                                        dukan.isFavorite
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }


}


@Composable
private fun ProductsList(
    productPagingItems: LazyPagingItems<SearchUiState.ProductUiState>,
    icon: DrawableResource,
    onProductClicked: (productId: Uuid, dukanId: Uuid) -> Unit
) {
    AnimatedContent(
        targetState = productPagingItems.loadState.refresh,
        label = "Search Success Content",
        transitionSpec = { fadeTransitionSpec() }
    ) { resultState ->
        when (resultState) {
            LoadState.Loading -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
                    contentPadding = PaddingValues(horizontal = Theme.spacing._16)
                ) {
                    items(count = 5) {
                        LoadingProductCard()
                    }
                }
            }

            is LoadState.Error -> SearchEmptyContent(
                icon = painterResource(resource = icon),
                title = stringResource(resource = Res.string.no_result_found),
                body = stringResource(resource = Res.string.no_result_found_body)
            )

            is LoadState.NotLoading -> {
                if (productPagingItems.itemCount == 0) {
                    SearchEmptyContent(
                        icon = painterResource(resource = icon),
                        title = stringResource(resource = Res.string.no_result_found),
                        body = stringResource(resource = Res.string.no_result_found_body)
                    )
                    return@AnimatedContent
                }

                val gridState = rememberLazyGridState()
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 320.dp),
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = Theme.spacing._16,
                        end = Theme.spacing._16,
                        bottom = Theme.spacing._8,
                        top = Theme.spacing._4
                    ),
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
                ) {
                    items(
                        count = productPagingItems.itemCount,
                        key = { index -> productPagingItems[index]?.id ?: index },
                        contentType = { "Product Search Card" }
                    ) { index ->
                        productPagingItems[index]?.let { product ->
                            ProductCard(
                                name = product.name,
                                imageUrl = product.imageUrl,
                                description = "from (${product.dukanName})",
                                basePrice = product.price,
                                finalPrice = product.price,
                                backgroundColor = Theme.colorScheme.background.surfaceLow,
                                productImageBackground = Theme.colorScheme.background.surfaceHigh,
                                onProductClick = { onProductClicked(product.id, product.dukanId) },
                                isOutOfStock = product.isOutOfStock
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchDukansContentPreview() {
    MenaTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            SearchCompleteContent(
                state = SearchUiState(
                    userSelectionSearchList = SearchUiState.UserSelectionSearchList.Dukans,
                    dukanPagingFlow = previewDukansFlow
                ),
                listener = PreviewSearchInteractionListener
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchProductsContentPreview() {
    MenaTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            SearchCompleteContent(
                state = SearchUiState(
                    userSelectionSearchList = SearchUiState.UserSelectionSearchList.Products,
                    productPagingFlow = previewProductsFlow
                ),
                listener = PreviewSearchInteractionListener
            )
        }
    }
}
