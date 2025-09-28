package net.thechance.mena.trends.presentation.screen.category_publish

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.add_categories_to_video
import mena.trends_presentation.generated.resources.back_arrow
import mena.trends_presentation.generated.resources.choose_categories
import mena.trends_presentation.generated.resources.ic_arrow_left
import mena.trends_presentation.generated.resources.ic_hint
import mena.trends_presentation.generated.resources.new_trend
import mena.trends_presentation.generated.resources.publish_categories_screen_count
import mena.trends_presentation.generated.resources.publish_hint
import mena.trends_presentation.generated.resources.publish_video
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.component.CategoryItem
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
            is CategoryPublishEffect.NavigateToTrends -> navController.navigate(
                route = Route.Trends
            )
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
            topBar = {
                CategoryPublishAppBar(
                    onBackClick = listener::onBackClick,
                    currentStepNumber = stringResource(Res.string.publish_categories_screen_count)
                )
            }
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
                        .padding(horizontal = Theme.spacing._16),
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

                Spacer(Modifier.weight(1f))

                PublishButton(
                    onPublishClick = { listener.onPublishClick() },
                    isButtonEnabled = state.isPublishButtonEnabled(),
                    isButtonLoading = state.isPublishButtonLoadingVisible,
                )
            }
        }
    } else {
        LoadingProgressBar()
    }
}

@Composable
private fun CategoryPublishAppBar(
    onBackClick: () -> Unit,
    currentStepNumber: String
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
        trailingContent = {
            Text(
                text = currentStepNumber,
                style = Theme.typography.label.small,
                color = Theme.colorScheme.shadePrimary,
                modifier = Modifier
                    .background(
                        shape = RoundedCornerShape(Theme.radius.full),
                        color = Theme.colorScheme.background.surfaceLow
                    )
                    .padding(horizontal = Theme.spacing._8, vertical = Theme.spacing._4)
            )
        }
    )
}

@Composable
private fun PublishButton(
    onPublishClick: () -> Unit,
    isButtonEnabled: Boolean,
    isButtonLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onPublishClick,
        isEnabled = isButtonEnabled,
        isLoading = isButtonLoading,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.spacing._16),
        shape = RoundedCornerShape(Theme.radius.md),
        containerColor = Theme.colorScheme.primary.primary,
        disabledContainerColor = Theme.colorScheme.primary.primary.copy(alpha = 0.5f),
        contentColor = Theme.colorScheme.primary.onPrimary,
        disabledContentColor = Theme.colorScheme.primary.onPrimary.copy(alpha = 0.5f),
    ) { contentColor ->
        Text(
            text = stringResource(Res.string.publish_video),
            color = contentColor,
            style = Theme.typography.label.medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(
                    vertical = 13.dp,
                    horizontal = Theme.spacing._24
                )
        )
    }
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