package net.thechance.mena.trends.presentation.screen.update_categories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import mena.trends_presentation.generated.resources.change_tags
import mena.trends_presentation.generated.resources.choose_interests
import mena.trends_presentation.generated.resources.help_text
import mena.trends_presentation.generated.resources.save_change
import mena.trends_presentation.generated.resources.tags_updated_failure
import mena.trends_presentation.generated.resources.tags_updated_success
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
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
import net.thechance.mena.trends.presentation.shared.model.SnackBarStatus
import net.thechance.mena.trends.presentation.shared.util.ObserveAsEffect
import net.thechance.mena.trends.presentation.snackbar.LocalSnackbarController
import net.thechance.mena.trends.presentation.snackbar.SnackBarData
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun UpdateCategoriesScreen(
    viewModel: UpdateCategoriesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navController = LocalNavController.current
    val snackBarController = LocalSnackbarController.current

    ObserveAsEffect(effects = viewModel.effect) { effect ->
        when (effect) {
            is UpdateCategoriesScreenEffect.NavigateBack -> navController.popBackStack()
            is UpdateCategoriesScreenEffect.SaveFailure -> {
                snackBarController.showSnackBar(
                    SnackBarData(
                        message = getString(Res.string.tags_updated_failure),
                        snackBarType = SnackBarStatus.Error,
                    )
                )
            }

            is UpdateCategoriesScreenEffect.NavigateToTrendsAndShowSuccess -> {
                snackBarController.showSnackBar(
                    SnackBarData(
                        message = getString(Res.string.tags_updated_success),
                        snackBarType = SnackBarStatus.Success,
                    )
                )

                navController.navigate(Route.Home) {
                    popUpTo(Route.Home) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
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
    Scaffold(
        topBar = { ChangeTagsAppBar(onBackClick = listener::onClickBack) },
        bottomBar = {
            TrendsAnimatedVisibility(
                visible = state.errorState == null && state.isLoading.not(),
                content = {
                    SaveChangeButton(
                        onSaveClick = listener::onClickSave,
                        isButtonEnabled = state.saveButtonEnabled(),
                        isButtonLoading = state.isSaveButtonLoading,
                        modifier = Modifier
                            .padding(
                                start = Theme.spacing._16,
                                end = Theme.spacing._16,
                                bottom = Theme.spacing._24
                            )
                    )
                }
            )
        },
        content = {
            TrendsAnimatedVisibility(
                visible = state.isLoading,
                content = { LoadingProgressBar() }
            )

            TrendsAnimatedVisibility(
                visible = state.errorState == null && state.isLoading.not(),
                content = { UpdateCategoryScreenBody(listener, state) }
            )

            TrendsAnimatedVisibility(
                visible = state.errorState == ErrorState.NoInternet,
                content = { NoConnection { listener.onClickRetry() } }
            )
        }
    )
}

@Composable
private fun UpdateCategoryScreenBody(
    listener: UpdateCategoriesInteractionListener,
    state: UpdateCategoriesScreenState
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
                    modifier = Modifier.padding(
                        bottom = Theme.spacing._12,
                        end = Theme.spacing._8
                    )
                )
            }
        }
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
            BackIcon()
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

@Preview
@Composable
private fun UpdateCategoriesScreenPreview() {
    MenaTheme {
        UpdateCategoriesScreenContent(
            state = UpdateCategoriesScreenState(),
            listener = object : UpdateCategoriesInteractionListener {
                override fun onClickBack() {}
                override fun onClickRetry() {}
                override fun onClickCategory(categoryId: String) {}
                override fun onClickSave() {}
            }
        )
    }
}