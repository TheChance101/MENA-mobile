package net.thechance.mena.identity.presentation.screen.login

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.useCase.LoginUseCase
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.menaCountries.MenaCountry
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.selectByCountry
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage

class LoginScreenModel(
    private val loginUseCase: LoginUseCase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<LoginScreenUIState, LoginScreenUIEffect>(LoginScreenUIState()),
    LoginScreenInteractionListener {

    override fun onLoginClicked() {
        updateState { copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            function = ::onLogin,
            onSuccess = ::onLoginSuccess,
            onError = ::onErrorAccrue,
            dispatcher = dispatcher
        )
    }

    private suspend fun onLogin() {
        loginUseCase.login(
            state.value.countryPickerUIState.currentCountry.callingCode,
            state.value.phoneNumber,
            state.value.password
        )
    }

    private fun onLoginSuccess() {
        updateState { copy(isLoading = false) }
        sendNewEffect(LoginScreenUIEffect.NavigateToHome)
    }

    private fun onErrorAccrue(errorState: ErrorState) {
        updateState {
            copy(
                isLoading = false,
                errorMessage = mapErrorToMessage(errorState)
            )
        }
    }


    private fun changeIsLoginEnabled() {
        updateState {
            val countryCode = countryPickerUIState.currentCountry.callingCode
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

    override fun clearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }


    override fun onSelectCountryItem(country: MenaCountry) {
        updateState {
            copy(
                countryPickerUIState = countryPickerUIState.copy(
                    selectedCountry = country,
                    countries = countryPickerUIState.countries.selectByCountry(country),
                    isEnabled = countryPickerUIState.currentCountry != country
                )
            )
        }
    }

    override fun onClickConfirmButton() {
        updateState {
            copy(
                showCountryBottomSheet = false,
                countryPickerUIState = countryPickerUIState.copy(
                    currentCountry = countryPickerUIState.selectedCountry!!,
                    isEnabled = false
                )
            )
        }
        changeIsLoginEnabled()
    }

    override fun onDismissBottomSheet() {
        updateState {
            copy(
                showCountryBottomSheet = false,
                countryPickerUIState = countryPickerUIState.copy(
                    selectedCountry = countryPickerUIState.currentCountry
                )
            )
        }
    }
}