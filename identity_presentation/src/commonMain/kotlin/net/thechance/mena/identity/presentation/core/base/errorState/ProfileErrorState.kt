package net.thechance.mena.identity.presentation.core.base.errorState

sealed interface ProfileErrorState {
    data object UsernameRequired : ProfileErrorState
    data object FirstNameRequired : ProfileErrorState
    data object LastNameRequired : ProfileErrorState
    data object PasswordMismatch : ProfileErrorState
    data object CameraPermissionRequired : ProfileErrorState
    data object NoNetwork : ProfileErrorState
    data class SomethingWentWrong(val message: String?) : ProfileErrorState
}