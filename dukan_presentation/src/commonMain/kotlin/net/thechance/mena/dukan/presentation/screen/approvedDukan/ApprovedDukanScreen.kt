package net.thechance.mena.dukan.presentation.screen.approvedDukan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.approvedDukan.content.ApprovedDukanContent
import net.thechance.mena.dukan.presentation.screen.manageShelf.ManageShelfArgs
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

    val deletedShelfId = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>(ManageShelfArgs.deletedShelfId, null)
        ?.collectAsStateWithLifecycle()

    LaunchedEffect(deletedShelfId?.value != null) {
        viewModel.onShowDeleteShelfConfirmationDialog()
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.remove<String>(ManageShelfArgs.deletedShelfId)
    }

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            ApprovedDukanEffect.NavigateBack -> navController.popBackStack()

            ApprovedDukanEffect.NavigateToAddShelf -> navController.navigate(
                DukanRoute.CreateShelfScreenRoute
            )

            ApprovedDukanEffect.NavigateToEditShelf -> {
            }

            ApprovedDukanEffect.NavigateToAddProduct -> {
            }

            ApprovedDukanEffect.NavigateToProductDetails -> {
            }
        }
    }
    ApprovedDukanContent(
        state = state,
        listener = viewModel,
        deletedShelfId = deletedShelfId?.value
    )
}
