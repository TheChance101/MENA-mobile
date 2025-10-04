package net.thechance.mena.dukan.presentation.screen.manageDukan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.add_shelf_successfully
import net.thechance.mena.dukan.presentation.component.SnackBarType
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.createShelf.CreateShelfArgs
import net.thechance.mena.dukan.presentation.screen.manageDukan.content.ManageDukanContent
import net.thechance.mena.dukan.presentation.screen.manageShelf.ManageShelfArgs
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.ObserveSavedStateEvent
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanEffect
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ManageDukanScreen(
    viewModel: ManageDukanViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    navController.currentBackStackEntry?.apply {
        ObserveSavedStateEvent<String>(CreateShelfArgs.createShelfSnackBar) { message ->
            viewModel.onShowSnackBar(
                message = Res.string.add_shelf_successfully,
                type = SnackBarType.SUCCESS
            )
        }
        ObserveSavedStateEvent<String>(ManageShelfArgs.deletedShelfId) { id ->
            viewModel.onShowDeleteShelfDailog(shelfId = id)
        }
    }

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            ManageDukanEffect.NavigateBack -> navController.popBackStack()

            ManageDukanEffect.NavigateToAddShelf -> navController.navigate(
                DukanRoute.CreateShelfScreenRoute
            )

            is ManageDukanEffect.NavigateToManageShelf -> {
                navController.navigate(
                    DukanRoute.ManageShelfScreenRoute(
                        shelfId = effect.shelfId,
                        shelfTitle = effect.shelfTitle
                    )
                )
            }

            ManageDukanEffect.NavigateToAddProduct -> {
                navController.navigate(DukanRoute.CreateProductScreenRoute)
            }

            ManageDukanEffect.NavigateToProductDetails -> {
            }
        }
    }
    ManageDukanContent(
        state = state,
        listener = viewModel,
        pager = viewModel.pager
    )
}
