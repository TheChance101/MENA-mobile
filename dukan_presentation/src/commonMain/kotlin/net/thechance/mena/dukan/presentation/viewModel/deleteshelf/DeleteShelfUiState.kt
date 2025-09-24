package net.thechance.mena.dukan.presentation.viewModel.deleteshelf

import mena.dukan_presentation.generated.resources.Res
import mena.dukan_presentation.generated.resources.delete
import mena.dukan_presentation.generated.resources.dismiss
import org.jetbrains.compose.resources.StringResource

data class DeleteShelfConfirmationDialogUiState(
    val title: String,
    val description: String,
    val type: ConfirmDialogType
)

enum class ConfirmDialogType(val text: StringResource) {
    DELETE(text = Res.string.delete),
    DISMISS(text =  Res.string.dismiss)
}