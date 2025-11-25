package net.thechance.mena.dukan.presentation.screen.dukanCart

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.LoadingDots
import net.thechance.mena.dukan.presentation.component.state.NoInternetContent
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.dukanCart.DukanCartArgs.PRODUCTS_CART
import net.thechance.mena.dukan.presentation.screen.dukanCart.content.DukanCartContent
import net.thechance.mena.dukan.presentation.screen.productDetails.ProductDetailsArgs
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartEffects
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartUiState.CartState
import net.thechance.mena.dukan.presentation.viewModel.dukanCart.DukanCartViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DukanCartScreen(viewModel: DukanCartViewModel = koinViewModel()) {
    OnSystemBackPressed(viewModel::onBackClicked)

    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) {
        when (it) {
            DukanCartEffects.NavigateBack -> {
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    PRODUCTS_CART,
                    state.productQuantity
                )
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    ProductDetailsArgs.HAS_PRODUCT_IN_CART,
                    state.totalPrice > 0
                )
                navController.popBackStack()
            }

            is DukanCartEffects.NavigateToCheckout ->
                navController.navigate(DukanRoute.CheckoutScreenRoute(it.dukanId))

            is DukanCartEffects.NavigateToDukanDetails ->
                navController.navigate(DukanRoute.DukanDetails(it.dukanId))
        }
    }

    AnimatedContent(
        targetState = state.cartState
    ) { targetState ->
        when (targetState) {
            CartState.LOADING -> LoadingDots(modifier = Modifier.fillMaxSize())
            CartState.NO_INTERNET -> NoInternetContent(
                onRetry = viewModel::onRetryLoadCartClicked,
                modifier = Modifier.fillMaxSize().padding(horizontal = Theme.spacing._16)
            )

            CartState.LOADED -> DukanCartContent(
                state = state,
                listener = viewModel
            )
        }
    }
}