package net.thechance.mena.faith.presentation.base

import mena.faith_presentation.generated.resources.Res
import mena.faith_presentation.generated.resources.default_snackbar_message
import org.jetbrains.compose.resources.StringResource

data class SnackBarState(
    val message: StringResource = Res.string.default_snackbar_message,
    val status: Status = Status.Success,
    val isVisible: Boolean = false
) {
    enum class Status {
        Success, Error
    }
}