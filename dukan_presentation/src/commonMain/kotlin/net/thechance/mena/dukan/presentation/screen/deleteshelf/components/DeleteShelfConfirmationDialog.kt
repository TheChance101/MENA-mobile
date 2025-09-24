package net.thechance.mena.dukan.presentation.screen.deleteshelf.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.ic_cancel
import mena.dukan_presentation.generated.resources.ic_close_circle
import net.thechance.mena.designsystem.presentation.component.icon.Icon
import net.thechance.mena.designsystem.presentation.component.text.Text
import net.thechance.mena.designsystem.presentation.theme.theme.MenaTheme
import net.thechance.mena.designsystem.presentation.theme.theme.Theme
import net.thechance.mena.dukan.presentation.viewModel.deleteshelf.ConfirmDialogType
import net.thechance.mena.dukan.presentation.viewModel.deleteshelf.DeleteShelfConfirmationDialogUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DeleteShelfConfirmationDialog(
    state: DeleteShelfConfirmationDialogUiState,
    onClick: (ConfirmDialogType) -> Unit,
    modifier: Modifier = Modifier
) {

    Dialog(
        onDismissRequest = { onClick(ConfirmDialogType.DISMISS) },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = true
        ),
        content = {
            Box(
                modifier = modifier.clip(RoundedCornerShape(Theme.radius.xl))
                    .background(Theme.colorScheme.background.surfaceLow)
            ) {
                DeleteShelfConfirmationContent(state = state, onClick = onClick)
            }
        }
    )
}

@Composable
fun DeleteShelfConfirmationContent(
    state: DeleteShelfConfirmationDialogUiState,
    onClick: (ConfirmDialogType) -> Unit
) {
    val confirmTextColor by animateColorAsState(
        if (state.type == ConfirmDialogType.DELETE) Theme.colorScheme.error
        else Theme.colorScheme.primary.primary
    )

    Box(
        modifier = Modifier.padding(Theme.spacing._12)
            .clip(RoundedCornerShape(Theme.radius.full))
            .background(Theme.colorScheme.background.surface)
    ){
        Icon(
            painter = painterResource(Res.drawable.ic_cancel),
            contentDescription = "",
            modifier = Modifier.padding(Theme.spacing._8),
            tint = Theme.colorScheme.primary.primary
        )
    }
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = Theme.spacing._12, vertical = Theme.spacing._24),
    ) {
        Text(
            text = state.title,
            style = Theme.typography.title.medium,
            color = Theme.colorScheme.shadePrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = Theme.spacing._12)
        )
        Text(
            text = state.description,
            style = Theme.typography.body.small,
            color = Theme.colorScheme.shadeSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = Theme.spacing._24)
        )
        Text(
            text = stringResource(state.type.text),
            style = Theme.typography.label.medium,
            color = confirmTextColor,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth().clickable {
                onClick(state.type)
            }
        )
    }
}

@Preview
@Composable
private fun DeleteShelfConfirmationDialogPreview1() {
    MenaTheme {
        DeleteShelfConfirmationDialog(
            onClick = {},
            state = DeleteShelfConfirmationDialogUiState(
                title = "Delete shelf",
                description = "Are you sure you want to delete this shelf?",
                type = ConfirmDialogType.DELETE
            )
        )
    }
}

@Preview
@Composable
private fun DeleteShelfConfirmationDialogPreview2() {
    MenaTheme {
        DeleteShelfConfirmationDialog(
            onClick = {},
            state = DeleteShelfConfirmationDialogUiState(
                title = "Unable to Delete Shelf",
                description = "This shelf has a products inside it, you can’t delete it until remove products.",
                type = ConfirmDialogType.DISMISS
            )
        )
    }
}