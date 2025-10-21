package net.thechance.mena.dukan.presentation.screen.manageDukan.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.product.EditProductIcon
import net.thechance.mena.dukan.presentation.component.product.ProductCard
import net.thechance.mena.dukan.presentation.component.shared.LazyVerticalGridItems
import net.thechance.mena.dukan.presentation.util.pagination.PagerOld
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfigOld
import net.thechance.mena.dukan.presentation.util.stubPreviews.FakeProductPagingSourceOld
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeProducts
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState.ProductUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ManageDukanProductsList(
    products: List<ProductUiState>,
    pagerOld: PagerOld<Int, ProductUiState>,
    modifier: Modifier = Modifier,
    onProductClick: (ProductUiState) -> Unit = {},
) {
    LazyVerticalGridItems(
        items = products,
        pagerOld = pagerOld,
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
private fun ManageDukanProductsLayoutPreview() {
    MenaTheme {
        ManageDukanProductsList(
            fakeProducts(),
            pagerOld = PagerOld(
                config = PagingConfigOld(),
                pagingSourceOldFactory = { FakeProductPagingSourceOld }
            ),
        )
    }
}