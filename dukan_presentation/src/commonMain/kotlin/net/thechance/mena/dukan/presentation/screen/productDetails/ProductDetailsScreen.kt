package net.thechance.mena.dukan.presentation.screen.productDetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    ObserveAsEffect(viewModel.effect) { effects ->
        when (effects) {
            ProductDetailsEffects.NavigateBack -> navController.popBackStack()
        }
    }

    ProductDetailsContent(
        state = state,
        listener = viewModel,
    )
}