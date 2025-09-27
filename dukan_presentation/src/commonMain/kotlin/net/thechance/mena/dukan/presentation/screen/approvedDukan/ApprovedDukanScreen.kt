package net.thechance.mena.dukan.presentation.screen.approvedDukan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.approvedDukan.content.ApprovedDukanContent
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanEffect
import net.thechance.mena.dukan.presentation.viewModel.approvedDukan.ApprovedDukanViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ApprovedDukanScreen(
    viewModel: ApprovedDukanViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            ApprovedDukanEffect.NavigateBack -> navController.popBackStack()

            ApprovedDukanEffect.NavigateToAddShelf -> navController.navigate(
                DukanRoute.CreateShelfScreenRoute
            )

            ApprovedDukanEffect.NavigateToEditShelf -> {
                // TODO: Navigate to edit shelf screen
            }

            ApprovedDukanEffect.NavigateToAddProduct -> {
                // TODO: Navigate to add product screen
            }

            ApprovedDukanEffect.NavigateToProductDetails -> {
                // TODO: Navigate to product details screen
            }
        }
    }

    ApprovedDukanContent(
        state = state,
        listener = viewModel
    )
}
