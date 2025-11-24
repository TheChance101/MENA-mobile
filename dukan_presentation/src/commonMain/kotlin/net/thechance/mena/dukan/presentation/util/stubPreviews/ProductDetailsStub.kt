package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.viewModel.productDetails.ProductDetailsUiState

val fakeProductDetails = ProductDetailsUiState(
    product = ProductDetailsUiState.ProductInfo(
        id = "p123",
        name = "Classic Leather Jacket",
        basePrice = 149.99,
        description = "A high-quality classic leather jacket, perfect for all seasons. Made from 100% genuine leather with a comfortable inner lining.",
        images = listOf(
            "https.example.com/images/jacket1.jpg",
            "https.example.com/images/jacket2.jpg",
            "https.example.com/images/jacket3.jpg"
        )
    ),
    isLoading = false,
    isFavorite = true
)