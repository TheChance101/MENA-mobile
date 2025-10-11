package net.thechance.mena.dukan.presentation.screen.dukans.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.dukan.presentation.screen.dukans.compnent.DukansList
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.stubPreviews.FakeDukanPagingSource
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukansInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukans.DukanUiState
import net.thechance.mena.dukan.presentation.viewModel.dukans.DukansInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukans.DukansUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DukansContent(
    state: DukansUiState,
    listener: DukansInteractionListener,
    pager: Pager<Int, DukanUiState>,
    categoryTitle: String
) {
    val lazyListState = rememberLazyListState()

    lazyListState.LoadMoreOnScroll(pager)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppBar(
                title = categoryTitle,
                onLeadingClick = listener::onBackClick,
                leadingContent = {
                    net.thechance.mena.designsystem.presentation.component.icon.Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = "Back"
                    )
                }
            )

            DukansList(
                dukans = state.dukans.items,
                pager = pager,
                onDukanClick = listener::onDukanClick
            )
        }

    }
}

@Preview
@Composable
private fun DukansContentPreview() {
    MenaTheme {
        DukansContent(
            state = DukansUiState(),
            listener = PreviewDukansInteractionListener,
            pager = Pager(
                config = PagingConfig(),
                pagingSourceFactory = { FakeDukanPagingSource() }
            ),
            categoryTitle = "Dukan"
        )
    }
}
