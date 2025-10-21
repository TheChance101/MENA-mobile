package net.thechance.mena.dukan.presentation.screen.manageDukan.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.LazyVerticalGridItems
import net.thechance.mena.dukan.presentation.component.productCard.EditProductIcon
import net.thechance.mena.dukan.presentation.component.productCard.ProductCard
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.stubPreviews.FakeProductPagingSource
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeProducts
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState.ProductUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProductsList(
    products: List<ProductUiState>,
    pager: Pager<Int, ProductUiState>,
    modifier: Modifier = Modifier,
    onProductClick: (ProductUiState) -> Unit = {},
) {
    LazyVerticalGridItems(
        items = products,
        pager = pager,
        modifier = modifier.fillMaxWidth().padding(top = Theme.spacing._8),
    ) { product ->
        ProductCard(
            modifier = Modifier.animateItem(),
            productName = product.name,
            productImageUrl = product.imageUrl,
            productDescription = product.description ?: "",
            productPrice = product.price,
            productCardBackground = Theme.colorScheme.background.surfaceLow,
            productAction = { EditProductIcon(onClick = { onProductClick(product) }) }
        )
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
                pagingSourceFactory = { FakeProductPagingSource }
            ),
        )
    }
}
