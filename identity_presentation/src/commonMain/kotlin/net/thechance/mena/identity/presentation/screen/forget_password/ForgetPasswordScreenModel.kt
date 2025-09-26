package net.thechance.mena.identity.presentation.screen.forget_password

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineScope
import net.thechance.mena.identity.domain.useCase.LoginUseCase
import net.thechance.mena.identity.presentation.base.BaseScreenModel
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.menaCountries.MenaCountry
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.selectByCountry

class ForgetPasswordScreenModel(
    val loginUseCase: LoginUseCase
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
        sendNewEffect(ForgetPasswordScreenUIEffect.NavigateToOTP)
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
}

