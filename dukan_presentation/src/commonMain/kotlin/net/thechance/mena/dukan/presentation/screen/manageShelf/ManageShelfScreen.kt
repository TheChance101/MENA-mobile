package net.thechance.mena.dukan.presentation.screen.manageShelf

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.manageShelf.content.ManageShelfContent
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.viewModel.manageShelf.ManageShelfEffect
import net.thechance.mena.dukan.presentation.viewModel.manageShelf.ManageShelfViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ManageShelfScreen(
    viewModel: ManageShelfViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            ManageShelfEffect.NavigateBack -> navController.popBackStack()
            is ManageShelfEffect.NavigateBackWithShelfId -> {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(ManageShelfArgs.deletedShelfId, effect.shelfId)
                navController.popBackStack()
            }

            ManageShelfEffect.NavigateBackWithEditedShelfName -> {
                navController.previousBackStackEntry
                    ?.savedStateHandle?.set(
                        ManageShelfArgs.isShelfNameEdited,
                        true
                    )
                navController.popBackStack()
            }
        }
    }

    ManageShelfContent(
        state = state,
        listener = viewModel
    )
}