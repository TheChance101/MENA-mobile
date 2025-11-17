package net.thechance.mena.identity.presentation.feature.profileFlow.changePassword

import net.thechance.mena.identity.presentation.feature.profileFlow.profile.SnackBarUiState

sealed interface ChangePasswordScreenUIEffect {
    data class NavigateBack(val snackBarUiState: SnackBarUiState? = null) :
        ChangePasswordScreenUIEffect
}