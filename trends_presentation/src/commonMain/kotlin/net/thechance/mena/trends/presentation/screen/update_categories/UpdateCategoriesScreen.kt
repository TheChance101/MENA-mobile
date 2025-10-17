package net.thechance.mena.trends.presentation.screen.update_categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mena.trends_presentation.generated.resources.Res
import mena.trends_presentation.generated.resources.back_arrow
import mena.trends_presentation.generated.resources.change_tags
import mena.trends_presentation.generated.resources.choose_interests
import mena.trends_presentation.generated.resources.help_text
import mena.trends_presentation.generated.resources.ic_arrow_left
import mena.trends_presentation.generated.resources.save_change
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
import net.thechance.mena.trends.presentation.shared.base.toStringResource
import net.thechance.mena.trends.presentation.shared.component.CategoryItem
import net.thechance.mena.trends.presentation.shared.component.snackbar.TrendsSnackBar
import net.thechance.mena.trends.presentation.shared.model.SnackBarStatus
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UpdateCategoriesScreen(
    viewModel: UpdateCategoriesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current

    ObserveAsEffect(effects = viewModel.effect) { effect ->
        when (effect) {
            is UpdateCategoriesScreenEffect.NavigateBack -> navController.popBackStack()
            is UpdateCategoriesScreenEffect.NavigateToTrends -> navController.navigate(Route.ReelHome)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getCategories()
    }

    UpdateCategoriesScreenContent(
        state = state,
        listener = viewModel
    )
}

@Composable
private fun UpdateCategoriesScreenContent(
    state: UpdateCategoriesScreenState,
    listener: UpdateCategoriesInteractionListener
) {
    if (state.isLoading.not()) {
        Scaffold(
            topBar = { ChangeTagsAppBar(onBackClick = listener::onBackClick) },
            bottomBar = {
                SaveChangeButton(
                    onSaveClick = listener::onSaveClick,
                    isButtonEnabled = state.saveButtonEnabled(),
                    isButtonLoading = state.isSaveButtonLoading,
                    modifier = Modifier
                        .padding(
                            start = Theme.spacing._16,
                            end = Theme.spacing._16,
                            bottom = Theme.spacing._24
                        )
                )
            },
            snakeBar = {
                state.errorState?.let { errorState ->
                    TrendsSnackBar(
                        message = stringResource(errorState.toStringResource()),
                        status = SnackBarStatus.Error
                    )
                }
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
                            onClick = { id -> listener.onCategoryClick(categoryId = id) },
                            modifier = Modifier.padding(
                                bottom = Theme.spacing._12,
                                end = Theme.spacing._8
                            )
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
private fun SaveChangeButton(
    isButtonEnabled: Boolean,
    isButtonLoading: Boolean,
    modifier: Modifier = Modifier,
    onSaveClick: () -> Unit
) {
    PrimaryButton(
        modifier = modifier.fillMaxWidth(),
        text = stringResource(resource = Res.string.save_change),
        onClick = onSaveClick,
        isEnabled = isButtonEnabled,
        isLoading = isButtonLoading,
        contentPadding = PaddingValues(vertical = 13.dp)
    )
}

@Composable
private fun ChangeTagsAppBar(onBackClick: () -> Unit) {
    AppBar(
        onLeadingClick = onBackClick,
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back_arrow)
            )
        },
        title = stringResource(Res.string.change_tags),
    )
}

@Composable
private fun ChooseInterestsMessage() {
    Text(
        text = stringResource(resource = Res.string.choose_interests),
        style = Theme.typography.title.medium,
        color = Theme.colorScheme.shadePrimary,
        modifier = Modifier.padding(bottom = Theme.spacing._4, top = Theme.spacing._16)
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
private fun UpdateCategoriesScreenPreview() {
    MenaTheme {
        UpdateCategoriesScreenContent(
            state = UpdateCategoriesScreenState(),
            listener = object : UpdateCategoriesInteractionListener {
                override fun onBackClick() {}
                override fun onCategoryClick(categoryId: String) {}
                override fun onSaveClick() {}
            }
        )
    }
}