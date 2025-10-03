package net.thechance.mena.identity.presentation.screen.forgetPassword

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.repository.ResetPasswordRepository
import net.thechance.mena.identity.domain.useCase.LoginUseCase
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.menaCountries.MenaCountry
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.selectByCountry
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
        changeIsContinueEnabled()
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

    override fun onClickContinue() {
        tryToExecute(
            function = {
                resetPasswordRepository.requestOTP(
                    phoneNumber = PhoneNumber(
                        countryCode = state.value.countryPickerUIState.currentCountry.callingCode,
                        localNumber = state.value.phoneNumber
                    ),
                    countryCodeName = state.value.countryPickerUIState.currentCountry.countryCodeName
                )
            },
            onSuccess = ::verifyPhoneNumberSuccess,
            onError = ::onError,
            dispatcher = dispatcher
        )
    }

    private fun verifyPhoneNumberSuccess() {
        sendNewEffect(
            ForgetPasswordScreenUIEffect.NavigateToOTP(
                phoneNumber = state.value.phoneNumber,
                callingCode = state.value.countryPickerUIState.currentCountry.callingCode,
                countryCode = state.value.countryPickerUIState.currentCountry.countryCodeName
            )
        )
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
            val countryCode = countryPickerUIState.currentCountry.callingCode
            val mobileNumberValid = loginUseCase.isMobileNumberValid(countryCode, phoneNumber)
            copy(isContinueEnabled = mobileNumberValid)
        }
    }

    private fun onError(errorState: ErrorState) {
        updateState {
            copy(
                isLoading = false,
                errorMessage = mapErrorToMessage(errorState)
            )
        }
    }
}