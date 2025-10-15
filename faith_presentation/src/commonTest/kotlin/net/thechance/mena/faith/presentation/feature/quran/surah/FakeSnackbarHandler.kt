package net.thechance.mena.faith.presentation.feature.quran.surah

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.thechance.mena.faith.presentation.base.snackbar.SnackBarState
import net.thechance.mena.faith.presentation.base.snackbar.SnackbarHandler
import org.jetbrains.compose.resources.StringResource

class FakeSnackbarHandler : SnackbarHandler {
    override val snackBarState = MutableStateFlow(SnackBarState())

    override fun showSnackBar(
        message: StringResource,
        status: SnackBarState.Status,
        durationMillis: Long,
        scope: CoroutineScope
    ) {
        snackBarState.value = SnackBarState(
            message = "Fake message",
            status = status,
            isVisible = true
        )
    }

    override fun showSnackBar(
        message: suspend () -> String,
        status: SnackBarState.Status,
        durationMillis: Long,
        scope: CoroutineScope
    ) {
        scope.launch {
            snackBarState.value = SnackBarState(
                message = message(),
                status = status,
                isVisible = true
            )
        }
    }

    override fun hideSnackBar() {
        snackBarState.value = snackBarState.value.copy(isVisible = false)
    }
}
