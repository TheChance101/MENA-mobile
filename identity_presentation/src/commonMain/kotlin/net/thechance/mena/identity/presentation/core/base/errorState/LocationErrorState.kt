package net.thechance.mena.identity.presentation.core.base.errorState

sealed interface LocationErrorState {
    data object NoLocationPermission : LocationErrorState
    data object UnableToFindLocation : LocationErrorState
    data object FailedToOpenSettings : LocationErrorState
    data object AddressNotFound : LocationErrorState
    data class SomethingWentWrong(val message: String?) : LocationErrorState
}