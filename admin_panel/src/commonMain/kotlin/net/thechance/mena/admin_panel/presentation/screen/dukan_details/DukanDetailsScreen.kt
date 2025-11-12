package net.thechance.mena.admin_panel.presentation.screen.dukan_details

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import net.thechance.mena.admin_panel.navigation.LocalNavController
import net.thechance.mena.admin_panel.presentation.component.PanelScaffold
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.component.DukanDetails
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.component.DukanDetailsAppBar
import net.thechance.mena.admin_panel.presentation.screen.dukan_details.component.ShelvesDetails
import net.thechance.mena.admin_panel.presentation.utils.ObserveAsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun DukanDetailsScreen(
    viewModel: DukanDetailsViewModel = koinViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val adminPanelNavController = LocalNavController.current

    ObserveAsEffect(
        effect = viewModel.uiEffect,
        onEffect = { effect -> onDukanDetailsEffect(effect, adminPanelNavController) }
    )

    DukanDetailsScreenContent(
        state = state,
        interactionListener = viewModel
    )
}

@Composable
private fun DukanDetailsScreenContent(
    state: DukanDetailsScreenState,
    interactionListener: DukanDetailsInteractionListener
){
    PanelScaffold(
        topBar = {
            DukanDetailsAppBar(
                onBackBtnClicked = interactionListener::onBackBtnClicked,
                dukanStatus = DukanDetailsScreenState.DukanStatus.ACTIVE,
                onChangeDukanStatusBtnClicked = interactionListener::onChangeDukanStatusBtnClicked
            )
        }
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            DukanDetails(
                dukanName = state.dukan.name,
                dukanCategories = state.dukan.categories,
                dukanLocation = state.dukan.address,
                dukanImg = state.dukan.imageUrl,
                modifier = Modifier.padding(end = 8.dp).weight(1f).fillMaxHeight()
            )
            ShelvesDetails(
                totalShelves = state.totalShelves,
                shelves = state.shelves,
                selectedShelf = state.selectedShelfId,
                onShelfClicked = interactionListener::onShelfSelected,
                onNextPageRequested = interactionListener::onNextShelvesPageRequested,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }
    }
}

private fun onDukanDetailsEffect(
    effect: DukanDetailEffect,
    navController: NavController
) {
    when (effect) {
        DukanDetailEffect.NavigateBack -> navController.popBackStack()
    }
}