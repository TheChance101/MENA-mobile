@file:OptIn(ExperimentalUuidApi::class)

package net.thechance.mena.dukan.presentation.screen.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.best_dukans_around_you
import mena.dukan_presentation.generated.resources.dukan_main_content_empty_error_body
import mena.dukan_presentation.generated.resources.dukan_main_content_empty_error_title
import mena.dukan_presentation.generated.resources.dukan_pending
import mena.dukan_presentation.generated.resources.editor_pick_dukans
import mena.dukan_presentation.generated.resources.what_do_you_need
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.component.shared.SnackBarUiState
import net.thechance.mena.dukan.presentation.component.state.EmptyStateContent
import net.thechance.mena.dukan.presentation.component.state.NoInternetContent
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.DukanRoute.DukanDetails
import net.thechance.mena.dukan.presentation.navigation.DukanRoute.DukansScreenRoute
import net.thechance.mena.dukan.presentation.navigation.DukanRoute.ManageDukanScreenRoute
import net.thechance.mena.dukan.presentation.navigation.DukanRoute.PendingScreenRoute
import net.thechance.mena.dukan.presentation.navigation.DukanRoute.SearchScreenRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.dukanDetails.DukanDetailsArgs
import net.thechance.mena.dukan.presentation.screen.main.components.TopAppBar
import net.thechance.mena.dukan.presentation.screen.main.components.bestNersetDukanSection.BestNearestDukanSection
import net.thechance.mena.dukan.presentation.screen.main.components.categorySection.CategorySection
import net.thechance.mena.dukan.presentation.screen.main.components.categorySection.fakeCategories
import net.thechance.mena.dukan.presentation.screen.main.components.dukansDiscountSection.DukansDiscountSection
import net.thechance.mena.dukan.presentation.screen.main.components.editorPickDukanSection.editorPickDukanItems
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.ObserveSavedStateEvent
import net.thechance.mena.dukan.presentation.util.animation.fadeTransitionSpec
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewMainScreenInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeBestNearestDuknas
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukans
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenEffect
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi


@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val state: State<MainScreenUiState> = viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    navController.currentBackStackEntry?.apply {
        ObserveSavedStateEvent<String>(DukanDetailsArgs.DUKAN_ID) { dukanId ->
            viewModel.setFavoriteState(dukanId)
        }
    }

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            MainScreenEffect.NavigateToAddDukanScreen -> {
                navController.navigate(DukanRoute.CreateDukanScreenRoute)
            }

            MainScreenEffect.NavigateToPendingDukanScreen -> {
                navController.navigate(
                    PendingScreenRoute(state.value.dukanState.name)
                )
            }

            MainScreenEffect.NavigateToManageDukanScreen -> {
                navController.navigate(ManageDukanScreenRoute)
            }

            MainScreenEffect.NavigateToDukansCategoriesScreen -> {
                navController.navigate(DukanRoute.DukanCategoriesScreenRoute)
            }

            is MainScreenEffect.NavigateToDukansScreenByCategory -> {
                navController.navigate(
                    DukansScreenRoute(
                        categoryId = effect.categoryId,
                        categoryTitle = effect.categoryName
                    )
                )
            }

            is MainScreenEffect.NavigateToSelectedDukan -> {
                navController.navigate(DukanDetails(effect.dukanId))
            }

            MainScreenEffect.NavigateToSearchScreen -> {
                navController.navigate(route = SearchScreenRoute)
            }
        }
    }

    LaunchedEffect(state) {
        viewModel.getDukanState()
    }

    MainContent(
        listener = viewModel,
        state = state.value,
    )
}

