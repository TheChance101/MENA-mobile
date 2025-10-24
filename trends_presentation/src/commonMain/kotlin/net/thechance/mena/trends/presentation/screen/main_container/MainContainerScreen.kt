package net.thechance.mena.trends.presentation.screen.main_container

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
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
    AnimatedVisibility(
        visible = state.isCategoriesAlreadySelectedByUser == null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colorScheme.background.surface.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            DotsProgressIndicator()
        }
    }
}
