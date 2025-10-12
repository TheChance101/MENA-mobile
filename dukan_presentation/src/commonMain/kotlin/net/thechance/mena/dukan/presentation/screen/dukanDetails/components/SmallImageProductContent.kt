package net.thechance.mena.dukan.presentation.screen.dukanDetails.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_arrow_right
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.productCard.LoadingProductCard
import net.thechance.mena.dukan.presentation.component.productCard.ProductCard
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.resources.painterResource

@Composable
fun SmallImageProductContent(
    shelves: PagingData<DukanDetailsUiState.ShelfUiState>,
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    pager: Pager<Int, DukanDetailsUiState.ShelfUiState>,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    lazyListState.LoadMoreOnScroll(pager)

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._16),
        contentPadding = PaddingValues(vertical = 16.dp),
        state = lazyListState
    ) {
        items(shelves.items) { shelf ->
            ProductsShelf(
                state = state,
                shelf = shelf,
                listener = listener
            )
        }
    }
}

@Composable
private fun ProductsShelf(
    state: DukanDetailsUiState,
    shelf: DukanDetailsUiState.ShelfUiState,
    listener: DukanDetailsInteractionListener,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)) {

        when (state.shelvesState) {
            DukanDetailsUiState.ShelvesState.LOADING -> {
                ShelfHeader(
                    isLoading = true,
                    state = state,
                    shelfName = shelf.name,
                    onViewAllClicked = {listener.onViewAllShelfProductsClicked(shelf.id, shelf.name)},
                    modifier = Modifier.padding(horizontal = Theme.spacing._16)
                )
            }
            DukanDetailsUiState.ShelvesState.LOADED -> {
                ShelfHeader(
                    isLoading = false,
                    state = state,
                    shelfName = shelf.name,
                    onViewAllClicked = {listener.onViewAllShelfProductsClicked(shelf.id, shelf.name)},
                    modifier = Modifier.padding(horizontal = Theme.spacing._16)
                )
            }
            DukanDetailsUiState.ShelvesState.EMPTY -> {}
        }

        LazyRow(
            contentPadding = PaddingValues(Theme.spacing._16),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
        ) {
            val productPairs = shelf.products.chunked(2)

            when (state.productsState) {
                DukanDetailsUiState.ProductsState.LOADING -> {
                    productCardState(
                        isLoading = true,
                        productPairs = productPairs
                    )
                }
                DukanDetailsUiState.ProductsState.LOADED -> {
                    productCardState(
                        isLoading = false,
                        productPairs = productPairs
                    )
                }
                DukanDetailsUiState.ProductsState.EMPTY -> {}
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun LazyListScope.productCardState(
    isLoading: Boolean,
    productPairs: List<List<DukanDetailsUiState.ProductUiState>>,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        items(productPairs.size) {index->
            Column(
                modifier = modifier.fillParentMaxWidth(if (index == productPairs.lastIndex) 1f else 0.95f),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
            ) {
                LoadingProductCard()
                LoadingProductCard()
            }
        }
    } else {
        itemsIndexed(productPairs) {index, pair ->
            Column(
                modifier = Modifier.fillParentMaxWidth(if (index == productPairs.lastIndex) 1f else 0.95f),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
            ) {
                pair.forEach { product ->
                    ProductCard(
                        productName = product.name,
                        productImageUrl = product.imageUrl,
                        productDescription = product.description,
                        productPrice = product.price,
                        productCardBackground = Theme.colorScheme.background.surfaceLow,
                        productAction = {}
                    )
                }
            }
        }
    }
}

@Composable
private fun ShelfHeader(
    isLoading: Boolean,
    state: DukanDetailsUiState,
    shelfName: String,
    onViewAllClicked:() -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            onViewAllClicked()
          },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = shelfName,
            style = Theme.typography.title.small,
            color = Theme.colorScheme.shadePrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .shimmerLoading(isLoading, Theme.radius.lg)
                .weight(1f)
        )

        Text(
            text = "All",
            style = Theme.typography.label.medium,
            color = Color(state.dukanInfo.color),
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(end = 3.dp)
        )

        Icon(
            painter = painterResource(Res.drawable.ic_arrow_right),
            contentDescription = "arrow right",
            tint = Color(state.dukanInfo.color)
        )
    }
}