@Composable
private fun MainContent(
    listener: MainInteractionListener,
    state: MainScreenUiState,
) {
    val dukans = state.editorPickDukans.collectAsLazyPagingItems()
    val bestNearestDukan = state.bestNearestDukans.collectAsLazyPagingItems()

    val editorLoaded by remember(dukans) {
        derivedStateOf { dukans.loadState.refresh is LoadState.NotLoading }
    }
    val nearestLoaded by remember(bestNearestDukan) {
        derivedStateOf { bestNearestDukan.loadState.refresh is LoadState.NotLoading }
    }

    val isEmptyContent by remember(
        state.isContentLoading,
        state.categories,
        dukans,
        bestNearestDukan
    ) {
        derivedStateOf {
            !state.isContentLoading &&
                    editorLoaded && nearestLoaded &&
                    state.categories.isEmpty() &&
                    dukans.itemCount == 0 &&
                    bestNearestDukan.itemCount == 0
        }
    }
    Scaffold(
        topBar = {
            AnimatedContent(
                targetState = state.isConnected,
                transitionSpec = { fadeTransitionSpec() }
            ) { isConnected ->
                if (!isConnected || isEmptyContent) return@AnimatedContent
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    dukanButtonStatus = state.dukanState.status,
                    onDukanIconClicked = listener::onDukanButtonClicked,
                    onSearchIconClicked = listener::onSearchButtonClicked
                )
            }
        },
        snakeBar = { ManageDukanSnackbar(state.snackBarState, listener) }
    ) {
        AnimatedContent(
            targetState = state.isConnected,
            transitionSpec = { fadeTransitionSpec() }
        ) { isConnected ->
            if (!isConnected) {
                NoInternetContent(
                    onRetry = listener::onRetryClicked,
                    modifier = Modifier.fillMaxSize()
                )
                return@AnimatedContent
            }
        }
        if (state.isConnected) {
            AnimatedContent(
                targetState = isEmptyContent,
                transitionSpec = { fadeTransitionSpec() }
            ) { isEmptyContent ->
                if (isEmptyContent) {
                    EmptyStateContent(
                        image = Res.drawable.dukan_pending,
                        title = Res.string.dukan_main_content_empty_error_title,
                        body = Res.string.dukan_main_content_empty_error_body
                    )
                } else {
                    MainScreenSections(
                        state = state,
                        bestNearestDukan = bestNearestDukan,
                        dukans = dukans,
                        listener = listener
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreenSections(
    state: MainScreenUiState,
    bestNearestDukan: LazyPagingItems<MainScreenUiState.BestNearestDukanUiState>,
    dukans: LazyPagingItems<MainScreenUiState.EditorPickDukanUiState>,
    listener: MainInteractionListener
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(328.dp),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing._8),
        contentPadding = PaddingValues(horizontal = Theme.spacing._16)
    ) {

        if (state.dukanTopDiscount.isNotEmpty()) {
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                DukansDiscountSection(
                    state = state.dukanTopDiscount,
                    onClick = listener::onShopNowClicked,
                    modifier = Modifier.padding(top = Theme.spacing._8, bottom = Theme.spacing._24)
                )
            }
        }

        if (state.categories.isNotEmpty()) {
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Column {
                    Text(
                        text = stringResource(Res.string.what_do_you_need),
                        style = Theme.typography.title.small,
                        color = Theme.colorScheme.shadePrimary,
                        modifier = Modifier.padding(bottom = Theme.spacing._8)
                    )

                    CategorySection(
                        categories = state.categories,
                        onCategoryClick = listener::onSelectedCategoryClicked,
                        onViewMoreClick = listener::onViewMoreClicked,
                    )
                }
            }
        }

        if (bestNearestDukan.itemCount > 0) {
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Column {
                    Text(
                        text = stringResource(Res.string.best_dukans_around_you),
                        style = Theme.typography.title.small,
                        color = Theme.colorScheme.shadePrimary,
                        modifier = Modifier.padding(top = Theme.spacing._16)
                    )

                    BestNearestDukanSection(
                        modifier = Modifier.padding(top = Theme.spacing._8),
                        dukans = bestNearestDukan,
                        onDukanClick = listener::onNearestDukanClicked,
                    )
                }
            }
        }

        if (dukans.itemCount > 0) {
            item(
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Text(
                    stringResource(Res.string.editor_pick_dukans),
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier.padding(
                        top = Theme.spacing._16,
                        bottom = Theme.spacing._4
                    )
                )
            }
            editorPickDukanItems(
                dukans = dukans,
                onDukanClick = listener::onEditorPickDukanClicked,
                onClickFavorite = listener::onFavoriteDukanClicked
            )

        }
    }
}


@Composable
private fun ManageDukanSnackbar(
    snackBarState: SnackBarUiState?,
    listener: MainInteractionListener
) {
    AnimatedContent(
        targetState = snackBarState != null,
        transitionSpec = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down
            ) togetherWith slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up
            )
        }
    ) {
        if (it) {
            snackBarState?.let {
                SnackBar(
                    snackBarUiState = snackBarState,
                    onDismiss = listener::onSnackBarDismissed,
                    onClick = listener::onSnackBarDismissed
                )
            }
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MenaTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colorScheme.background.surface),
            contentAlignment = Alignment.Center
        ) {
            MainContent(
                listener = PreviewMainScreenInteractionListener,
                state = MainScreenUiState(
                    categories = fakeCategories(),
                    bestNearestDukans = flowOf(PagingData.from(fakeBestNearestDuknas())),
                    editorPickDukans = flowOf(PagingData.from(fakeDukans())),
                    isConnected = true
                ),
            )
        }
    }
}
