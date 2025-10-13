package net.thechance.mena.dukan.presentation.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.best_dukans_around_you
import mena.dukan_presentation.generated.resources.editor_pick_dukans
import mena.dukan_presentation.generated.resources.what_do_you_need
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.DukanRoute.ManageDukanScreenRoute
import net.thechance.mena.dukan.presentation.navigation.DukanRoute.PendingScreenRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.screen.main.components.TopAppBar
import net.thechance.mena.dukan.presentation.screen.main.components.bestNersetDukanSection.BestNearestDukanSection
import net.thechance.mena.dukan.presentation.screen.main.components.categorySection.CategorySection
import net.thechance.mena.dukan.presentation.screen.main.components.categorySection.fakeCategories
import net.thechance.mena.dukan.presentation.screen.main.components.editorPickDukanSection.EditorPickDukanItemsSection
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.pagination.Pager
import net.thechance.mena.dukan.presentation.util.pagination.PagingConfig
import net.thechance.mena.dukan.presentation.util.pagination.PagingData
import net.thechance.mena.dukan.presentation.util.stubPreviews.BestNearestDukanPagingSource
import net.thechance.mena.dukan.presentation.util.stubPreviews.EditorPickDukanItemsListPagingSource
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewMainScreenInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeBestNearestDuknas
import net.thechance.mena.dukan.presentation.util.stubPreviews.fakeDukans
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainEffect
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainScreenUiState
import net.thechance.mena.dukan.presentation.viewModel.mainScreen.MainViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current
    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            MainEffect.NavigateToAddDukanScreen -> navController.navigate(DukanRoute.CreateDukanScreenRoute)
            MainEffect.NavigateToPendingDukanScreen -> navController.navigate(
                PendingScreenRoute(
                    state.value.dukanState.name,
                )
            )

            MainEffect.NavigateToManageDukanScreen -> navController.navigate(ManageDukanScreenRoute)

            MainEffect.NavigateCategoryToScreen ->
                navController.navigate(DukanRoute.DukanCategoriesScreenRoute)

            is MainEffect.NavigateToDukansScreenByCategory -> {
                //ToDO: navigate to dukan screen by category
            }

            is MainEffect.NavigateSelectedDukan -> DukanRoute.DukanDetails(effect.dukanId)
        }
    }

    MainContent(
        listener = viewModel,
        state = state.value,
        editorPickDukanPager = viewModel.editorPickDukanPager,
        bestNearestDukanPager = viewModel.bestNearestDukanPager
    )
}

@Composable
private fun MainContent(
    listener: MainInteractionListener,
    state: MainScreenUiState,
    editorPickDukanPager: Pager<Int, MainScreenUiState.EditorPickDukanUiState>,
    bestNearestDukanPager: Pager<Int, MainScreenUiState.BestNearestDukanUiState>
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                onDukanIconClicked = listener::onDukanButtonClicked,
                dukanButtonStatus = state.dukanState.status
            )
        },
    ) {
        Column {
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
                onCategoryClick = listener::onCategorySelectedClick,
                onViewMoreClick = listener::onViewMoreButtonClick,
            )

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
                state = state,
                onDukanClick = listener::onNearestDukanClick,
                pager = bestNearestDukanPager,
                modifier = Modifier
                    .padding(
                        start = Theme.spacing._16,
                        top = Theme.spacing._8
                    ),
            )

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

            EditorPickDukanItemsSection(
                state = state,
                onDukanClick = listener::onEditorPickDukanClick,
                pager = editorPickDukanPager,
            )
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
                    bestNearestDukans = PagingData(items = fakeBestNearestDuknas()),
                    editorPickDukans = PagingData(items = fakeDukans())
                ),
                editorPickDukanPager = Pager(
                    config = PagingConfig(),
                    pagingSourceFactory = { EditorPickDukanItemsListPagingSource }
                ),
                bestNearestDukanPager = Pager(
                    config = PagingConfig(),
                    pagingSourceFactory = { BestNearestDukanPagingSource }
                )
            )
        }
    }
}
