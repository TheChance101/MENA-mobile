package net.thechance.mena.identity.presentation.screen.forget_password

import net.thechance.mena.identity.presentation.base.BaseInteractionListener
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.menaCountries.MenaCountry

interface ForgetPasswordScreenInteractionListener : BaseInteractionListener{
    fun onSelectCountryItem(country: MenaCountry)
    fun onClickConfirmButton()
    fun onDismissBottomSheet()
    fun onContinueClicked()
    fun onPhoneCodeClicked()
    fun onPhoneChanged(phone: String)
    fun onBackClicked()
    fun clearErrorMessage()
}