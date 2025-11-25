package net.thechance.mena.dukan.presentation.screen.createShelf

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.create
import mena.dukan_presentation.generated.resources.create_shelf
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.title
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.navigation.LocalNavController
import net.thechance.mena.dukan.presentation.util.ObserveAsEffect
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewCreateShelfInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createShelf.CreateShelfEffect
import net.thechance.mena.dukan.presentation.viewModel.createShelf.CreateShelfInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.createShelf.CreateShelfUiState
import net.thechance.mena.dukan.presentation.viewModel.createShelf.CreateShelfViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateShelfScreen(
    viewModel: CreateShelfViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val navController = LocalNavController.current

    ObserveAsEffect(viewModel.effect) { effect ->
        when (effect) {
            CreateShelfEffect.NavigateBack -> navController.navigateUp()
            CreateShelfEffect.NavigateToManageDukan -> {
                val backStack = navController.previousBackStackEntry
                backStack?.savedStateHandle[CreateShelfArgs.IS_SHELF_CREATED] = true
                navController.popBackStack()
            }
        }
    }

    CreateShelfContent(
        state = state,
        interactionListener = viewModel
    )
}

@Composable
private fun CreateShelfContent(
    state: CreateShelfUiState,
    interactionListener: CreateShelfInteractionListener
) {
    OnSystemBackPressed(interactionListener::onBackClicked)
    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(Res.string.create_shelf),
                onLeadingClick = interactionListener::onBackClicked,
                contentPadding = PaddingValues(
                    horizontal = Theme.spacing._12,
                    vertical = Theme.spacing._8
                ),
                leadingContent = {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_left),
                        contentDescription = stringResource(Res.string.back_arrow),
                        tint = Theme.colorScheme.primary.primary
                    )
                }
            )
        },
        bottomBar = {
            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Theme.spacing._16, vertical = Theme.spacing._16),
                text = stringResource(Res.string.create),
                onClick = interactionListener::onCreateClicked,
                isEnabled = state.isCreateButtonEnabled,
                isLoading = state.isLoading,
                contentPadding = PaddingValues(vertical = Theme.spacing._12)
            )
        },
        snakeBar = {
            state.snackBarState?.let { snackBarState ->
                SnackBar(
                    snackBarUiState = snackBarState,
                    onDismiss = interactionListener::onDismissSnackBar,
                    onClick = interactionListener::onDismissSnackBar
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = Theme.spacing._16)
        ) {
            item {
                Text(
                    text = stringResource(Res.string.title),
                    style = Theme.typography.title.small,
                    color = Theme.colorScheme.shadePrimary,
                    modifier = Modifier.padding(
                        start = Theme.spacing._16,
                        top = Theme.spacing._24,
                        bottom = Theme.spacing._4
                    ),
                    textAlign = TextAlign.Start
                )
            }
            item {
                TextField(
                    value = state.shelfTitle,
                    onValueChanged = interactionListener::onTitleChanged,
                    modifier = Modifier
                        .padding(horizontal = Theme.spacing._16)
                        .padding(bottom = Theme.spacing._12),
                    hint = "",
                    maxCharacters = 50
                )
            }
        }
    }
}

@Preview
@Composable
private fun CreateShelfContentPreview() {
    MenaTheme {
        CreateShelfContent(
            state = CreateShelfUiState(),
            interactionListener = PreviewCreateShelfInteractionListener
        )
    }
}