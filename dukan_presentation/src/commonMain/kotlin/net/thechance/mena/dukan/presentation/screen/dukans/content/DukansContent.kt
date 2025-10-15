package net.thechance.mena.dukan.presentation.screen.dukans.content

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.dukan_pending
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.no_dukans_body
import mena.dukan_presentation.generated.resources.no_dukans_title
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.dukan.presentation.component.EmptyStateContent
import net.thechance.mena.dukan.presentation.screen.dukans.component.DukansList
import net.thechance.mena.dukan.presentation.util.animation.fadeCubicTransition
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.stubPreviews.FakeDukanPagingSource
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukansInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukans.DukanUiState
import net.thechance.mena.dukan.presentation.viewModel.dukans.DukansInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukans.DukansState
import net.thechance.mena.dukan.presentation.viewModel.dukans.DukansUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DukansContent(
    state: DukansUiState,
    listener: DukansInteractionListener,
    pager: Pager<Int, DukanUiState>
) {
    val lazyListState = rememberLazyListState()

    lazyListState.LoadMoreOnScroll(pager)

    Scaffold(
        topBar = {
            AppBar(
                title = state.categoryTitle,
                onLeadingClick = listener::onBackClick,
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.back_arrow)
                    )
                }
            )
        }
    ) {
        AnimatedContent(
            targetState = state.dukansState,
            transitionSpec = { fadeCubicTransition() },
            label = "Dukans Animation"
        ) { target ->
            when (target) {
                DukansState.LOADING -> DukansList(
                    dukans = state.dukans.items,
                    pager = pager,
                    onDukanClick = listener::onDukanClick,
                    onFavoriteClick = listener::onFavoriteClick,
                    isLoading = true
                )

                DukansState.LOADED -> DukansList(
                    dukans = state.dukans.items,
                    pager = pager,
                    onDukanClick = listener::onDukanClick,
                    onFavoriteClick = listener::onFavoriteClick
                )

                DukansState.EMPTY -> EmptyStateContent(
                    image = Res.drawable.dukan_pending,
                    title = Res.string.no_dukans_title,
                    body = Res.string.no_dukans_body
                )
            }
        }
    }
}

@Preview
@Composable
private fun DukansContentPreview() {
    MenaTheme {
        DukansContent(
            state = DukansUiState(categoryTitle = "Dukan"),
            listener = PreviewDukansInteractionListener,
            pager = Pager(
                config = PagingConfig(),
                pagingSourceFactory = { FakeDukanPagingSource() }
            )
        )
    }
}
