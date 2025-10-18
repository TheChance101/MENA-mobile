package net.thechance.mena.dukan.presentation.screen.dukanDetails.content.noImageDukanDetails

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.FlowPreview
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.util.stubPreviews.FakeShelvesDukanDetailsPagingSource
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeShelves
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.DukanInfo
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelvesState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoImageDukanDetails(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    pagerShelves: Pager<Int, ShelfUiState>,
) {
    OnSystemBackPressed(listener::onBackClicked)
    Scaffold(
        topBar = {
            AppBarNoImageDukan(
                state = state.dukanInfo,
                listener = listener
            )
        }
    ) {
        NoImageDukanContent(
            state = state,
            listener = listener,
            pagerShelves = pagerShelves
        )
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun NoImageDukanContent(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    pagerShelves: Pager<Int, ShelfUiState>
) {


    AnimatedContent(
        targetState = state.shelvesState
    ) {
        when (it) {
            ShelvesState.LOADING -> {
                NoImageDukanShelvesProductsLoading()
            }

            ShelvesState.LOADED -> {
                NoImageDukanShelvesProducts(
                    state,
                    listener,
                    pagerShelves
                )
            }

            ShelvesState.EMPTY -> {}
        }
    }

}

@Preview
@Composable
private fun NoImageDukanDetailsPreview() {
    var previewState by remember {
        mutableStateOf(
            DukanDetailsUiState(
                DukanInfo(
                    name = "Calvin Klein store international",
                    color = 0xFFFB5B5D
                ),
                shelvesState = ShelvesState.LOADED,
                shelfIdSelected = "1",
                shelves = PagingData(fakeShelves())
            )
        )
    }

    val previewListener =
        object : DukanDetailsInteractionListener by PreviewDukanDetailsInteractionListener {
            override fun onShelfClicked(id: String) {
                previewState = previewState.copy(
                    shelfIdSelected = id,
                )
            }

            override fun onAddToCartClick(productId: String) {
                previewState = previewState.copy(
                    shelves = previewState.shelves.copy(
                        items = previewState.shelves.items.map { shelf ->
                            shelf.copy(
                                products = shelf.products.map { product ->
                                    if (product.id == productId) {
                                        product.copy(inCartQuantity = 1)
                                    } else {
                                        product
                                    }
                                }
                            )
                        }
                    )
                )
            }
        }
    MenaTheme {
        NoImageDukanDetails(
            state = previewState,
            listener = previewListener,
            pagerShelves = Pager(
                config = PagingConfig(),
                pagingSourceFactory = { FakeShelvesDukanDetailsPagingSource() }
            ),
        )
    }
}