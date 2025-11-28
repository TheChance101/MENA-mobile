package net.thechance.mena.admin_panel.presentation.screen.dukan_details.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.DukanDetailsInteractionListener
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.DukanDetailsScreenState
import net.thechance.mena.admin_panel.presentation.utils.PaginationTrigger
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
internal fun DukanDetailsInCompactMode(
    state: DukanDetailsScreenState,
    interactionListener: DukanDetailsInteractionListener,
    isMapVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(state.products.isEmpty()) {
        listState.scrollToItem(2)
    }

    PaginationTrigger(
        listState = listState,
        buffer = 5,
        loadNextItems = interactionListener::onNextProductsPageRequested
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            DukanDetailsCard(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                dukan = state.dukan,
                isLoading = state.isDukanDetailsLoading,
                isMapVisible = isMapVisible
            )
        }
        lazyShelvesDetailsCard(
            totalShelves = state.totalShelves,
            shelves = state.shelves,
            selectedShelf = state.selectedShelfId,
            onShelfClicked = interactionListener::onShelfSelected,
            onNextShelvesPageRequested = interactionListener::onNextShelvesPageRequested,
            products = state.products,
            isProductLoading = state.isProductsLoading,
            isShelvesLoading = state.isShelvesLoading
        )
    }
}