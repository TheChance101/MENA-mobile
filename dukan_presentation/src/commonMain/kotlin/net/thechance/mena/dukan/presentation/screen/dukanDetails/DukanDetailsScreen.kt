package net.thechance.mena.dukan.presentation.screen.dukanDetails

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.dukan.presentation.component.loading.LoadingDots
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.dukanDetails.content.NoImageDukanDetails
import net.thechance.mena.dukan.presentation.screen.dukanDetails.content.SmallImageDukanDetails
import net.thechance.mena.dukan.presentation.screen.dukanDetails.content.WideImageDukanDetails
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsEffects
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DukanDetailsScreen(
    viewModel: DukanDetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            DukanDetailsEffects.NavigateBack -> navController.popBackStack()
            is DukanDetailsEffects.NavigateToViewAllShelfProducts -> navController.navigate(
                DukanRoute.ShelfDetails(effect.id, effect.name, effect.style, effect.color)
            )

            is DukanDetailsEffects.NavigateToViewDukanOnMap -> {

            }
        }
    }

    AnimatedContent(
        targetState = state.dukanDetailsState
    ) { targetState ->
        when (targetState) {
            DukanDetailsUiState.DukanDetailsState.LOADING -> LoadingDots(modifier = Modifier.fillMaxSize())
            else -> DukanDetailsContent(
                state = state,
                listener = viewModel
            )
        }
    }
}

@Composable
private fun DukanDetailsContent(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener
) {
    when (state.dukanInfo.style) {
        DukanDetailsUiState.Style.WIDE_IMAGE -> WideImageDukanDetails(
            state = state,
            listener = listener,
        )

        DukanDetailsUiState.Style.SMALL_IMAGE -> SmallImageDukanDetails(
            state = state,
            listener = listener,
        )

        DukanDetailsUiState.Style.NO_IMAGE -> NoImageDukanDetails(
            state = state,
            listener = listener,
        )
    }
}