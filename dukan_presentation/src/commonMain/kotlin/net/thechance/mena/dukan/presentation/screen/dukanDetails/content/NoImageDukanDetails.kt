package net.thechance.mena.dukan.presentation.screen.dukanDetails.content

import androidx.compose.runtime.Composable
import kotlinx.coroutines.FlowPreview
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageDukanDetails.NoImageDukanAppBar
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageDukanDetails.NoImageDukanShelves
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukanDetails
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoImageDukanDetails(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
) {
    OnSystemBackPressed(listener::onBackClicked)
    Scaffold(
        topBar = {
            NoImageDukanAppBar(
                state = state.dukanInfo,
                listener = listener
            )
        }
    ) {
        NoImageDukanContent(
            state = state,
            listener = listener,
        )
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun NoImageDukanContent(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
) {
    NoImageDukanShelves(
        state,
        listener,
    )
}

@Preview
@Composable
private fun NoImageDukanDetailsPreview() {
    MenaTheme {
        NoImageDukanDetails(
            state = fakeDukanDetails,
            listener = PreviewDukanDetailsInteractionListener,
        )
    }
}

@Preview
@Composable
private fun NoImageDukanDetailsLoadingPreview() {
    MenaTheme {
        NoImageDukanDetails(
            state = fakeDukanDetails.copy(),
            listener = PreviewDukanDetailsInteractionListener,
        )
    }
}