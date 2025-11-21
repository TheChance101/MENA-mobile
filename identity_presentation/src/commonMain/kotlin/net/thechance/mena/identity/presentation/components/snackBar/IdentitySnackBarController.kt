package net.thechance.mena.identity.presentation.components.snackBar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import mena.identity_presentation.generated.resources.Res
import mena.identity_presentation.generated.resources.error
import mena.identity_presentation.generated.resources.success
import org.jetbrains.compose.resources.StringResource

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
            isVisible = true,
            message = message,
            title = title,
            type = type,
            duration = duration
        )
        currentSnackBarData = snackBarData
        _effect.trySend(snackBarData)
    }

    fun dismissSnackBar() {
        currentSnackBarData = currentSnackBarData?.copy(
            isVisible = false
        )
    }
}