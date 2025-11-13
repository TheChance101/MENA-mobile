package net.thechance.mena.admin_panel.presentation.screen.dukan_details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalAnimationApi::class)
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

        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ){
            val isCompact = maxWidth < 800.dp

            AnimatedContent(
                targetState = isCompact,
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter = fadeIn(tween(300)),
                        initialContentExit = fadeOut(tween(300))
                    )
                },
                label = "layoutTransition"
            ) { compact ->
                if (compact) {
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DukanDetails(
                            dukanName = state.dukan.name,
                            dukanCategories = state.dukan.categories,
                            dukanLocation = state.dukan.address,
                            dukanImg = state.dukan.imageUrl,
                            modifier = Modifier.fillMaxWidth()
                        )
                        ShelvesDetails(
                            totalShelves = state.totalShelves,
                            shelves = state.shelves,
                            selectedShelf = state.selectedShelfId,
                            onShelfClicked = interactionListener::onShelfSelected,
                            onNextShelvesPageRequested = interactionListener::onNextShelvesPageRequested,
                            products = state.products,
                            onNextProductsPageRequested = interactionListener::onNextProductsPageRequested,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                else{
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        DukanDetails(
                            dukanName = state.dukan.name,
                            dukanCategories = state.dukan.categories,
                            dukanLocation = state.dukan.address,
                            dukanImg = state.dukan.imageUrl,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .verticalScroll(rememberScrollState())
                        )
                        ShelvesDetails(
                            totalShelves = state.totalShelves,
                            shelves = state.shelves,
                            selectedShelf = state.selectedShelfId,
                            onShelfClicked = interactionListener::onShelfSelected,
                            onNextShelvesPageRequested = interactionListener::onNextShelvesPageRequested,
                            products = state.products,
                            onNextProductsPageRequested = interactionListener::onNextProductsPageRequested,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                }
            }
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