package net.thechance.mena.dukan.presentation.screen.productLayout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.dukan.presentation.component.productCard.EditProductIcon
import net.thechance.mena.dukan.presentation.component.productCard.ProductCard
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun ProductsLayout(
    products: List<ProductUiState>
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(products) { product ->
            ProductCard(
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