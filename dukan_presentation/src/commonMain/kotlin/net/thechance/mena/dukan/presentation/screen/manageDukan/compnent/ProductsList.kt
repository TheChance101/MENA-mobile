package net.thechance.mena.dukan.presentation.screen.manageDukan.compnent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.productCard.EditProductIcon
import net.thechance.mena.dukan.presentation.component.productCard.ProductCard
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.stubPreviews.FakeProductPagingSource
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeProducts
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ProductUiState
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun ProductsList(
    products: List<ProductUiState>,
    pager: Pager<Int, ProductUiState>,
    modifier: Modifier = Modifier,
    onProductClick: (ProductUiState) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()

    lazyListState.LoadMoreOnScroll(pager)

    LazyColumn(
        modifier = modifier.fillMaxWidth()
            .padding(top = Theme.spacing._8),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._16,
            vertical = Theme.spacing._8
        ),
        state = lazyListState
    ) {
        items(products) { product ->
            ProductCard(
                modifier = Modifier.animateItem(),
                productName = product.name,
                productImageUrl = product.imageUrl,
                productDescription = product.description ?: "",
                productPrice = product.price,
                productCardBackground = Theme.colorScheme.background.surfaceLow,
                productAction = {
                    EditProductIcon(onClick = {
                        onProductClick(product)
                    })
                }
            )
        }
    }
}

@Preview
@Composable
private fun ProductsLayoutPreview() {
    MenaTheme {
        ProductsList(
            fakeProducts(),
            pager = Pager(
                config = PagingConfig(),
                pagingSourceFactory = { FakeProductPagingSource() }
            ),
        )
    }
}
