@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.screen.search.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.dukans
import mena.dukan_presentation.generated.resources.img_not_found_search
import mena.dukan_presentation.generated.resources.no_result_found
import mena.dukan_presentation.generated.resources.no_result_found_body
import mena.dukan_presentation.generated.resources.products
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.LoadingDukanPlaceholder
import net.thechance.mena.dukan.presentation.component.loading.LoadingProductCard
import net.thechance.mena.dukan.presentation.component.product.ProductCard
import net.thechance.mena.dukan.presentation.component.shared.DukanCard
import net.thechance.mena.dukan.presentation.util.animation.fadeWithSlideHorizontalTransition
import net.thechance.mena.dukan.presentation.util.animation.fadeWithSlideVerticalTransition
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewSearchInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.previewDukansFlow
import net.thechance.mena.dukan.presentation.util.stubPreviews.previewProductsFlow
import net.thechance.mena.dukan.presentation.viewModel.search.SearchInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.search.SearchUiState
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .padding(top = Theme.spacing._12, start = Theme.spacing._16)
                .fillMaxWidth()
                .height(32.dp),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        ) {
            Chip(
                text = stringResource(resource = Res.string.dukans),
                modifier = Modifier.height(32.dp),
                isSelected = state.userSelectionSearchList == SearchUiState.UserSelectionSearchList.Dukans,
                onClick = listener::onSelectDukans,
            )
            Chip(
                text = stringResource(resource = Res.string.products),
                modifier = Modifier.height(32.dp),
                isSelected = state.userSelectionSearchList == SearchUiState.UserSelectionSearchList.Products,
                onClick = listener::onSelectProducts,
            )
        }
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
                    onDukanFavoriteClicked = listener::onDukanFavoriteClicked
                )

                SearchUiState.UserSelectionSearchList.Products -> ProductsList(
                    productPagingItems = productPagingItems,
                    onProductClicked = listener::onProductClicked
                )
            }
        }
    }
}

@Composable
private fun DukansList(
    dukanPagingItems: LazyPagingItems<SearchUiState.DukanUiState>,
    onDukanClicked: (dukanId: Uuid) -> Unit,
    onDukanFavoriteClicked: (dukan: Uuid) -> Unit
) {
    AnimatedContent(
        targetState = dukanPagingItems.loadState.refresh,
        label = "Search Success Content",
        transitionSpec = { fadeWithSlideVerticalTransition() }
    ) { resultState ->
        when (resultState) {
            LoadState.Loading -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
                    contentPadding = PaddingValues(horizontal = Theme.spacing._16)
                ) {
                    items(count = 8) {
                        LoadingDukanPlaceholder()
                    }
                }
            }

            is LoadState.Error -> SearchEmptyContent(
                icon = painterResource(resource = Res.drawable.img_not_found_search),
                title = stringResource(resource = Res.string.no_result_found),
                body = stringResource(resource = Res.string.no_result_found_body)
            )

            is LoadState.NotLoading -> {
                if (dukanPagingItems.itemCount == 0) {
                    SearchEmptyContent(
                        icon = painterResource(resource = Res.drawable.img_not_found_search),
                        title = stringResource(resource = Res.string.no_result_found),
                        body = stringResource(resource = Res.string.no_result_found_body)
                    )
                    return@AnimatedContent
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
                    contentPadding = PaddingValues(horizontal = Theme.spacing._16)
                ) {
                    items(
                        count = dukanPagingItems.itemCount,
                        contentType = { "Dukan Search Card" }
                    ) { index ->
                        dukanPagingItems[index]?.let { dukan ->
                            DukanCard(
                                title = dukan.title,
                                imageUrl = dukan.imageUrl,
                                onClick = { onDukanClicked(dukan.id) },
                                isFavorite = dukan.isFavorite, // Todo
                                onFavoriteClick = { onDukanFavoriteClicked(dukan.id) },
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
    onProductClicked: (productId: Uuid) -> Unit
) {
    AnimatedContent(
        targetState = productPagingItems.loadState.refresh,
        label = "Search Success Content",
        transitionSpec = { fadeWithSlideVerticalTransition() }
    ) { resultState ->
        when (resultState) {
            LoadState.Loading -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
                    contentPadding = PaddingValues(horizontal = Theme.spacing._16)
                ) {
                    items(count = 8) {
                        LoadingProductCard()
                    }
                }
            }

            is LoadState.Error -> SearchEmptyContent(
                icon = painterResource(resource = Res.drawable.img_not_found_search),
                title = stringResource(resource = Res.string.no_result_found),
                body = stringResource(resource = Res.string.no_result_found_body)
            )

            is LoadState.NotLoading -> {
                if (productPagingItems.itemCount == 0) {
                    SearchEmptyContent(
                        icon = painterResource(resource = Res.drawable.img_not_found_search),
                        title = stringResource(resource = Res.string.no_result_found),
                        body = stringResource(resource = Res.string.no_result_found_body)
                    )
                    return@AnimatedContent
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
                    contentPadding = PaddingValues(horizontal = Theme.spacing._16)
                ) {
                    items(
                        count = productPagingItems.itemCount,
                        contentType = { "Product Search Card" }
                    ) { index ->
                        productPagingItems[index]?.let { product ->
                            ProductCard(
                                productName = product.name,
                                productImageUrl = product.imageUrl,
                                productDescription = product.dukanName,
                                productPrice = product.price,
                                productCardBackground = Theme.colorScheme.background.surfaceLow,
                                productImageBackground = Theme.colorScheme.background.surfaceHigh,
                                onProductClicked = { onProductClicked(product.id) },
                                productAction = {},
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
    SearchCompleteContent(
        state = SearchUiState(
            userSelectionSearchList = SearchUiState.UserSelectionSearchList.Dukans,
            dukanPagingFlow = previewDukansFlow
        ),
        listener = PreviewSearchInteractionListener
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchProductsContentPreview() {
    SearchCompleteContent(
        state = SearchUiState(
            userSelectionSearchList = SearchUiState.UserSelectionSearchList.Products,
            productPagingFlow = previewProductsFlow
        ),
        listener = PreviewSearchInteractionListener
    )
}
