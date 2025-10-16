package net.thechance.mena.trends.presentation.screen.category_publish

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.add_categories_to_video
import mena.trends_presentation.generated.resources.back_arrow
import mena.trends_presentation.generated.resources.choose_categories
import mena.trends_presentation.generated.resources.ic_arrow_left
import mena.trends_presentation.generated.resources.ic_hint
import mena.trends_presentation.generated.resources.new_trend
import mena.trends_presentation.generated.resources.publish_hint
import mena.trends_presentation.generated.resources.upload_video
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.component.CategoryItem
import net.thechance.mena.trends.presentation.shared.component.UploadPageNumber
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CategoryPublishScreen(
    viewModel: CategoryPublishViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            is CategoryPublishEffect.NavigateBack -> navController.popBackStack()
            is CategoryPublishEffect.NavigateToTrends -> navController.navigate(Route.Trends){
                popUpTo(Route.MainContainer)
            }
        }
    }

    CategoryPublishContent(
        state = state,
        listener = viewModel
    )
}

@Composable
private fun CategoryPublishContent(
    state: CategoryPublishState,
    listener: CategoryPublishInteractionListener,
) {
    if (state.isLoading.not()) {
        Scaffold(
            topBar = { CategoryPublishAppBar(listener::onBackClick) },
            bottomBar = {
                PrimaryButton(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = Theme.spacing._16),
                    text = stringResource(resource = Res.string.upload_video),
                    onClick = listener::onPublishClick,
                    isEnabled = state.isPublishButtonEnabled,
                    isLoading = state.isPublishButtonLoadingVisible,
                    contentPadding = PaddingValues(vertical = 13.dp)
                )
            },
            modifier = Modifier.padding(bottom = Theme.spacing._24)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(Res.string.add_categories_to_video),
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier
                        .padding(
                            bottom = Theme.spacing._4,
                            start = Theme.spacing._16,
                            end = Theme.spacing._16,
                            top = Theme.spacing._16
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Theme.spacing._16)
                        .padding(bottom = Theme.spacing._24),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_hint),
                        contentDescription = stringResource(Res.string.publish_hint),
                        tint  = Theme.colorScheme.shadeSecondary,
                        modifier = Modifier.padding(end = Theme.spacing._2)
                    )

                    Text(
                        text = stringResource(Res.string.choose_categories),
                        style = Theme.typography.label.small,
                        color = Theme.colorScheme.shadeSecondary,
                        modifier = Modifier.padding(end = Theme.spacing._16)
                    )
                }

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Theme.spacing._16)
                ) {
                    state.categories.forEach { category ->
                        CategoryItem(
                            category = category,
                            onClick = { id -> listener.onCategoryClick(id) },
                            modifier = Modifier
                                .padding(bottom = Theme.spacing._12, end = Theme.spacing._8)
                        )
                    }
                }
            }
        }
    } else {
        LoadingProgressBar()
    }
}

@Composable
private fun CategoryPublishAppBar(
    onBackClick: () -> Unit
) {
    AppBar(
        onLeadingClick = onBackClick,
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back_arrow)
            )
        },
        title = stringResource(Res.string.new_trend),
        trailingContent = { UploadPageNumber(page = 3) }
    )
}

@Composable
private fun LoadingProgressBar() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface),
        contentAlignment = Alignment.Center
    ) {
        DotsProgressIndicator()
    }
}

@Preview
@Composable
private fun CategoryPublishScreenPreview() {
    MenaTheme {
        CategoryPublishContent(
            state = CategoryPublishState(),
            listener = object : CategoryPublishInteractionListener {
                override fun onBackClick() {}
                override fun onCategoryClick(categoryId: String) {}
                override fun onPublishClick() {}
            }
        )
    }
}