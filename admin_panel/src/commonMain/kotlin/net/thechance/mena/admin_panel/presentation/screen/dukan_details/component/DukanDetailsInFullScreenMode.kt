package net.thechance.mena.admin_panel.presentation.screen.dukan_details.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
internal fun DukanDetailsInFullScreenMode(
    state: DukanDetailsScreenState,
    interactionListener: DukanDetailsInteractionListener,
    isMapVisible: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(bottom = 16.dp)
            .fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        DukanDetailsCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            dukan = state.dukan,
            isLoading = state.isDukanDetailsLoading,
            isMapVisible = isMapVisible
        )
        ShelvesDetailsCard(
            modifier = Modifier.weight(1f).fillMaxHeight(),
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