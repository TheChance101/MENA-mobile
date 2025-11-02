package net.thechance.mena.dukan.presentation.screen.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import net.thechance.mena.dukan.presentation.navigation.DukanRoute.ManageDukanScreenRoute
import net.thechance.mena.dukan.presentation.navigation.DukanRoute.PendingScreenRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.main.components.TopAppBar
import net.thechance.mena.dukan.presentation.screen.main.components.bestNersetDukanSection.BestNearestDukanSection
import net.thechance.mena.dukan.presentation.screen.main.components.categorySection.CategorySection
import net.thechance.mena.dukan.presentation.screen.main.components.categorySection.fakeCategories
import net.thechance.mena.dukan.presentation.screen.main.components.editorPickDukanSection.editorPickDukanItems
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
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


@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val state: State<MainScreenUiState> = viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current
    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            MainScreenEffect.NavigateToAddDukanScreen -> navController.navigate(DukanRoute.CreateDukanScreenRoute)
            MainScreenEffect.NavigateToPendingDukanScreen -> navController.navigate(
                PendingScreenRoute(
                    state.value.dukanState.name,
                )
            )

            MainScreenEffect.NavigateToManageDukanScreen -> navController.navigate(
                ManageDukanScreenRoute
            )

            MainScreenEffect.NavigateCategoryToScreen ->
                navController.navigate(DukanRoute.DukanCategoriesScreenRoute)

            is MainScreenEffect.NavigateToDukansScreenByCategory -> {
                navController.navigate(
                    DukanRoute.DukansScreenRoute(
                        categoryId = effect.categoryId,
                        categoryTitle = effect.categoryName
                    )
                )
            }

            is MainScreenEffect.NavigateSelectedDukan -> {
                navController.navigate(
                    DukanRoute.DukanDetails(effect.dukanId)
                )
            }
        }
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

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    onDukanIconClicked = listener::onDukanButtonClicked,
                    dukanButtonStatus = state.dukanState.status
                )
            },
            snakeBar = { ManageDukanSnackbar(state.snackBarState, listener) }
        ) {
            val isMainSectionsEmpty = state.categories.isEmpty() &&
                    bestNearestDukan.itemCount == 0 &&
                    dukans.itemCount == 0

            val isMainSectionsLoading = state.isCategoriesLoading ||
                    state.isBestNearestDukanLoading ||
                    state.isEditorPickDukanLoading

            if ( isMainSectionsEmpty && isMainSectionsLoading.not()) {
                EmptyStateContent(
                    image = Res.drawable.dukan_pending,
                    title = Res.string.dukan_main_content_empty_error_title,
                    body = Res.string.dukan_main_content_empty_error_body
                )
                return@Scaffold
            }

            MainScreenSections(
                state = state,
                bestNearestDukan = bestNearestDukan,
                dukans = dukans,
                listener = listener
            )
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
    LazyColumn {
        if (state.categories.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(Res.string.what_do_you_need),
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier.padding(
                        start = Theme.spacing._16,
                        bottom = Theme.spacing._8
                    )
                )

                CategorySection(
                    categories = state.categories,
                    onCategoryClick = listener::onCategorySelectedClicked,
                    onViewMoreClick = listener::onViewMoreClicked,
                )
            }
        }

        if ((bestNearestDukan.itemCount > 0)) {
            item {
                Text(
                    text = stringResource(Res.string.best_dukans_around_you),
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier.padding(
                        start = Theme.spacing._16,
                        top = Theme.spacing._16
                    )
                )

                BestNearestDukanSection(
                    dukans = bestNearestDukan,
                    onDukanClick = listener::onNearestDukanClicked,
                )
            }
        }

        if (dukans.itemCount > 0) {
            item {
                Text(
                    stringResource(Res.string.editor_pick_dukans),
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier.padding(
                        top = Theme.spacing._16,
                        start = Theme.spacing._16,
                        bottom = Theme.spacing._12
                    )
                )
            }
            editorPickDukanItems(
                dukans = dukans,
                onDukanClick = listener::onEditorPickDukanClicked
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
                    onDismiss = listener::onDismissSnackBar
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
                    editorPickDukans = flowOf(PagingData.from(fakeDukans()))
                ),
            )
        }
    }
}
