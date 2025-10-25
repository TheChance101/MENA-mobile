package net.thechance.mena.trends.presentation.snackbar

import androidx.compose.runtime.compositionLocalOf
import net.thechance.mena.trends.presentation.shared.model.SnackBarStatus

interface SnackBarController {
    fun showSnackBar(snackBarData: SnackBarData)
}

data class SnackBarData(
    val message: String,
    val snackBarType: SnackBarStatus,
    val snackbarDuration: SnackbarDuration = SnackbarDuration.Medium,
    val onComplete: (() -> Unit)? = null,
)

enum class SnackbarDuration(val timeInMillis: Long) {
    Brief(1500),
    Medium(3000),
    Extensive(5000),
    Indefinite(Long.MAX_VALUE);
}

val LocalSnackbarController = compositionLocalOf<SnackBarController> {
    error("No SnackBarController provided")
}