package net.thechance.mena.dukan.presentation.screen.dukanDetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.all
import mena.dukan_presentation.generated.resources.ic_arrow_right
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.productCard.ProductCard
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        contentPadding = PaddingValues(vertical = 16.dp),
        state = lazyListState
    ) {
        shelves.items.filter { it.products.isNotEmpty() }.forEach { shelf ->
            stickyHeader(key = shelf.id) {
                ShelfHeader(
                    state = state,
                    shelfName = shelf.name,
                    onViewAllClicked = {
                        listener.onViewAllShelfProductsClicked(
                            shelf.id,
                            shelf.name
                        )
                    },
                    modifier = Modifier
                        .background(Theme.colorScheme.background.surface)
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
    cartColor: Color? = null,
    listener: DukanDetailsInteractionListener
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

@Composable
private fun ShelfHeader(
    state: DukanDetailsUiState,
    shelfName: String,
    onViewAllClicked: () -> Unit,
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
                .weight(1f)
        )

        Text(
            text = stringResource(Res.string.all),
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
