@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.screen.search.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
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
import androidx.paging.PagingData
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import kotlinx.coroutines.flow.Flow
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.dukans
import mena.dukan_presentation.generated.resources.img_not_found_search
import mena.dukan_presentation.generated.resources.no_result_found
import mena.dukan_presentation.generated.resources.no_result_found_body
import mena.dukan_presentation.generated.resources.products
import net.thechance.mena.designsystem.presentation.component.chip.Chip
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.LoadingDots
import net.thechance.mena.dukan.presentation.component.loading.LoadingDukanPlaceholder
import net.thechance.mena.dukan.presentation.component.product.ProductCard
import net.thechance.mena.dukan.presentation.component.shared.DukanCard
import net.thechance.mena.dukan.presentation.util.animation.fadeWithSlideHorizontalTransition
import net.thechance.mena.dukan.presentation.util.animation.fadeWithSlideVerticalTransition
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewSearchInteractionListener
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
                    dukanFlow = state.dukanPagingFlow,
                    onDukanClicked = listener::onDukanClicked,
                    onDukanFavoriteClicked = listener::onDukanFavoriteClicked
                )

                SearchUiState.UserSelectionSearchList.Products -> ProductsList(
                    productFlow = state.productPagingFlow,
                    onProductClicked = listener::onProductClicked
                )
            }
        }
    }
}

@Composable
private fun DukansList(
    dukanFlow: Flow<PagingData<SearchUiState.DukanUiState>>,
    onDukanClicked: (dukanId: Uuid) -> Unit,
    onDukanFavoriteClicked: (dukan: Uuid) -> Unit
) {
    val dukanPagingItems = dukanFlow.collectAsLazyPagingItems()

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
                    when {
                        dukanPagingItems.itemCount == 0 -> items(count = 8) { LoadingDukanPlaceholder() }
                        else -> item { LoadingDots(modifier = Modifier.fillMaxWidth()) }
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
                        key = { index -> dukanPagingItems.itemKey { index } },
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
    productFlow: Flow<PagingData<SearchUiState.ProductUiState>>,
    onProductClicked: (productId: Uuid) -> Unit
) {
    val productPagingData = productFlow.collectAsLazyPagingItems()

    AnimatedContent(
        targetState = productPagingData.loadState.refresh,
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
                    when {
                        productPagingData.itemCount == 0 -> items(count = 8) { LoadingDukanPlaceholder() }
                        else -> item { LoadingDots(modifier = Modifier.fillMaxWidth()) }
                    }
                }
            }

            is LoadState.Error -> SearchEmptyContent(
                icon = painterResource(resource = Res.drawable.img_not_found_search),
                title = stringResource(resource = Res.string.no_result_found),
                body = stringResource(resource = Res.string.no_result_found_body)
            )

            is LoadState.NotLoading -> {
                if (productPagingData.itemCount == 0) {
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
                        count = productPagingData.itemCount,
                        key = { index -> productPagingData.itemKey { index } },
                        contentType = { "Product Search Card" }
                    ) { index ->
                        productPagingData[index]?.let { product ->
                            ProductCard(
                                productName = product.name,
                                productImageUrl = product.imageUrl,
                                productDescription = product.dukanName,
                                productPrice = product.price,
                                productCardBackground = Theme.colorScheme.background.surfaceLow,
                                productImageBackground = Theme.colorScheme.background.surfaceHigh,
                                modifier = Modifier.clickable(
                                    onClick = { onProductClicked(product.id) },
                                    indication = null,
                                    interactionSource = null
                                ),
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
        ),
        listener = PreviewSearchInteractionListener
    )
}
