package net.thechance.mena.dukan.presentation.screen.categoryDukans.content

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.dukan_pending
import mena.dukan_presentation.generated.resources.no_dukans_body
import mena.dukan_presentation.generated.resources.no_dukans_title
import mena.dukan_presentation.generated.resources.no_result_found
import mena.dukan_presentation.generated.resources.no_result_found_body
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.util.AppTheme
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.component.state.EmptyStateContent
import net.thechance.mena.dukan.presentation.component.state.NoInternetContent
import net.thechance.mena.dukan.presentation.screen.categoryDukans.component.AnimatedCategorySearchHeader
import net.thechance.mena.dukan.presentation.screen.categoryDukans.component.CategoryDukansList
import net.thechance.mena.dukan.presentation.util.animation.fadeCubicTransition
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewCategoryDukansInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CategoryDukansContent(
    state: CategoryDukansUiState,
    listener: CategoryDukansInteractionListener,
) {
    val dukans = state.dukans.collectAsLazyPagingItems()
    Scaffold(
        topBar = {
            CategoryDukansAppBar(state, listener)
        },
        snakeBar = {
            state.snackBarUiState?.let { snackBarState ->
                SnackBar(
                    snackBarUiState = snackBarState,
                    onDismiss = listener::onSnackBarDismissed,
                    onClick = listener::onSnackBarDismissed
                )
            }
        }
    ) {
        AnimatedContent(
            targetState = dukans.loadState.refresh,
            transitionSpec = { fadeCubicTransition() },
            label = "Dukans Animation"
        ) { target ->
            when (target) {
                LoadState.Loading -> {
                    CategoryDukansList(
                        dukans = dukans,
                        listener = listener,
                        isLoading = true,
                    )
                }

                is LoadState.NotLoading -> {
                    if (dukans.itemCount == 0) {
                        EmptyStateContent(
                            image = Res.drawable.dukan_pending,
                            title = when (state.onSearchMode) {
                                true -> Res.string.no_result_found
                                false -> Res.string.no_dukans_title
                            },
                            body = when (state.onSearchMode) {
                                true -> Res.string.no_result_found_body
                                false -> Res.string.no_dukans_body
                            }
                        )
                    } else {
                        CategoryDukansList(
                            dukans = dukans,
                            listener = listener,
                        )
                    }
                }

                is LoadState.Error -> {
                    NoInternetContent(
                        onRetry = listener::onRetryClicked,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryDukansAppBar(
    state: CategoryDukansUiState,
    listener: CategoryDukansInteractionListener
) {
    AnimatedCategorySearchHeader(
        categoryTitle = state.categoryTitle,
        query = state.searchQuery,
        onQueryChange = listener::onSearchChanged,
        onClearClick = listener::onClearSearchClicked,
        onBackClick = listener::onBackClicked,
        onSearchMode = state.onSearchMode,
        onSearchIconClick = listener::onSearchIconClick
    )
}


@Preview
@Composable
private fun DukansContentPreview() {
    MenaTheme(appTheme = AppTheme.DARK.name) {
        CategoryDukansContent(
            state = CategoryDukansUiState(categoryTitle = "Dukan"),
            listener = PreviewCategoryDukansInteractionListener,
        )
    }
}