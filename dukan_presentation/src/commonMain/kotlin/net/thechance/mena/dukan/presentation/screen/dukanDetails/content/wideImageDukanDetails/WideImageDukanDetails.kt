package net.thechance.mena.dukan.presentation.screen.dukanDetails.content.wideImageDukanDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.createFakePager
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeProducts
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WideImageDukanDetails(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    pagerShelf: Pager<Int, DukanDetailsUiState.ShelfUiState>,
    pagerProduct: Pager<Int, DukanDetailsUiState.ProductUiState>
) {
    OnSystemBackPressed { listener::onBackClicked }
    val gridState = rememberLazyGridState()
    gridState.LoadMoreOnScroll(pagerProduct)
    Scaffold(
        topBar = {
            WideImageDukanDetailsAppBar(
                onBackClicked = listener::onBackClicked,
                onCartClicked = {}
            )
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Adaptive(minSize = 160.dp),
            contentPadding = PaddingValues(horizontal = Theme.spacing._16),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing._16),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                DukanHeader(state = state.dukanInfo)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                DukanShelvesSection(state = state, listener = listener, shelvesPager = pagerShelf)
            }
            ProductsGridSection(state = state)
        }
    }
}

@Preview(showBackground = true, name = "Full Screen Preview")
@Composable
private fun WideImageDukanDetailsPreview() {
    val mockShelves = listOf(
        DukanDetailsUiState.ShelfUiState(id = "1", name = "Fruits"),
        DukanDetailsUiState.ShelfUiState(id = "2", name = "Vegetables"),
        DukanDetailsUiState.ShelfUiState(id = "3", name = "Baked Goods"),
        DukanDetailsUiState.ShelfUiState(id = "4", name = "Dairy"),
    )

    val mockProducts = fakeProducts().map {
        DukanDetailsUiState.ProductUiState(
            id = it.id,
            name = it.name,
            imageUrl = it.imageUrl,
            price = it.price,
            description = it.description ?: ""
        )
    }
    val fakeShelfPager = createFakePager<Int, DukanDetailsUiState.ShelfUiState>(items = mockShelves)
    val fakeProductPager =
        createFakePager<Int, DukanDetailsUiState.ProductUiState>(items = mockProducts)
    val mockDukanState = DukanDetailsUiState(
        dukanInfo = DukanDetailsUiState.DukanInfo(
            name = "Sarah's Fresh Market",
            imageUrl = "https://via.placeholder.com/800x400/228B22/FFFFFF?text=Sarah's+Market",
            color = 0xFF228B22
        ),
        shelvesState = DukanDetailsUiState.ShelvesState.LOADED,
        shelves = PagingData(items = mockShelves),
        shelfIdSelected = "2",
        productsState = DukanDetailsUiState.ProductsState.LOADED,
        productsShelf = PagingData(items = mockProducts)
    )

    MenaTheme {
        WideImageDukanDetails(
            state = mockDukanState,
            listener = PreviewDukanDetailsInteractionListener,
            pagerShelf = fakeShelfPager,
            pagerProduct = fakeProductPager,
        )
    }
}