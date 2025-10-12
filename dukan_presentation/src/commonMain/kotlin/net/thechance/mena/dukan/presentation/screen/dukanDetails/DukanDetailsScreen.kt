package net.thechance.mena.dukan.presentation.screen.dukanDetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.dukanDetails.content.NoImageDukanDetails
import net.thechance.mena.dukan.presentation.screen.dukanDetails.content.SmallImageDukanDetails
import net.thechance.mena.dukan.presentation.screen.dukanDetails.content.WideImageDukanDetails
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsEffects
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DukanDetailsScreen(
    dukanId: String,
    viewModel: DukanDetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            DukanDetailsEffects.NavigateBack -> navController.popBackStack()
            is DukanDetailsEffects.NavigateToViewAllShelfProducts -> navController.navigate(
                DukanRoute.ShelfDetails(effect.id, effect.name)
            )

            is DukanDetailsEffects.NavigateToViewDukanOnMap -> {

            }
        }
    }

    when (state.style) {
        DukanDetailsUiState.Style.WIDE_IMAGE -> WideImageDukanDetails(state, viewModel)
        DukanDetailsUiState.Style.SMALL_IMAGE -> SmallImageDukanDetails(state, viewModel)
        DukanDetailsUiState.Style.NO_IMAGE -> NoImageDukanDetails(state, viewModel)
    }
}