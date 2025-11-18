package net.thechance.mena.dukan.presentation.screen.categoryDukans.content
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.dukan_pending
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_search
import mena.dukan_presentation.generated.resources.no_dukans_body
import mena.dukan_presentation.generated.resources.no_dukans_title
import mena.dukan_presentation.generated.resources.search_icon
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.designsystem.presentation.util.AppTheme
import net.thechance.mena.dukan.presentation.component.state.EmptyStateContent
import net.thechance.mena.dukan.presentation.component.state.NoInternetContent
import net.thechance.mena.dukan.presentation.screen.categoryDukans.component.CategoryDukansList
import net.thechance.mena.dukan.presentation.screen.sharedComponents.SearchHeader
import net.thechance.mena.dukan.presentation.util.animation.fadeCubicTransition
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewCategoryDukansInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.categoryDukans.CategoryDukansUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
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
                            title = Res.string.no_dukans_title,
                            body = Res.string.no_dukans_body
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
    var searchable by remember { mutableStateOf(false) }
    AnimatedContent(
        targetState = searchable,
        transitionSpec = { slideHorizontalTransition() },
        label = "Search Animation"
    ) {
        when (searchable) {
            true -> {
                SearchHeader(
                    query = "",
                    onQueryChange = {},
                    onBackClick = listener::onBackClicked,
                    onClearClick = {},
                )
            }

            false -> {
                AppBar(
                    title = state.categoryTitle,
                    onLeadingClick = listener::onBackClicked,
                    leadingContent = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_arrow_left),
                            contentDescription = stringResource(Res.string.back_arrow),
                            tint = Theme.colorScheme.primary.primary
                        )
                    },
                    trailingContent = {
                        AppBarOptionContainer(
                            onClick = { searchable = true },
                        ) {
                            Icon(
                                painter = painterResource(resource = Res.drawable.ic_search),
                                contentDescription = stringResource(resource = Res.string.search_icon),
                                modifier = Modifier.size(size = 20.dp),
                                tint = Theme.colorScheme.shadePrimary
                            )
                        }
                    }
                )
            }
        }
    }

}

fun slideHorizontalTransition(): ContentTransform {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },        // from right
        animationSpec = tween(300)
    ) togetherWith slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth / 2 },   // slide left
        animationSpec = tween(250)
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