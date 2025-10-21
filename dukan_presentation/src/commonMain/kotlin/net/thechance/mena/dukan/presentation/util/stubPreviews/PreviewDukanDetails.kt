package net.thechance.mena.dukan.presentation.util.stubPreviews

import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.DukanInfo
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ProductUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelvesState

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
        price = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg"
    ),
    ProductUiState(
        id = "products id 2",
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        price = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg"
    ),
    ProductUiState(
        id = "products id 3",
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        price = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg"
    )
)

val fakeShelves = listOf(
    ShelfUiState(
        id = "1",
        name = "Clothes",
        products = fakeProducts
    ),
    ShelfUiState(
        id = "2",
        name = "Perfumes",
        products = fakeProducts
    ),
    ShelfUiState(
        id = "3",
        name = "Accessories",
        products = fakeProducts
    ),
    ShelfUiState(
        id = "4",
        name = "Shoes",
        products = fakeProducts
    ),
    ShelfUiState(
        id = "5",
        name = "Bags",
        products = fakeProducts
    ),
    ShelfUiState(
        id = "6",
        name = "Electronics",
        products = fakeProducts
    ),
    ShelfUiState(
        id = "7",
        name = "Clearance",
        products = fakeProducts
    ),
    ShelfUiState(
        id = "8",
        name = "Books",
        products = fakeProducts
    )
)

val fakeDukanDetails = DukanDetailsUiState(
    dukanInfo = fakeDukanInfo,
    shelvesState = ShelvesState.LOADED,
    shelfIdSelected = "1",
    shelves = PagingData(fakeShelves),
    productsState = DukanDetailsUiState.ProductsState.LOADED,
    productsShelf = PagingData(items = fakeProducts)
)

val fakePagerShelvesDukanDetails = createFakePager<Int, ShelfUiState>(fakeShelves)

val fakePagerProductsDukanDetails = createFakePager<Int, ProductUiState>(fakeProducts)