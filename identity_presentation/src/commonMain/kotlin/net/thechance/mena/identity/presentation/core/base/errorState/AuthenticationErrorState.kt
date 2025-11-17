package net.thechance.mena.identity.presentation.core.base.errorState


sealed interface AuthenticationErrorState {
    data object InvalidCountryCode : AuthenticationErrorState
    data object InvalidMobileNumber : AuthenticationErrorState
    data object InvalidPassword : AuthenticationErrorState
    data object InvalidOTP : AuthenticationErrorState
    
    data object UserIsBlocked : AuthenticationErrorState
    data object TooManyRequests : AuthenticationErrorState
    data object OTPExpired : AuthenticationErrorState
    data object PhoneNumberAlreadyExists : AuthenticationErrorState
    data object UsernameAlreadyExists : AuthenticationErrorState


    data object IncorrectPassword : AuthenticationErrorState
    data object NoNetwork : AuthenticationErrorState
    data object InvalidCredentials : AuthenticationErrorState

    data object InvalidRequest :AuthenticationErrorState
    data class SomethingWentWrong(val message: String?) : AuthenticationErrorState
}