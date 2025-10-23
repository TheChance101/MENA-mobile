package net.thechance.mena.identity.presentation.base.error

sealed interface LocationErrorState {
    data object NoLocationPermission : LocationErrorState
    data object FailedToRequestPermission : LocationErrorState
    
    data object UnableToFindLocation : LocationErrorState
    data object FailedToOpenSettings : LocationErrorState
    
    data object AddressNotFound : LocationErrorState
    
    data class SomethingWentWrong(val message: String?) : LocationErrorState
}