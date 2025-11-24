package net.thechance.mena.dukan.presentation.screen.dukanDetails

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.dukan.presentation.component.loading.LoadingDots
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.DukanRoute.DukanCart
import net.thechance.mena.dukan.presentation.navigation.DukanRoute.ShelfDetails
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.dukanDetails.content.NoImageDukanDetailsContent
import net.thechance.mena.dukan.presentation.screen.dukanDetails.content.SmallImageDukanDetailsContent
import net.thechance.mena.dukan.presentation.screen.dukanDetails.content.WideImageDukanDetailsContent
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
    LaunchedEffect(Unit) {
        viewModel.refreshProducts()
    }

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            DukanDetailsEffects.NavigateBackWithDukanId -> {
                if (state.isFavoritePressed) {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        DukanDetailsArgs.DUKAN_ID,
                        state.dukanInfo.dukanId
                    )
                }
                navController.popBackStack()
            }

            is DukanDetailsEffects.NavigateToViewAllShelfProducts -> navController.navigate(
                ShelfDetails(effect.id, effect.name, effect.dukanId)
            )

            is DukanDetailsEffects.NavigateToViewDukanOnMap -> navController.navigate(
                DukanRoute.DukanLocation(
                    latitude = effect.latitude,
                    longitude = effect.longitude
                )
            )

            is DukanDetailsEffects.NavigateToProductDetails -> navController.navigate(
                DukanRoute.ProductDetails(productId = effect.productId, dukanId = effect.dukanId)
            )

            is DukanDetailsEffects.NavigateToCart ->
                navController.navigate(DukanCart(effect.dukanId))
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
        DukanDetailsUiState.Style.WIDE_IMAGE -> WideImageDukanDetailsContent(
            state = state,
            listener = listener
        )

        DukanDetailsUiState.Style.SMALL_IMAGE -> SmallImageDukanDetailsContent(
            state = state,
            listener = listener,
        )

        DukanDetailsUiState.Style.NO_IMAGE -> NoImageDukanDetailsContent(
            state = state,
            listener = listener,
        )
    }
}