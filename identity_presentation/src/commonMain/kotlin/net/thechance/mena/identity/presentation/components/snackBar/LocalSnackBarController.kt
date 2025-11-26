package net.thechance.mena.identity.presentation.components.snackBar

import androidx.compose.runtime.staticCompositionLocalOf

val LocalSnackBarController = staticCompositionLocalOf<IdentitySnackBarController> {
    error("No SnackBarController provided")
}