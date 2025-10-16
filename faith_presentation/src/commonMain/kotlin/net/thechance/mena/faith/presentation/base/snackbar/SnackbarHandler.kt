package net.thechance.mena.faith.presentation.base.snackbar

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString


interface SnackbarHandler {
    val snackBarState: StateFlow<SnackBarState>

    fun showSnackBar(
        message: suspend () -> String,
        status: SnackBarState.Status,
        durationMillis: Long = 3000L,
        scope: CoroutineScope,
    ) = Napier.e { "Snackbar is Empty" }

    fun showSnackBar(
        message: StringResource,
        status: SnackBarState.Status,
        durationMillis: Long = 3000L,
        scope: CoroutineScope,
    ) = Napier.e { "Snackbar is Empty" }

    fun hideSnackBar() = Napier.e { "Snackbar is Empty" }

    companion object {
        val Empty = object : SnackbarHandler {
            override val snackBarState = MutableStateFlow(SnackBarState())
        }
    }
}

class DefaultSnackbarHandlerImpl : SnackbarHandler {
    override val snackBarState: MutableStateFlow<SnackBarState> = MutableStateFlow(SnackBarState())

    override fun showSnackBar(
        message: suspend () -> String,
        status: SnackBarState.Status,
        durationMillis: Long,
        scope: CoroutineScope,
    ) {
        scope.launch {
            if (snackBarState.value.isVisible) {
                hideSnackBar()
                delay(1000L)
            }
            snackBarState.update {
                SnackBarState(
                    message = message(),
                    status = status,
                    isVisible = true
                )
            }
            delay(durationMillis)
            snackBarState.update { it.copy(isVisible = false) }
        }
    }

    override fun showSnackBar(
        message: StringResource,
        status: SnackBarState.Status,
        durationMillis: Long,
        scope: CoroutineScope,
    ) = showSnackBar(
        message = { getString(message) },
        status = status,
        durationMillis = durationMillis,
        scope = scope
    )

    override fun hideSnackBar() = snackBarState.update { it.copy(isVisible = false) }
}
