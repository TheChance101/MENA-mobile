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
import net.thechance.mena.dukan.presentation.util.pagination.PagerOld
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
    pagerOldShelf: PagerOld<Int, ShelfUiState>,
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
            pagerOldShelves = pagerOldShelf
        )
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun NoImageDukanContent(
    state: DukanDetailsUiState,
    listener: DukanDetailsInteractionListener,
    pagerOldShelves: PagerOld<Int, ShelfUiState>
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
                pagerOldShelves
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
            pagerOldShelf = fakePagerShelvesDukanDetails
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
            pagerOldShelf = fakePagerShelvesDukanDetails
        )
    }
}