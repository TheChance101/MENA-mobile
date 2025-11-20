package net.thechance.mena.identity.presentation.screen.login

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.useCase.LoginUseCase
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.errorState.ErrorState
import net.thechance.mena.identity.presentation.screen.countryPicker.menaCountries.MenaCountry
import net.thechance.mena.identity.presentation.mapper.createNavigateToHomeEffect
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import org.jetbrains.compose.resources.StringResource

class LoginScreenViewModel(
    private val loginUseCase: LoginUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<LoginScreenUIState, LoginScreenUIEffect>(LoginScreenUIState()),
    LoginScreenInteractionListener {

    override fun onLoginClicked() {
        updateState { copy(isLoading = true) }
        tryToExecute(
            function = ::onLogin,
            onSuccess = { onLoginSuccess() },
            onError = ::onLoginError,
            dispatcher = dispatcher
        )
    }

    private suspend fun onLogin() {
        loginUseCase.login(
            state.value.currentCountry.callingCode,
            state.value.phoneNumber,
            state.value.password
        )
    }

    private fun onLoginSuccess() {
        updateState { copy(isLoading = false) }
        sendNewEffect(createNavigateToHomeEffect())
    }

    private fun onLoginError(throwable: Throwable) {
        updateState { copy(isLoading = false) }

        sendNewEffect(
            LoginScreenUIEffect.ShowSnackBarError(
                errorStringResource = mapErrorMessage(throwable)
            )
        )
    }


    private fun changeIsLoginEnabled() {
        updateState {
            val countryCode = currentCountry.callingCode
            val mobileNumberValid = loginUseCase.isMobileNumberValid(countryCode, phoneNumber)
            val passwordValid = loginUseCase.isPasswordValid(password)
            copy(isLoginEnabled = passwordValid && mobileNumberValid)
        }
    }

    override fun onRegisterClicked() {
        sendNewEffect(LoginScreenUIEffect.NavigateToRegister)
    }

    override fun onForgotPasswordClicked() {
        sendNewEffect(LoginScreenUIEffect.NavigateToForgotPassword)
    }

    override fun onPhoneCodeClicked() {
        updateState { copy(showCountryBottomSheet = true) }
    }


    override fun onPhoneChanged(phone: String) {
        updateState { copy(phoneNumber = phone) }
        changeIsLoginEnabled()
    }

    override fun onPasswordChanged(password: String) {
        updateState { copy(password = password) }
        changeIsLoginEnabled()
    }

    override fun onPasswordVisibilityToggled() {
        updateState { copy(isPasswordVisible = !isPasswordVisible) }
    }

    override fun onConfirmCountryItem(country: MenaCountry) {
        updateState { copy(currentCountry = country, showCountryBottomSheet = false) }
        changeIsLoginEnabled()
    }

    override fun onDismissBottomSheet() {
        updateState { copy(showCountryBottomSheet = false) }
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource{
        return when(throwable){
            is AuthenticationException -> mapAuthenticationErrorToMessage(handleLoginException(throwable))
            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }
}