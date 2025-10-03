package net.thechance.mena.identity.presentation.screen.forgetPassword

import net.thechance.mena.identity.presentation.base.BaseInteractionListener
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.menaCountries.MenaCountry

interface ForgetPasswordScreenInteractionListener : BaseInteractionListener{
    fun onSelectCountryItem(country: MenaCountry)
    fun onClickConfirmButton()
    fun onDismissBottomSheet()
    fun onClickContinue()
    fun onClickCountry()
    fun onChangePhone(phone: String)
    fun onClickBack()
    fun onClearErrorMessage()
}