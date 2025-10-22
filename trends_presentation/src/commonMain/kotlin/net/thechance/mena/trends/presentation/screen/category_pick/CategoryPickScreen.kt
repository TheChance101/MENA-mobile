package net.thechance.mena.trends.presentation.screen.category_pick

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import mena.trends_presentation.generated.resources.choose_interests
import mena.trends_presentation.generated.resources.help_text
import net.thechance.mena.designsystem.presentation.component.indicator.DotsProgressIndicator
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.trends.presentation.navigation.LocalNavController
import net.thechance.mena.trends.presentation.navigation.Route
import net.thechance.mena.trends.presentation.shared.component.CategoryItem
import net.thechance.mena.trends.presentation.shared.component.NextButton
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
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
            is CategoryPickScreenEffect.NavigateToHome -> navController.navigate(Route.ReelHome) {
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
    if (state.isLoading.not()) {
        Scaffold(
            bottomBar = {
                NextButton(
                    onNextClick = listener::onClickNext,
                    isButtonEnabled = state.isNextButtonEnabled(),
                    isButtonLoading = state.isNextButtonLoading,
                    modifier = Modifier.padding(horizontal = Theme.spacing._16)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())
                    .padding(horizontal = Theme.spacing._16)
            ) {
                ChooseInterestsMessage()
                FlowRow(
                    modifier = Modifier.fillMaxWidth().padding(bottom = Theme.spacing._24)
                ) {
                    state.categories.forEach { category ->
                        CategoryItem(
                            category = category,
                            onClick = { id -> listener.onClickCategory(categoryId = id) },
                            modifier = Modifier.padding(bottom = Theme.spacing._12, end = Theme.spacing._8)
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

@Composable
private fun LoadingProgressBar() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Theme.colorScheme.background.surface),
        contentAlignment = Alignment.Center
    ) {
        DotsProgressIndicator()
    }
}

@Preview
@Composable
private fun CategoryPickScreenPreview() {
    MenaTheme {
        CategoryPickScreenContent(
            state = CategoryPickScreenState(),
            listener = object : CategoryPickInteractionListener {
                override fun onClickBack() {}
                override fun onClickCategory(categoryId: String) {}
                override fun onClickNext() {}
            }
        )
    }
}