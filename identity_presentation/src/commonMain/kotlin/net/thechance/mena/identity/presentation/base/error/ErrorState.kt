package net.thechance.mena.identity.presentation.base.error

sealed interface ErrorState {
    data class AuthenticationError(val error: AuthenticationErrorState) : ErrorState
    data class LocationError(val error: LocationErrorState) : ErrorState
    data class ProfileError(val error: ProfileErrorState) : ErrorState
    data class GenericError(val throwable: Throwable) : ErrorState
}