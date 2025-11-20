package net.thechance.mena.identity.presentation.components.snackBar

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error
import mena.identity_presentation.generated.resources.ic_close_circle
import mena.identity_presentation.generated.resources.ic_success
import mena.identity_presentation.generated.resources.success
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

@Immutable
data class SnackBarData(
    val message: StringResource,
    val title: StringResource,
    val duration: Long = 3_000L,
    val type: SnackBarType = SnackBarType.ERROR
) {

    enum class SnackBarType(val icon: DrawableResource) {
        ERROR(Res.drawable.ic_close_circle),
        SUCCESS(Res.drawable.ic_success),
    }
}

val LocalSnackBarController = staticCompositionLocalOf<IdentitySnackBarController> {
    error("No SnackBarController provided")
}

class IdentitySnackBarController() {

    private val _effect = Channel<SnackBarData>(capacity = Channel.UNLIMITED)
    val effect = _effect.receiveAsFlow()

    var currentSnackBarData by mutableStateOf<SnackBarData?>(null)
        private set


    fun showSnackBarError(
        message: StringResource,
        title: StringResource = Res.string.error,
        duration: Long = 3_000L
    ) {
        showSnackBar(
            message = message,
            title = title,
            type = SnackBarData.SnackBarType.ERROR,
            duration = duration
        )
    }

    fun showSnackBarSuccess(
        message: StringResource,
        title: StringResource = Res.string.success,
        duration: Long = 3_000L
    ) {
        showSnackBar(
            message = message,
            title = title,
            type = SnackBarData.SnackBarType.SUCCESS,
            duration = duration
        )
    }

    private fun showSnackBar(
        message: StringResource,
        title: StringResource,
        type: SnackBarData.SnackBarType,
        duration: Long
    ) {
        val snackBarData = SnackBarData(
            message = message,
            title = title,
            type = type,
            duration = duration
        )
        currentSnackBarData = snackBarData
        _effect.trySend(snackBarData)
    }

    fun dismissSnackBar() {
        currentSnackBarData = null
    }
}