package net.thechance.mena.dukan.presentation.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.DukanRoute.ManageDukanScreenRoute
import net.thechance.mena.dukan.presentation.navigation.DukanRoute.PendingScreenRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.main.components.TopAppBar
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainEffect
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current
    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            MainEffect.NavigateToAddDukanScreen -> navController.navigate(DukanRoute.CreateDukanScreenRoute)
            MainEffect.NavigateToPendingDukanScreen -> navController.navigate(
                PendingScreenRoute(
                    state.value.dukanState.name,
                )
            )

            MainEffect.NavigateToManageDukanScreen -> navController.navigate(ManageDukanScreenRoute)
        }
    }


    MainContent(
        listener = viewModel,
        state = state.value
    )
}

@Composable
private fun MainContent(
    listener: MainInteractionListener,
    state: MainScreenUiState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                onAddDukanIconClicked = listener::onDukanButtonClicked,
                dukanButtonStatus = state.dukanState.status
            )
        },
    ) {

    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colorScheme.background.surface),
            contentAlignment = Alignment.Center
        ) {
            MainContent(
                listener = object : MainInteractionListener {
                    override fun onDukanButtonClicked() {}
                },
                state = MainScreenUiState()
            )
        }
    }
}
