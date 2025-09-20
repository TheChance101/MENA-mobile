package net.thechance.mena.trends.presentation.screen.main_container

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.designsystem.presentation.component.button.Button
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
            MainContainerEffect.NavigateToTrends -> {
                navController.navigate(Route.Trends)
            }

            MainContainerEffect.NavigateToCategoryPick -> {
                navController.navigate(Route.Categories)
            }

            MainContainerEffect.NavigateToManageTrends -> {
                navController.navigate(Route.ManageReels)
            }
        }
    }

    MainContainerScreenContent(
        state = state,
        onClickCategory = viewModel::navigateToCategories,
        onClickManageTrends = viewModel::navigateToManageTrends
    )
}

@Composable
private fun MainContainerScreenContent(
    state: MainContainerState,
    onClickCategory: () -> Unit, // TODO: REMOVE CALLBACK IN FUTURE
    onClickManageTrends: () -> Unit // TODO: REMOVE CALLBACK IN FUTURE
) {


    if (state.isCategoriesAlreadySelectedByUser != null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onClickCategory,
                ) {
                    Text("Category")
                }

                Button(
                    onClick = onClickManageTrends,
                ) {
                    Text("Mange Reels")
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colorScheme.background.surface.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}