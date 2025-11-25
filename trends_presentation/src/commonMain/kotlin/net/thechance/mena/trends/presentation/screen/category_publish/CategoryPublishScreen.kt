package net.thechance.mena.trends.presentation.screen.category_publish

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.add_categories_to_video
import mena.trends_presentation.generated.resources.choose_categories
import mena.trends_presentation.generated.resources.ic_hint
import mena.trends_presentation.generated.resources.new_trend
import mena.trends_presentation.generated.resources.publish_hint
import mena.trends_presentation.generated.resources.upload_video
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.component.BackIcon
import net.thechance.mena.trends.presentation.shared.component.CategoryItem
import net.thechance.mena.trends.presentation.shared.component.LoadingProgressBar
import net.thechance.mena.trends.presentation.shared.component.NoConnection
import net.thechance.mena.trends.presentation.shared.component.TrendsAnimatedVisibility
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
            is CategoryPublishEffect.NavigateToHome -> navController.navigate(Route.Home) {
                popUpTo(Route.Home)
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
    TrendsAnimatedVisibility(visible = !state.isLoading) {
        Scaffold(
            topBar = { CategoryPublishAppBar(listener::onClickBack) },
            bottomBar = {
                if (state.error == null)
                    PrimaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = Theme.spacing._16,
                                end = Theme.spacing._16,
                                bottom = Theme.spacing._24
                            ),
                        text = stringResource(resource = Res.string.upload_video),
                        onClick = listener::onClickPublish,
                        isEnabled = state.isPublishButtonEnabled,
                        isLoading = state.isPublishButtonLoadingVisible,
                        contentPadding = PaddingValues(vertical = 13.dp)
                    )
            },
            content = {
                if (state.error == ErrorState.NoInternet) NoConnection(onRetry = listener::onClickRetry)
                else CategoryPublishScreenBody(state = state, listener = listener)
            }
        )
    }

    TrendsAnimatedVisibility(visible = state.isLoading) {
        LoadingProgressBar()
    }
}

@Composable
private fun CategoryPublishScreenBody(
    listener: CategoryPublishInteractionListener,
    state: CategoryPublishState
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
                tint = Theme.colorScheme.shadeSecondary,
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
                    onClick = { id -> listener.onClickCategory(id) },
                    modifier = Modifier
                        .padding(bottom = Theme.spacing._12, end = Theme.spacing._8)
                )
            }
        }
    }

    TrendsAnimatedVisibility(visible = state.isLoading) {
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
            BackIcon()
        },
        title = stringResource(Res.string.new_trend),
        trailingContent = { UploadPageNumber(page = 3) }
    )
}

@Preview(showBackground = true)
@Composable
private fun CategoryPublishScreenPreview() {
    MenaTheme {
        CategoryPublishScreenBody(
            state = CategoryPublishState(),
            listener = object : CategoryPublishInteractionListener {
                override fun onClickBack() {}
                override fun onClickCategory(categoryId: String) {}
                override fun onClickPublish() {}
                override fun onClickRetry() {}
            }
        )
    }
}