package net.thechance.mena.admin_panel.presentation.screen.dukan_details.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.DukanDetailsInteractionListener
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.DukanDetailsScreenState
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
internal fun DukanDetailsInCompactMode(
    state: DukanDetailsScreenState,
    interactionListener: DukanDetailsInteractionListener,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(bottom = 16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DukanDetailsCard(
            modifier = Modifier.fillMaxWidth(),
            dukan = state.dukan,
            isLoading = state.isDukanDetailsLoading,
            isMapVisible = state.isMapVisible
        )
        ShelvesDetailsCard(
            modifier = Modifier.fillMaxWidth(),
            totalShelves = state.totalShelves,
            shelves = state.shelves,
            selectedShelf = state.selectedShelfId,
            onShelfClicked = interactionListener::onShelfSelected,
            onNextShelvesPageRequested = interactionListener::onNextShelvesPageRequested,
            products = state.products,
            onNextProductsPageRequested = interactionListener::onNextProductsPageRequested,
            isProductLoading = state.isProductsLoading,
            isShelvesLoading = state.isShelvesLoading
        )
    }
}