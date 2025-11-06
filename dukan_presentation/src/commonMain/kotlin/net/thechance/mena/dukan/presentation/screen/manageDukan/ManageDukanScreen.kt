package net.thechance.mena.dukan.presentation.screen.manageDukan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_shelf_successfully
import mena.dukan_presentation.generated.resources.edit_shelf_successfully
import net.thechance.mena.dukan.presentation.component.shared.SnackBarType
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.createShelf.CreateShelfArgs
import net.thechance.mena.dukan.presentation.screen.manageDukan.content.ManageDukanContent
import net.thechance.mena.dukan.presentation.screen.manageShelf.ManageShelfArgs
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.ObserveSavedStateEvent
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiEffect
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ManageDukanScreen(
    viewModel: ManageDukanViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    navController.currentBackStackEntry?.apply {
        ObserveSavedStateEvent<Boolean>(CreateShelfArgs.IS_SHELF_CREATED) { isShelfCreated ->
            if (isShelfCreated) {
                viewModel.onShelfAdded(
                    message = Res.string.add_shelf_successfully,
                    type = SnackBarType.SUCCESS
                )
            }
        }
        ObserveSavedStateEvent<String>(ManageShelfArgs.deletedShelfId) { id ->
            viewModel.onShowDeleteShelfDialog(shelfId = id)
        }
        ObserveSavedStateEvent<Boolean>(ManageShelfArgs.isShelfNameEdited) { isEdited ->
            if (isEdited) {
                viewModel.onEditShelfName(
                    message = Res.string.edit_shelf_successfully,
                    snackBarType = SnackBarType.SUCCESS
                )
            }
        }
    }

    ObserveAsEffect(viewModel.effect) { effect: ManageDukanUiEffect ->
        when (effect) {
            ManageDukanUiEffect.NavigateBack -> navController.popBackStack()

            ManageDukanUiEffect.NavigateToAddShelf -> navController.navigate(
                DukanRoute.CreateShelfScreenRoute
            )

            is ManageDukanUiEffect.NavigateToManageShelf -> {
                navController.navigate(
                    DukanRoute.ManageShelfScreenRoute(
                        shelfId = effect.shelfId,
                        shelfTitle = effect.shelfTitle
                    )
                )
            }

            ManageDukanUiEffect.NavigateToAddProduct -> {
                navController.navigate(DukanRoute.CreateProductScreenRoute)
            }

            is ManageDukanUiEffect.NavigateToEditProduct -> {
                navController.navigate(
                    DukanRoute.EditProductScreenRoute(productId = effect.productId)
                )
            }

            ManageDukanUiEffect.NavigateToProductDetails -> {}
        }
    }
    ManageDukanContent(
        state = state,
        listener = viewModel,
    )
}