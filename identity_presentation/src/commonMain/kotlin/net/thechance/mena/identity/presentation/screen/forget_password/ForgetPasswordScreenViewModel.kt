package net.thechance.mena.identity.presentation.screen.forget_password

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineScope
import net.thechance.mena.identity.domain.repository.ForgetPasswordRepository
import net.thechance.mena.identity.domain.useCase.LoginUseCase
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.base.ErrorState
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.menaCountries.MenaCountry
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.selectByCountry
import net.thechance.mena.identity.presentation.mapper.mapErrorToMessage

class ForgetPasswordScreenViewModel(
    val loginUseCase: LoginUseCase,
    val forgetPasswordRepository: ForgetPasswordRepository
) : BaseScreenModel<ForgetPasswordScreenUIState, ForgetPasswordScreenUIEffect>(
    ForgetPasswordScreenUIState()
), ForgetPasswordScreenInteractionListener {
    override val viewModelScope: CoroutineScope
        get() = screenModelScope

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

    override fun onContinueClicked() {
        tryToExecute(
            function = {
                forgetPasswordRepository.requestOTP(
                    phoneNumber = state.value.phoneNumber,
                    countryCodeName = state.value.countryPickerUIState.currentCountry.countryCodeName
                )
            },
            onSuccess = ::verifyPhoneNumberSuccess,
            onError = ::onError
        )
    }

    private fun verifyPhoneNumberSuccess() {
        sendNewEffect(
            ForgetPasswordScreenUIEffect.NavigateToOTP(
                phoneNumber = state.value.phoneNumber,
                countryCode = state.value.countryPickerUIState.currentCountry.countryCodeName
            )
        )
    }

    override fun onPhoneCodeClicked() {
        updateState { copy(showCountryBottomSheet = true) }
    }

    override fun onPhoneChanged(phone: String) {
        updateState { copy(phoneNumber = phone) }
        changeIsContinueEnabled()
    }

    override fun onBackClicked() {
        sendNewEffect(ForgetPasswordScreenUIEffect.NavigateBack)
    }

    override fun clearErrorMessage() {
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