package net.thechance.mena.dukan.presentation.screen.dukanDetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.ProductsHeader
import net.thechance.mena.dukan.presentation.component.productCard.ProductCard
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState

@Composable
fun SmallImageProductContent(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    shelvesPager: Pager<Int, DukanDetailsUiState.ShelfUiState>,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    lazyListState.LoadMoreOnScroll(shelvesPager)

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = Theme.spacing._16),
        state = lazyListState
    ) {
        state.shelves.items.forEach { shelf ->
            stickyHeader(key = shelf.id) {
                ProductsHeader(
                    viewAllColor = Color(state.dukanInfo.color),
                    shelfName = shelf.name,
                    onClick = {
                        listener.onViewAllShelfProductsClicked(
                            shelf.id,
                            shelf.name
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = Theme.spacing._16)
                )
            }
            item {
                ProductsShelf(
                    shelf = shelf,
                    cartColor = Color(state.dukanInfo.color),
                    listener = listener
                )
            }
        }
    }
}

@Composable
private fun ProductsShelf(
    shelf: DukanDetailsUiState.ShelfUiState,
    listener: DukanDetailsInteractionListener,
    cartColor: Color? = null
) {
    val productPairs = remember(shelf.products) {
        shelf.products.chunked(2)
    }
    val lazyListState = rememberLazyListState()

    LazyRow(
        state = lazyListState,
        contentPadding = PaddingValues(Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {

        itemsIndexed(items = productPairs) { index, pair ->
            Column(
                modifier = Modifier.fillParentMaxWidth(if (index == productPairs.lastIndex) 1f else 0.95f),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
            ) {
                pair.forEach { product ->
                    key(product.id) {
                        ProductCard(
                            productName = product.name,
                            productImageUrl = product.imageUrl,
                            productDescription = product.description,
                            productPrice = product.price,
                            productCardBackground = Theme.colorScheme.background.surfaceLow,
                            productAction = {
                                CartOrQuantityProductComponent(
                                    showProductQuantity = product.showProductQuantity,
                                    cartColor = cartColor,
                                    onCartClick = {
                                        listener.onCartClick(product.id)
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

