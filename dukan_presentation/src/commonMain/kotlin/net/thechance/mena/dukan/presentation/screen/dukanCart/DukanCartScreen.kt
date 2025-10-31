package net.thechance.mena.dukan.presentation.screen.dukanCart

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.empty_shelf
import mena.dukan_presentation.generated.resources.shelf_empty_body
import mena.dukan_presentation.generated.resources.shelf_empty_title
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.LoadingDots
import net.thechance.mena.dukan.presentation.component.state.EmptyStateContent
import net.thechance.mena.dukan.presentation.component.state.NoInternetContent
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.dukanCart.content.DukanCartContent
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartEffects
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartUiState.DukanCartState
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DukanCartScreen(viewModel: DukanCartViewModel = koinViewModel()) {
    OnSystemBackPressed { viewModel::onBackClick }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) {
        when (it) {
            DukanCartEffects.NavigateBack -> navController.popBackStack()
            is DukanCartEffects.NavigateToCheckout -> {}
            is DukanCartEffects.NavigateToDukanDetails ->
                navController.navigate(DukanRoute.DukanDetails(it.dukanId))
        }
    }

    AnimatedContent(
        targetState = state.dukanCartState
    ) { targetState ->
        when (targetState) {
            DukanCartState.LOADING -> LoadingDots(modifier = Modifier.fillMaxSize())
            DukanCartState.EMPTY -> EmptyStateContent(
                image = Res.drawable.empty_shelf,
                title = Res.string.shelf_empty_title,
                body = Res.string.shelf_empty_body,
                modifier = Modifier.fillMaxSize().padding(horizontal = Theme.spacing._16)
            )

            DukanCartState.ERROR -> NoInternetContent(
                onRetry = viewModel::onRetryLoadCartClick,
                modifier = Modifier.fillMaxSize().padding(horizontal = Theme.spacing._16)
            )

            DukanCartState.LOADED -> DukanCartContent(
                state = state,
                listener = viewModel
            )
        }
    }
}