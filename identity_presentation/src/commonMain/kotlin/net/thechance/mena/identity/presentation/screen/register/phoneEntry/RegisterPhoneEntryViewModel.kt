package net.thechance.mena.identity.presentation.screen.register.phoneEntry

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import net.thechance.mena.identity.domain.exception.AuthenticationException
import net.thechance.mena.identity.domain.repository.RegisterRepository
import net.thechance.mena.identity.domain.useCase.LoginUseCase
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.error.ErrorState
import net.thechance.mena.identity.presentation.base.error.handleAuthenticationException
import net.thechance.mena.identity.presentation.mapper.mapAuthenticationErrorToMessage
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage
import net.thechance.mena.identity.presentation.screen.countryPicker.menaCountries.MenaCountry
import org.jetbrains.compose.resources.StringResource

class RegisterPhoneEntryViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerRepository: RegisterRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<RegisterPhoneEntryUIState, RegisterPhoneEntryUIEffect>(
    RegisterPhoneEntryUIState()
), RegisterPhoneEntryInteractionListener {

    override fun onSelectCountryItem(country: MenaCountry) {
        updateState {
            copy(currentCountry = country, showCountryBottomSheet = false)
        }
        changeIsContinueEnabled()
    }

    override fun onDismissBottomSheet() {
        updateState {
            copy(showCountryBottomSheet = false)
        }
    }

    override fun onClickContinue() {
        updateState { copy(isLoading = true, errorMessage = null) }
        tryToExecute(
            function = ::requestOTP,
            onSuccess = { onOTPRequestSuccess() },
            onError = ::onOTPRequestError,
            dispatcher = dispatcher
        )
    }

    private suspend fun requestOTP() {
        // TODO: Uncomment when ready to integrate with backend
        // registerRepository.requestOTP(
        //     phoneNumber = PhoneNumber(
        //         countryCode = state.value.currentCountry.callingCode,
        //         localNumber = state.value.phoneNumber
        //     ),
        //     countryCodeName = state.value.currentCountry.countryCodeName
        // )

        // Bypass for UI testing - just validate locally
        delay(1000) // Simulate network delay
    }

    private fun onOTPRequestSuccess() {
        updateState { copy(isLoading = false) }
        sendNewEffect(
            RegisterPhoneEntryUIEffect.NavigateToOTP(
                phoneNumber = state.value.phoneNumber,
                callingCode = state.value.currentCountry.callingCode,
                countryCode = state.value.currentCountry.countryCodeName
            )
        )
    }

    private fun onOTPRequestError(throwable: Throwable) {
        updateState { copy(isLoading = false, errorMessage = mapErrorMessage(throwable)) }
    }

    override fun onClickCountry() {
        updateState { copy(showCountryBottomSheet = true) }
    }

    override fun onChangePhone(phone: String) {
        updateState { copy(phoneNumber = phone) }
        changeIsContinueEnabled()
    }

    override fun onClickBack() {
        sendNewEffect(RegisterPhoneEntryUIEffect.NavigateBack)
    }

    override fun onClearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }

    override fun onClickLogin() {
        sendNewEffect(RegisterPhoneEntryUIEffect.NavigateToLogin)
    }

    private fun changeIsContinueEnabled() {
        updateState {
            val countryCode = currentCountry.callingCode
            val mobileNumberValid = loginUseCase.isMobileNumberValid(countryCode, phoneNumber)
            copy(isContinueEnabled = mobileNumberValid)
        }
    }

    private fun mapErrorMessage(throwable: Throwable): StringResource {
        return when (throwable) {
            is AuthenticationException -> mapAuthenticationErrorToMessage(
                handleAuthenticationException(throwable)
            )

            else -> mapErrorToMessage(ErrorState.GenericError(throwable))
        }
    }
}