package net.thechance.mena.dukan.presentation.screen.manageShelf.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.back_arrow
import mena.dukan_presentation.generated.resources.ic_arrow_left
import mena.dukan_presentation.generated.resources.ic_delete
import mena.dukan_presentation.generated.resources.manage_shelf
import mena.dukan_presentation.generated.resources.save
import mena.dukan_presentation.generated.resources.title
import net.thechance.mena.designsystem.presentation.component.appBar.AppBar
import net.thechance.mena.designsystem.presentation.component.appBar.AppBarOptionContainer
import net.thechance.mena.designsystem.presentation.component.button.PrimaryButton
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.component.textField.TextField
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.util.OnSystemBackPressed
import net.thechance.mena.dukan.presentation.util.stubPreviews.PreviewManageShelfInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.manageShelf.ManageShelfInteractionListener
import net.thechance.mena.dukan.presentation.viewModel.manageShelf.ManageShelfUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ManageShelfContent(
    state: ManageShelfUiState,
    listener: ManageShelfInteractionListener
) {
    OnSystemBackPressed(listener::onBackClicked)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colorScheme.background.surface)
            .systemBarsPadding()
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            ManageShelfAppBar(listener)
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
                onValueChanged = {},
                hint = "",
                modifier = Modifier.padding(
                    horizontal = Theme.spacing._16,
                    vertical = Theme.spacing._4
                ),
                readOnly = true
            )
        }
        PrimaryButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.spacing._16),
            text = stringResource(Res.string.save),
            onClick = {},
            isEnabled = state.isButtonEnabled,
            contentPadding = PaddingValues(vertical = Theme.spacing._12)
        )
    }
}

@Composable
private fun ManageShelfAppBar(
    listener: ManageShelfInteractionListener
) {
    AppBar(
        title = stringResource(Res.string.manage_shelf),
        leadingContent = {
            Image(
                painter = painterResource(Res.drawable.ic_arrow_left),
                contentDescription = stringResource(Res.string.back_arrow)
            )
        },
        onLeadingClick = listener::onBackClicked,
        trailingContent = {
            AppBarOptionContainer(
                onClick = listener::onDeleteClicked,
                content = {
                    Image(
                        painter = painterResource(Res.drawable.ic_delete),
                        contentDescription = stringResource(Res.string.delete_icon),
                    )
                }
            )
        },
        contentPadding = PaddingValues(
            horizontal = Theme.spacing._12,
            vertical = Theme.spacing._8
        )
    )
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