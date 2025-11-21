package net.thechance.mena.dukan.presentation.screen.manageDukan.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemKey
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.product.EditProductIcon
import net.thechance.mena.dukan.presentation.component.product.ProductCard
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeProducts
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState.ProductUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ManageDukanProductsList(
    products: LazyPagingItems<ProductUiState>,
    modifier: Modifier = Modifier,
    onProductClick: (ProductUiState) -> Unit = {},
    onEditProductClick: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Theme.spacing._4),
        contentPadding = PaddingValues(horizontal = Theme.spacing._16, vertical = Theme.spacing._8),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing._8)
    ) {
        items(
            count = products.itemCount,
            key = products.itemKey { it.id }
        ) { index ->
            products[index]?.let { product ->
                ProductCard(
                    modifier = Modifier.animateItem(),
                    productName = product.name,
                    productImageUrl = product.imageUrl,
                    productDescription = product.description.orEmpty(),
                    productPrice = product.price,
                    productCardBackground = Theme.colorScheme.background.surfaceLow,
                    productAction = { EditProductIcon(onClick = { onEditProductClick(product.id) }) },
                    onProductClick = { onProductClick(product) },
                    isOutOfStock = product.isOutOfStock
                )
            }
        }
    }
}

@Preview
@Composable
private fun ManageDukanProductsLayoutPreview() {
    MenaTheme {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(fakeProducts.size) { index ->
                val product = fakeProducts[index]
                ProductCard(
                    modifier = Modifier.padding(vertical = Theme.spacing._4),
                    productName = product.name,
                    productImageUrl = product.imageUrl,
                    productDescription = product.description,
                    productPrice = product.price,
                    productCardBackground = Theme.colorScheme.background.surfaceLow,
                    productAction = { EditProductIcon(onClick = {}) },
                    onProductClick = {},
                    isOutOfStock = product.isOutOfStock
                )
            }
        }
    }
}