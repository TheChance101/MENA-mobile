package net.thechance.mena.dukan.presentation.screen.manageDukan.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.manageDukan.ManageDukanUiState

@Composable
fun ManageDukanSnackbar(
    state: ManageDukanUiState,
    listener: ManageDukanInteractionListener
) {
    AnimatedContent(
        targetState = state.snackBarState != null,
        transitionSpec = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down
            ) togetherWith slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up
            )
        }
    ) {
        if (it) {
            state.snackBarState?.let {
                SnackBar(
                    snackBarUiState = state.snackBarState,
                    onDismiss = listener::onDismissSnackBar,
                    onClick = listener::onDismissSnackBar
                )
            }
        }
    }
}