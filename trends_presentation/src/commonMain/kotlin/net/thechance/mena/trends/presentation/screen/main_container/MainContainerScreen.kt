package net.thechance.mena.trends.presentation.screen.main_container

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.component.LoadingProgressBar
import net.thechance.mena.trends.presentation.shared.component.NoConnection
import net.thechance.mena.trends.presentation.shared.component.SomethingWentWrong
import net.thechance.mena.trends.presentation.shared.component.TrendsAnimatedVisibility
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MainContainerScreen(
    viewModel: MainContainerViewModel = koinViewModel()
) {
    val navController = LocalNavController.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            MainContainerEffect.NavigateToTrendHome -> {
                navController.navigate(Route.Home) {
                    popUpTo(Route.MainContainer) { inclusive = true }
                }
            }

            MainContainerEffect.NavigateToCategoryPick -> {
                navController.navigate(Route.Categories) {
                    popUpTo(Route.MainContainer) { inclusive = true }
                }
            }
        }
    }

    MainContainerScreenContent(state = state, listener = viewModel)
}

@Composable
private fun MainContainerScreenContent(
    state: MainContainerState,
    listener: MainContainerInteractionListener
) {
    Scaffold {
        TrendsAnimatedVisibility(
            visible = state.isCategoriesAlreadySelectedByUser == null
        ) {
            LoadingProgressBar()
        }

        TrendsAnimatedVisibility(state.error is ErrorState.RequestFailed) {
            SomethingWentWrong(onRetry = listener::onClickRetry)
        }

        TrendsAnimatedVisibility(state.error is ErrorState.NoInternet) {
            NoConnection(onRetry = listener::onClickRetry)
        }
    }
}