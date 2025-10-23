package net.thechance.mena.trends.presentation.snackbar

import androidx.compose.runtime.compositionLocalOf

val LocalSnackbarController = compositionLocalOf<SnackBarController> {
    error("No SnackBarController provided")
}
