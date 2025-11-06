package net.thechance.mena.identity.presentation.screen.changePassword

import net.thechance.mena.identity.presentation.screen.profile.SnackBarUiState


sealed interface ChangePasswordScreenUIEffect {
    data class NavigateBack(val snackBarUiState: SnackBarUiState? = null): ChangePasswordScreenUIEffect
}