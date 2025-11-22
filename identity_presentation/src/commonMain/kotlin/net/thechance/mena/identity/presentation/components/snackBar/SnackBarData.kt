package net.thechance.mena.identity.presentation.components.snackBar

import androidx.compose.runtime.Immutable
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.ic_close_circle
import mena.identity_presentation.generated.resources.ic_success
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

@Immutable
data class SnackBarData(
    val message: StringResource,
    val title: StringResource,
    val isVisible: Boolean = false,
    val duration: Long = 3_000L,
    val type: SnackBarType = SnackBarType.ERROR
) {

    enum class SnackBarType(val icon: DrawableResource) {
        ERROR(Res.drawable.ic_close_circle),
        SUCCESS(Res.drawable.ic_success),
    }
}