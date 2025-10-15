package net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageStyle

import androidx.compose.foundation.lazy.LazyListScope
import net.thechance.mena.dukan.presentation.screen.dukanDetails.shimmer.noImageDukanDetails.ShelfWithProductsShimmerNoImageDukan
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelvesState

private const val SHIMMER_SHELVES_COUNT = 4
fun LazyListScope.shelvesWithProductsNoImageDukan(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
) {
    when (state.shelvesState) {
        ShelvesState.LOADING -> {
            items(SHIMMER_SHELVES_COUNT) {
                ShelfWithProductsShimmerNoImageDukan()
            }
        }

        ShelvesState.LOADED -> {
            items(count = state.shelves.items.size, key = { state.shelves.items[it].id }) {
                val shelf = state.shelves.items[it]
                ShelfWithProductsNoImageDukan(
                    shelf = shelf,
                    listener = listener,
                    dukanColor = state.dukanInfo.color
                )

            }
        }

        ShelvesState.EMPTY -> {
        }
    }
}