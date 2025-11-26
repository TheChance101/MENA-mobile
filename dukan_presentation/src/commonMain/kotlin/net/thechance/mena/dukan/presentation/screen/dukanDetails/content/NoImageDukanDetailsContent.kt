package net.thechance.mena.dukan.presentation.screen.dukanDetails.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.cash.paging.compose.collectAsLazyPagingItems
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.component.state.NoInternetContent
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageDukanDetails.NoImageDukanAppBar
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageDukanDetails.NoImageDukanShelvesContent
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukanDetails
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoImageDukanDetailsContent(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
) {
    val shelves = state.shelves.collectAsLazyPagingItems()


    OnSystemBackPressed(listener::onBackClicked)
    Scaffold(
        topBar = {
            NoImageDukanAppBar(
                state = state.dukanInfo,
                isBadgeVisible = state.hasProductInCart,
                listener = listener
            )
        },
        snakeBar = {
            state.snackBarState?.let { snackBarState ->
                SnackBar(
                    snackBarUiState = snackBarState,
                    onDismiss = listener::onDismissSnackBar,
                    onClick = listener::onDismissSnackBar
                )
            }
        }
    ) {
        if (state.dukanDetailsState == DukanDetailsUiState.DukanDetailsState.ERROR) {
            NoInternetContent(
                onRetry = listener::onRetryClicked,
                modifier = Modifier.fillMaxSize()
            )
            return@Scaffold
        }
        NoImageDukanShelvesContent(
            state = state,
            listener = listener,
            shelves = shelves
        )
    }
}

@Preview
@Composable
private fun NoImageDukanDetailsPreview() {
    MenaTheme {
        NoImageDukanDetailsContent(
            state = fakeDukanDetails,
            listener = PreviewDukanDetailsInteractionListener,
        )
    }
}