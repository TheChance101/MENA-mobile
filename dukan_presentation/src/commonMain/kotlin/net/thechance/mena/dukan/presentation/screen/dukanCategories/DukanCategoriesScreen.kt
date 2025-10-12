package net.thechance.mena.dukan.presentation.screen.dukanCategories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_to_main_screen_icon
import mena.dukan_presentation.generated.resources.categories
import mena.dukan_presentation.generated.resources.ic_arrow_left
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.CategoryCard
import net.thechance.mena.dukan.presentation.component.SnackBar
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewDukanCategoriesInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanCategories.CategoryUiState
import net.thechance.mena.dukan.presentation.viewModel.dukanCategories.DukanCategoriesEffects
import net.thechance.mena.dukan.presentation.viewModel.dukanCategories.DukanCategoriesInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.dukanCategories.DukanCategoriesUiState
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
                // Todo ( navigate to dukans under category screen )
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Theme.colorScheme.background.surface)
            .statusBarsPadding()
    ) {
        CategoriesTopAppBar(
            onBackClick = interactionListener::onBackClicked
        )

        CategoriesList(
            categories = state.categories,
            onCategoryClick = interactionListener::onCategoryClicked
        )
    }

    state.snackBarUiState?.let { snackBarState ->
        SnackBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Theme.spacing._24)
                .clip(RoundedCornerShape(Theme.radius.md))
                .clickable(onClick = interactionListener::onDismissSnackBar),
            onDismiss = interactionListener::onDismissSnackBar,
            snackBarUiState = snackBarState
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
private fun ColumnScope.CategoriesList(
    categories: List<CategoryUiState>,
    onCategoryClick: (categoryName: String,categoryId: String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = categoryItemSize),
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(
            start = Theme.spacing._16,
            end = Theme.spacing._16,
            top = Theme.spacing._12,
            bottom = Theme.spacing._16
        ),
        verticalArrangement = Arrangement.spacedBy(space = Theme.spacing._16),
        horizontalArrangement = Arrangement.spacedBy(space = Theme.spacing._4),
    ) {
        items(
            items = categories,
            key = { it.id },
            contentType = { "CategoryCard" }
        ) { category ->
            CategoryCard(
                title = category.name,
                imageUrl = category.imageUrl,
                onClick = { onCategoryClick(category.name,category.id) },
            )
        }
    }
}

private val categoryItemSize = 76.dp

@Preview
@Composable
private fun CategoriesScreenPreview() {
    MenaTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            DukanCategoriesContent(
                state = DukanCategoriesUiState(
                    categories = previewCategories
                ),
                interactionListener = PreviewDukanCategoriesInteractionListener
            )
        }
    }
}

private val previewCategories = listOf(
    CategoryUiState("1", "Electronics", "electronics.jpg"),
    CategoryUiState("2", "Clothing", "clothing.jpg"),
    CategoryUiState("3", "Home & Kitchen", "home_kitchen.jpg"),
    CategoryUiState("4", "Books", "books.jpg"),
    CategoryUiState("5", "Toys & Games", "toys_games.jpg"),
)