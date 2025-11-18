package net.thechance.mena.dukan.presentation.screen.productDetails

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.dukan.presentation.component.state.NoInternetContent
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.viewModel.productDetails.ProductDetailsEffects
import net.thechance.mena.dukan.presentation.viewModel.productDetails.ProductDetailsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductDetailsScreen(
    viewModel: ProductDetailsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    LaunchedEffect(Unit){
        viewModel.refreshCartInfo()
    }

    ObserveAsEffect(viewModel.effect) { effects ->
        when (effects) {
            ProductDetailsEffects.NavigateBack -> navController.popBackStack()
            is ProductDetailsEffects.NavigateToCart -> {
                navController.navigate(DukanRoute.DukanCart(effects.dukanId))
            }

            ProductDetailsEffects.NavigateToFavorites -> {}
        }
    }

    if (state.errorState != null) {
        NoInternetContent(
            onRetry = viewModel::onRetryClicked,
            isLoading = state.isLoading,
            modifier = Modifier
                .fillMaxSize()
        )
    } else {
        ProductDetailsContent(
            state = state,
            listener = viewModel,
        )
    }
}