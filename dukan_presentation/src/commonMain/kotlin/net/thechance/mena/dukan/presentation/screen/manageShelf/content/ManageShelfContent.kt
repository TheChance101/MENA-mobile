package net.thechance.mena.dukan.presentation.screen.manageShelf.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.save
import mena.dukan_presentation.generated.resources.title
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.scaffold.Scaffold
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.component.shared.SnackBar
import net.thechance.mena.dukan.presentation.screen.manageShelf.component.ManageShelfAppBar
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewManageShelfInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.manageShelf.ManageShelfInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.manageShelf.ManageShelfUiState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ManageShelfContent(
    state: ManageShelfUiState,
    listener: ManageShelfInteractionListener
) {
    OnSystemBackPressed(listener::onBackClicked)

    Scaffold(
        topBar = {
            ManageShelfAppBar(
                onBackClicked = listener::onBackClicked,
                onDeleteClicked = listener::onDeleteClicked
            )
        },
        bottomBar = {
            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.spacing._16),
                text = stringResource(Res.string.save),
                onClick = listener::onSaveClicked,
                isEnabled = state.isSaveButtonEnabled,
                isLoading = state.isLoading,
                contentPadding = PaddingValues(vertical = Theme.spacing._12)
            )
        },
        snakeBar = {
            state.snackBarState?.let { snackBarState ->
                SnackBar(
                    snackBarUiState = snackBarState,
                    onDismiss = listener::onDismissSnackBar,
                    onClick = listener::onDismissSnackBar
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(Res.string.title),
                style = Theme.typography.title.small,
                color = Theme.colorScheme.shadePrimary,
                modifier = Modifier
                    .padding(horizontal = Theme.spacing._16)
                    .padding(top = Theme.spacing._16)
            )
            TextField(
                value = state.shelfTitle,
                onValueChanged = listener::onShelfNameChange,
                hint = "",
                modifier = Modifier.padding(
                    horizontal = Theme.spacing._16,
                    vertical = Theme.spacing._4
                ),
            )
        }
    }
}

@Preview
@Composable
private fun MyScreenPreview() {
    MenaTheme {
        ManageShelfContent(
            state = ManageShelfUiState(shelfTitle = "Clothes"),
            listener = PreviewManageShelfInteractionListener
        )
    }
}