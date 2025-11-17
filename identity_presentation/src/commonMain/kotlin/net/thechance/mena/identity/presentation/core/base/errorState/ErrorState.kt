package net.thechance.mena.identity.presentation.core.base.errorState

sealed interface ErrorState {
    data class AuthenticationError(val errorState: AuthenticationErrorState) : ErrorState
    data class LocationError(val errorState: LocationErrorState) : ErrorState
    data class ProfileError(val errorState: ProfileErrorState) : ErrorState
    data class GenericError(val throwable: Throwable) : ErrorState
}