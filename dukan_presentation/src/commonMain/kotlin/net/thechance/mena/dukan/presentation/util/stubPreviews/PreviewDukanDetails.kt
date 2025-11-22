package net.thechance.mena.dukan.presentation.util.stubPreviews

import androidx.paging.PagingData
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.DukanInfo
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ProductUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState

val fakeDukanInfo = DukanInfo(
    name = "Calvin Klein store international",
    color = 0xFFFB5B5D,
    imageUrl = "https://dukan.photos/200"
)

val fakeProducts = listOf(
    ProductUiState(
        id = "products id 1",
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        basePrice = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg"
    ),
    ProductUiState(
        id = "products id 2",
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        basePrice = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg"
    ),
    ProductUiState(
        id = "products id 3",
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        basePrice = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg"
    )
)

val fakeShelves = listOf(
    ShelfUiState(
        id = "1",
        name = "Clothes"
    ),
    ShelfUiState(
        id = "2",
        name = "Perfumes"
    ),
    ShelfUiState(
        id = "3",
        name = "Accessories"
    ),
    ShelfUiState(
        id = "4",
        name = "Shoes"
    ),
    ShelfUiState(
        id = "5",
        name = "Bags"
    ),
    ShelfUiState(
        id = "6",
        name = "Electronics"
    ),
    ShelfUiState(
        id = "7",
        name = "Clearance"
    ),
    ShelfUiState(
        id = "8",
        name = "Books"
    )
)

val fakeProductsLimited = mapOf(
    "1" to fakeProducts,
    "2" to fakeProducts,
    "3" to fakeProducts,
    "4" to fakeProducts,
    "5" to fakeProducts,
    "6" to fakeProducts,
    "7" to fakeProducts,
    "8" to fakeProducts
)
val fakeDukanDetails = DukanDetailsUiState(
    dukanInfo = fakeDukanInfo,
    shelfIdSelected = "1",
    shelves = flowOf(PagingData.from(fakeShelves)),
    productsShelf = flowOf(PagingData.from(fakeProducts)),
    bestSellingProducts = flowOf(PagingData.from(fakeProducts)),
    shelfProductsLimited = fakeProductsLimited
)
