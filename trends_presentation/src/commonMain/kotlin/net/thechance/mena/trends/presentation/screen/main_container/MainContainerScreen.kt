package net.thechance.mena.trends.presentation.screen.main_container

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.error_generic
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.component.LoadingProgressBar
import net.thechance.mena.trends.presentation.shared.component.TrendsAnimatedVisibility
import net.thechance.mena.trends.presentation.shared.model.SnackBarStatus
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import net.thechance.mena.trends.presentation.snackbar.LocalSnackbarController
import net.thechance.mena.trends.presentation.snackbar.SnackBarData
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MainContainerScreen(
    viewModel: MainContainerViewModel = koinViewModel()
) {
    val navController = LocalNavController.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            MainContainerEffect.NavigateToReelHome -> {
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

    MainContainerScreenContent(state = state)
}

@Composable
private fun MainContainerScreenContent(state: MainContainerState) {
    Scaffold(
        snakeBar = { ErrorSnackBar(state.error) }
    ) {
        TrendsAnimatedVisibility(
            visible = state.isCategoriesAlreadySelectedByUser == null
        ) {
            LoadingProgressBar()
        }
    }
}

@Composable
private fun ErrorSnackBar(
    error: ErrorState?
) {
    val snackBarController = LocalSnackbarController.current
    TrendsAnimatedVisibility(error != null) {
        snackBarController.showSnackBar(
            SnackBarData(
                message = stringResource(Res.string.error_generic),
                snackBarType = SnackBarStatus.Error,
            )
        )
    }
}
