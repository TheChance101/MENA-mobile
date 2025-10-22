package net.thechance.mena.dukan.presentation.screen.categoryDukans.content

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
import net.thechance.mena.dukan.presentation.component.shared.DukanCard
import net.thechance.mena.dukan.presentation.component.state.EmptyStateContent
import net.thechance.mena.dukan.presentation.component.shared.LazyVerticalGridItems
import net.thechance.mena.dukan.presentation.util.animation.fadeCubicTransition
import net.thechance.mena.dukan.presentation.util.pagination.LoadMoreOnScroll
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.stubPreviews.FakeDukanPagingSource
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewCategoryDukansInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansUiState
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansUiState.DukanUiState
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansUiState.DukansState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CategoryDukans(
    state: CategoryDukansUiState,
    listener: CategoryDukansInteractionListener,
    pager: Pager<Int, DukanUiState>
) {
    val lazyListState = rememberLazyListState()

    lazyListState.LoadMoreOnScroll(pager)

    Scaffold(
        topBar = { CategoryDukansAppBar(state, listener) }
    ) {
        AnimatedContent(
            targetState = state.dukansState,
            transitionSpec = { fadeCubicTransition() },
            label = "Dukans Animation"
        ) { target ->
            when (target) {
                DukansState.LOADING -> LazyVerticalGridItems(
                    items = state.dukans.items,
                    pager = pager,
                    itemContent = { dukan ->
                        DukanCard(
                            dukan = dukan,
                            isFavorite = dukan.isFavorite,
                            onClick = { listener.onDukanClick(dukan) },
                            onFavoriteClick = { listener.onFavoriteClick(dukan) },
                            isLoading = true
                        )
                    }
                )

                DukansState.LOADED -> LazyVerticalGridItems(
                    items = state.dukans.items,
                    pager = pager,
                    itemContent = { dukan ->
                        DukanCard(
                            dukan = dukan,
                            isFavorite = dukan.isFavorite,
                            onClick = { listener.onDukanClick(dukan) },
                            onFavoriteClick = { listener.onFavoriteClick(dukan) }
                        )
                    }
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

@Composable
private fun CategoryDukansAppBar(
    state: CategoryDukansUiState,
    listener: CategoryDukansInteractionListener
) {
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

@Preview
@Composable
private fun DukansContentPreview() {
    MenaTheme {
        CategoryDukans(
            state = CategoryDukansUiState(categoryTitle = "Dukan"),
            listener = PreviewCategoryDukansInteractionListener,
            pager = Pager(
                config = PagingConfig(),
                pagingSourceFactory = { FakeDukanPagingSource() }
            )
        )
    }
}