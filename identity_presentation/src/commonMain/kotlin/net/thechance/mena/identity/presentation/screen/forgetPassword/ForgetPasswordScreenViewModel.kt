package net.thechance.mena.identity.presentation.screen.forgetPassword

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository
import net.thechance.mena.identity.domain.useCase.LoginUseCase
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.error.ErrorState
import net.thechance.mena.identity.presentation.screen.countryPicker.menaCountries.MenaCountry
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage

class ForgetPasswordScreenViewModel(
    private val loginUseCase: LoginUseCase,
    private val resetPasswordRepository: ResetPasswordRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseScreenModel<ForgetPasswordScreenUIState, ForgetPasswordScreenUIEffect>(
    ForgetPasswordScreenUIState()
), ForgetPasswordScreenInteractionListener {

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
        tryToExecute(
            function = ::requestOTP,
            onSuccess = ::onOTPRequestSuccess,
            onError = ::onOTPRequestError,
            dispatcher = dispatcher
        )
    }

    private suspend fun requestOTP() {
        resetPasswordRepository.requestOTP(
            phoneNumber = PhoneNumber(
                countryCode = state.value.currentCountry.callingCode,
                localNumber = state.value.phoneNumber
            ),
            countryCodeName = state.value.currentCountry.countryCodeName
        )
    }

    private fun onOTPRequestSuccess() {
        sendNewEffect(
            ForgetPasswordScreenUIEffect.NavigateToOTP(
                phoneNumber = state.value.phoneNumber,
                callingCode = state.value.currentCountry.callingCode,
                countryCode = state.value.currentCountry.countryCodeName
            )
        )
    }

    private fun onOTPRequestError(errorState: ErrorState) {
        updateState { copy(errorMessage = mapErrorToMessage(errorState)) }
    }

    override fun onClickCountry() {
        updateState { copy(showCountryBottomSheet = true) }
    }

    override fun onChangePhone(phone: String) {
        updateState { copy(phoneNumber = phone) }
        changeIsContinueEnabled()
    }

    override fun onClickBack() {
        sendNewEffect(ForgetPasswordScreenUIEffect.NavigateBack)
    }

    override fun onClearErrorMessage() {
        updateState { copy(errorMessage = null) }
    }

    private fun changeIsContinueEnabled() {
        updateState {
            val countryCode = currentCountry.callingCode
            val mobileNumberValid = loginUseCase.isMobileNumberValid(countryCode, phoneNumber)
            copy(isContinueEnabled = mobileNumberValid)
        }
    }
}