package net.thechance.mena.dukan.presentation.screen.createDukan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.createDukan.content.CreateDukanContent
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanEffect
import net.thechance.mena.dukan.presentation.viewModel.createDukan.CreateDukanViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateDukanScreen(
    viewModel: CreateDukanViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            CreateDukanEffect.NavigateBack -> navController.popBackStack()
            CreateDukanEffect.NavigateNext -> navController.navigate(DukanRoute.RequestPendingScreenRoute)
        }
    }

    CreateDukanContent(
        state = state,
        listener = viewModel
    )
}