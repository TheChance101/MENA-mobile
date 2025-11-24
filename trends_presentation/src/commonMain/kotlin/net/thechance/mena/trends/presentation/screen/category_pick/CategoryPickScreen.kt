package net.thechance.mena.trends.presentation.screen.category_pick

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.choose_interests
import mena.trends_presentation.generated.resources.help_text
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.base.ErrorState
import net.thechance.mena.trends.presentation.shared.component.CategoryItem
import net.thechance.mena.trends.presentation.shared.component.LoadingProgressBar
import net.thechance.mena.trends.presentation.shared.component.NextButton
import net.thechance.mena.trends.presentation.shared.component.NoConnection
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CategoryPickScreen(
    viewModel: CategoryPickViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(effects = viewModel.effect) { effect ->
        when (effect) {
            is CategoryPickScreenEffect.NavigateBack -> navController.popBackStack()
            is CategoryPickScreenEffect.NavigateToHome -> navController.navigate(Route.Home) {
                popUpTo(Route.Categories) { inclusive = true }
            }
        }
    }

    CategoryPickScreenContent(
        state = state,
        listener = viewModel
    )
}

@Composable
private fun CategoryPickScreenContent(
    state: CategoryPickScreenState,
    listener: CategoryPickInteractionListener
) {
    AnimatedVisibility(visible = !state.isLoading) {
        Scaffold(
            bottomBar = {
                NextButton(
                    onNextClick = listener::onClickNext,
                    isButtonEnabled = state.isNextButtonEnabled(),
                    isButtonLoading = state.isNextButtonLoading,
                    modifier = Modifier.padding(start = Theme.spacing._16, end = Theme.spacing._16, bottom = Theme.spacing._24)
                )
            }
        ) {
            AnimatedVisibility(
                visible = state.error is ErrorState.NoInternet,
                content = { NoConnection { listener.onClickRetry() } }
            )

            AnimatedVisibility(
                visible = state.isLoading,
                content = { LoadingProgressBar() }
            )

            AnimatedVisibility(
                visible = state.error == null && state.isLoading.not(),
                content = { CategoryPickScreenBody(state, listener) }
            )
        }
    }
}

@Composable
private fun CategoryPickScreenBody(
    state: CategoryPickScreenState,
    listener: CategoryPickInteractionListener
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .padding(horizontal = Theme.spacing._16)
    )
    {
        ChooseInterestsMessage()

        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = Theme.spacing._24)
        ) {
            state.categories.forEach { category ->
                CategoryItem(
                    category = category,
                    onClick = { id -> listener.onClickCategory(categoryId = id) },
                    modifier = Modifier.padding(
                        bottom = Theme.spacing._12,
                        end = Theme.spacing._8
                    )
                )
            }
        }
    }

    AnimatedVisibility(visible = state.isLoading) {
        LoadingProgressBar()
    }
}

@Composable
private fun ChooseInterestsMessage() {
    Text(
        text = stringResource(resource = Res.string.choose_interests),
        style = Theme.typography.title.medium,
        color = Theme.colorScheme.shadePrimary,
        modifier = Modifier.padding(bottom = Theme.spacing._4, top = 72.dp)
    )

    Text(
        text = stringResource(resource = Res.string.help_text),
        style = Theme.typography.body.small,
        color = Theme.colorScheme.shadeSecondary,
        modifier = Modifier.padding(bottom = Theme.spacing._24)
    )
}