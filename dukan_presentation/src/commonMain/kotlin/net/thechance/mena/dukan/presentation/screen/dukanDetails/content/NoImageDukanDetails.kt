package net.thechance.mena.dukan.presentation.screen.dukanDetails.content

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import kotlinx.coroutines.FlowPreview
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageDukanDetails.NoImageDukanAppBar
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageDukanDetails.NoImageDukanShelves
import net.thechance.mena.dukan.presentation.screen.dukanDetails.components.noImageDukanDetails.NoImageDukanShelvesSkeleton
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukanDetails
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakePagerShelvesDukanDetails
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelfUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanDetails.DukanDetailsUiState.ShelvesState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoImageDukanDetails(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    pagerShelf: Pager<Int, ShelfUiState>,
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
            pagerShelves = pagerShelf
        )
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun NoImageDukanContent(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    pagerShelves: Pager<Int, ShelfUiState>
) {
    AnimatedContent(
        targetState = state.shelvesState,
        label = "Shelves Animation",
    ) { shelvesState ->
        when (shelvesState) {
            ShelvesState.LOADING -> NoImageDukanShelvesSkeleton()

            ShelvesState.LOADED -> NoImageDukanShelves(
                state,
                listener,
                pagerShelves
            )

            ShelvesState.EMPTY -> {}
        }
    }

}

@Preview
@Composable
private fun NoImageDukanDetailsPreview() {
    MenaTheme {
        NoImageDukanDetails(
            state = fakeDukanDetails,
            listener = PreviewDukanDetailsInteractionListener,
            pagerShelf = fakePagerShelvesDukanDetails
        )
    }
}

@Preview
@Composable
private fun NoImageDukanDetailsLoadingPreview() {
    MenaTheme {
        NoImageDukanDetails(
            state = fakeDukanDetails.copy(shelvesState = ShelvesState.LOADING),
            listener = PreviewDukanDetailsInteractionListener,
            pagerShelf = fakePagerShelvesDukanDetails
        )
    }
}