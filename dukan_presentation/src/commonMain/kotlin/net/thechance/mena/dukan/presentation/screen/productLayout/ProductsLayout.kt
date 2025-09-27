package net.thechance.mena.dukan.presentation.screen.productLayout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.dukan.presentation.component.productCard.EditProductIcon
import net.thechance.mena.dukan.presentation.component.productCard.ProductCardHorizontal
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun ProductsLayout(
    column: Int,
    products: List<ProductUiState>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(column),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(products) { product ->
            ProductCardHorizontal(
                productUiState = product,
                productAction = { EditProductIcon(onClick = {}) }
            )
        }
    }
}

@Preview
@Composable
private fun ProductsLayoutPreview() {
    MenaTheme {
        ProductsLayout(
            1,
            fakeProducts()
        )
    }
}

private fun fakeProducts(): List<ProductUiState> {
    return listOf(
        ProductUiState(
            id = "1",
            name = "Wireless Bluetooth Headphones",
            description = "Girls Crochet Tank Top description text here for this product",
            price = 79.99,
            imageUrl = "https://example.com/images/headphones1.jpg"
        ),
        ProductUiState(
            id = "2",
            name = "Smartphone Case",
            description = "Durable protective case for all models",
            price = 19.99,
            imageUrl = "https://example.com/images/case1.jpg"
        ),
        ProductUiState(
            id = "3",
            name = "Stainless Steel Water Bottle",
            price = 24.99,
            imageUrl = "https://example.com/images/bottle1.jpg"
        ),
        ProductUiState(
            id = "4",
            name = "Girls Crochet Tank Top",
            description = "Girls Crochet Tank Top description text here for this product",
            price = 15.99,
            imageUrl = "https://example.com/images/coffee1.jpg"
        ),
        ProductUiState(
            id = "5",
            name = "Girls Crochet Tank Top",
            description = "Girls Crochet Tank Top description text here for this product",
            price = 23.70,
            imageUrl = "https://example.com/images/mat1.jpg"
        )
    )
}