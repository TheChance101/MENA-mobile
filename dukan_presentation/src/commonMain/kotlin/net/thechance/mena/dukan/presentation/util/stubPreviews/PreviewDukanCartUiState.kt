package net.thechance.mena.dukan.presentation.util.stubPreviews

import androidx.paging.PagingData
import kotlinx.coroutines.flow.flowOf
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartUiState.DukanCartState
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartUiState.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartUiState.ProductsUiState

val fakeProductsDukanCart = listOf(
    ProductsUiState(
        id = "products id 1",
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        price = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg",
        quantity = 2
    ),
    ProductsUiState(
        id = "products id 2",
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        price = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg",
        quantity = 1
    ),
    ProductsUiState(
        id = "products id 3",
        name = "Girls Crochet Tank Top",
        price = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg",
        quantity = 1
    ),
    ProductsUiState(
        id = "products id 4",
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        price = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg",
        quantity = 1
    ),
    ProductsUiState(
        id = "products id 5",
        name = "Girls Crochet Tank Top",
        price = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg",
        quantity = 1
    ),
    ProductsUiState(
        id = "products id 6",
        name = "Girls Crochet Tank Top",
        description = "Girls Crochet Tank Top description text here for this productGirls Crochet Tank Top",
        price = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg",
        quantity = 1
    ),
    ProductsUiState(
        id = "products id 7",
        name = "Girls Crochet Tank Top",
        price = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg",
        quantity = 1
    ),
    ProductsUiState(
        id = "products id 8",
        name = "Girls Crochet Tank Top",
        price = 23.99,
        imageUrl = "https://m.media-amazon.com/images/I/61CRq2R6i4L._AC_SL1024_.jpg",
        quantity = 1
    )
)

val dukanCartUiState = DukanCartUiState(
    dukanCartState = DukanCartState.LOADED,
    totalPrice = 15.99,
    dukanDetails = DukanDetailsUiState(
        id = "dukan1",
        name = "Calvin Klein store ",
        imageUrl = "https://dukan.photos/200"
    ),
    products = flowOf(PagingData.from(fakeProductsDukanCart))
)