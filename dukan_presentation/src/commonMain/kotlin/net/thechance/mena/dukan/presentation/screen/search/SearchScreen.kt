package net.thechance.mena.dukan.presentation.screen.search

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.img_not_found_search
import mena.dukan_presentation.generated.resources.img_start_search
import mena.dukan_presentation.generated.resources.no_result_found
import mena.dukan_presentation.generated.resources.no_result_found_body
import mena.dukan_presentation.generated.resources.start_search
import mena.dukan_presentation.generated.resources.start_search_body
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.search.component.SearchCompleteContent
import net.thechance.mena.dukan.presentation.screen.search.component.SearchEmptyContent
import net.thechance.mena.dukan.presentation.screen.sharedComponents.SearchHeader
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.animation.fadeTransitionSpec
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewSearchInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.search.SearchEffect
import net.thechance.mena.dukan.presentation.viewModel.search.SearchInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.search.SearchUiState
import net.thechance.mena.dukan.presentation.viewModel.search.SearchViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(viewModel: SearchViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(effects = viewModel.effect) { effect ->
        when (effect) {
            SearchEffect.NavigateBack -> navController.navigateUp()
            is SearchEffect.NavigateToDukanDetails -> {
                navController.navigate(route = DukanRoute.DukanDetails(effect.dukanId))
            }

            is SearchEffect.NavigateToProductDetails -> {
                navController.navigate(
                    route = DukanRoute.ProductDetails(
                        productId = effect.productId,
                        dukanId = effect.dukanId
                    )
                )
            }
        }
    }

    SearchContent(
        state = state,
        listener = viewModel
    )
}

@Composable
private fun SearchContent(
    state: SearchUiState,
    listener: SearchInteractionListener
) {
    Scaffold(
        modifier = Modifier,
        topBar = {
            SearchHeader(
                query = state.searchQuery,
                onQueryChange = listener::onSearchChanged,
                onBackClick = listener::onBackClicked,
                onClearClick = listener::onClearSearchClicked,
            )
        },
        snakeBar = {
            state.snackBarUiState?.let { snackBarUiState ->
                SnackBar(
                    snackBarUiState = snackBarUiState,
                    onDismiss = listener::onSnackBarDismissed
                )
            }
        }
    ) {
        AnimatedContent(
            targetState = state.searchContentState,
            label = "Search Content",
            transitionSpec = { fadeTransitionSpec() }
        ) { currentSearchState ->
            when (currentSearchState) {
                SearchUiState.SearchContentState.Idle -> SearchEmptyContent(
                    icon = painterResource(resource = Res.drawable.img_start_search),
                    title = stringResource(resource = Res.string.start_search),
                    body = stringResource(resource = Res.string.start_search_body)
                )

                SearchUiState.SearchContentState.Complete -> SearchCompleteContent(
                    state = state,
                    listener = listener
                )

                SearchUiState.SearchContentState.Empty -> {
                    SearchEmptyContent(
                        icon = painterResource(resource = Res.drawable.img_not_found_search),
                        title = stringResource(resource = Res.string.no_result_found),
                        body = stringResource(resource = Res.string.no_result_found_body)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    MenaTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            SearchContent(
                state = SearchUiState(searchContentState = SearchUiState.SearchContentState.Idle),
                listener = PreviewSearchInteractionListener
            )
        }
    }
}