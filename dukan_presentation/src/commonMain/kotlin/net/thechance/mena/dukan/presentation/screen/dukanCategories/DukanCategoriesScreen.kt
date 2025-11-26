package net.thechance.mena.dukan.presentation.screen.dukanCategories

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_to_main_screen_icon
import mena.dukan_presentation.generated.resources.categories
import mena.dukan_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.loading.LoadingDots
import net.thechance.mena.dukan.presentation.component.shared.CategoryCard
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.component.state.NoInternetContent
import net.thechance.mena.dukan.presentation.navigation.DukanRoute
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.animation.fadeWithSlideTransition
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanCategoriesInteractionListener
import net.thechance.mena.dukan.presentation.util.stubPreviews.previewCategories
import net.thechance.mena.dukan.presentation.viewModel.dukanCategories.DukanCategoriesEffects
import net.thechance.mena.dukan.presentation.viewModel.dukanCategories.DukanCategoriesInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanCategories.DukanCategoriesUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanCategories.DukanCategoriesUiState.CategoryUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanCategories.DukanCategoriesViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DukanCategoriesScreen(
    viewModel: DukanCategoriesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(effects = viewModel.effect) { effect ->
        when (effect) {
            DukanCategoriesEffects.NavigateBack -> navController.navigateUp()
            is DukanCategoriesEffects.NavigateToDukansOfCategory -> {
                navController.navigate(
                    DukanRoute.DukansScreenRoute(
                        categoryId = effect.categoryId,
                        categoryTitle = effect.categoryName
                    )
                )
            }
        }
    }

    DukanCategoriesContent(
        state = state,
        interactionListener = viewModel
    )
}


@Composable
private fun DukanCategoriesContent(
    state: DukanCategoriesUiState,
    interactionListener: DukanCategoriesInteractionListener
) {
    Scaffold(
        topBar = { CategoriesTopAppBar(onBackClick = interactionListener::onBackClicked) },
        snakeBar = { DukanCategoriesSnackBar(state, interactionListener::onDismissSnackBar) }
    ) {
        AnimatedContent(
            targetState = state.dukanCategoriesState,
            transitionSpec = { fadeWithSlideTransition() }
        ) { currentState ->
            when (currentState) {
                DukanCategoriesUiState.DukanCategoriesState.LOADING -> LoadingDots(modifier = Modifier.fillMaxSize())
                DukanCategoriesUiState.DukanCategoriesState.ERROR -> {
                    NoInternetContent(
                        onRetry = interactionListener::onRetryClicked,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                DukanCategoriesUiState.DukanCategoriesState.LOADED -> CategoriesList(
                    categories = state.categories,
                    onCategoryClick = interactionListener::onCategoryClicked
                )
            }
        }
    }
}

@Composable
private fun DukanCategoriesSnackBar(
    state: DukanCategoriesUiState,
    onDismissSnackBar: () -> Unit
) {
    state.snackBarUiState?.let { snackBarState ->
        SnackBar(
            snackBarUiState = snackBarState,
            onDismiss = onDismissSnackBar,
            onClick = onDismissSnackBar
        )
    }
}

@Composable
private fun CategoriesTopAppBar(
    onBackClick: () -> Unit
) {
    AppBar(
        title = stringResource(resource = Res.string.categories),
        titleColor = Theme.colorScheme.shadePrimary,
        modifier = Modifier
            .background(color = Theme.colorScheme.background.surface)
            .padding(top = Theme.spacing._16, bottom = Theme.spacing._8),
        contentPadding = PaddingValues(horizontal = Theme.spacing._16),
        leadingContent = {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_arrow_left),
                contentDescription = stringResource(resource = Res.string.back_to_main_screen_icon),
                tint = Theme.colorScheme.primary.primary
            )
        },
        onLeadingClick = onBackClick,
    )
}

@Composable
private fun CategoriesList(
    categories: List<CategoryUiState>,
    onCategoryClick: (categoryName: String, categoryId: String) -> Unit
) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = 76.dp),
        contentPadding = PaddingValues(
            start = Theme.spacing._16,
            end = Theme.spacing._16,
            top = Theme.spacing._12,
            bottom = Theme.spacing._16
        ),
        horizontalArrangement = Arrangement.Start,
    ) {
        items(
            items = categories,
            key = { it.id },
            contentType = { "CategoryCard" }
        ) { category ->
            CategoryCard(
                title = category.name,
                imageUrl = category.imageUrl,
                onClick = { onCategoryClick(category.name, category.id) },
            )
        }
    }
}

@Preview
@Composable
private fun CategoriesScreenPreview() {
    MenaTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            DukanCategoriesContent(
                state = DukanCategoriesUiState(categories = previewCategories),
                interactionListener = PreviewDukanCategoriesInteractionListener
            )
        }
    }
}