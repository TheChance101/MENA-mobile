package net.thechance.mena.trends.presentation.screen.category_pick

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import mena.trends_presentation.generated.resources.choose_interests
import mena.trends_presentation.generated.resources.help_text
import mena.trends_presentation.generated.resources.next
import net.thechance.mena.designsystem.presentation.component.button.Button
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.component.CategoryItem
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CategoryPickScreen(
    viewModel: CategoryPickViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            is CategoryPickScreenEffect.NavigateBack -> navController.popBackStack()
            is CategoryPickScreenEffect.NavigateToTrends -> navController.navigate(Route.Trends)
        }
    }

    CategoryPickScreenContent(
        state = state,
        listener = viewModel,
    )
}

@Composable
private fun CategoryPickScreenContent(
    state: CategoryPickScreenState,
    listener: CategoryPickInteractionListener,
) {
    if (state.isLoading.not()) {
        Scaffold(
            bottomBar = {
                NextButton(
                    onNextClick = listener::onNextClick,
                    isButtonEnabled = state.isNextButtonEnabled(),
                    isButtonLoading = state.isNextButtonLoading,
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = stringResource(Res.string.choose_interests),
                    style = Theme.typography.title.medium,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier
                        .padding(
                            bottom = Theme.spacing._4,
                            start = Theme.spacing._16,
                            end = Theme.spacing._16,
                            top = 72.dp
                        )
                )

                Text(
                    text = stringResource(Res.string.help_text),
                    style = Theme.typography.body.small,
                    color = Theme.colorScheme.shadeSecondary,
                    modifier = Modifier
                        .padding(
                            bottom = Theme.spacing._24,
                            start = Theme.spacing._16,
                            end = Theme.spacing._16
                        )
                )

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = Theme.spacing._16,
                            end = Theme.spacing._16,
                            bottom = 100.dp
                        ),
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
private fun NextButton(
    onNextClick: () -> Unit,
    isButtonEnabled: Boolean,
    isButtonLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onNextClick,
        isEnabled = isButtonEnabled,
        isLoading = isButtonLoading,
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colorScheme.background.surface)
            .padding(
                start = Theme.spacing._16,
                end = Theme.spacing._16,
                bottom = Theme.spacing._24
            ),
        shape = RoundedCornerShape(Theme.radius.md),
        containerColor = Theme.colorScheme.primary.primary,
        disabledContainerColor = Theme.colorScheme.primary.primary.copy(alpha = 0.5f),
        contentColor = Theme.colorScheme.primary.onPrimary,
        disabledContentColor = Theme.colorScheme.primary.onPrimary.copy(alpha = 0.5f),
    ) { contentColor ->
        Text(
            text = stringResource(Res.string.next),
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