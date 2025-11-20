package net.thechance.mena.identity.presentation.screen.resetPassword.phoneEntry

import net.thechance.mena.identity.presentation.base.BaseInteractionListener
import net.thechance.mena.identity.presentation.screen.countryPicker.menaCountries.MenaCountry

interface ResetPasswordPhoneEntryScreenInteractionListener : BaseInteractionListener{
    fun onSelectCountryItem(country: MenaCountry)
    fun onDismissBottomSheet()
    fun onClickContinue()
    fun onClickCountry()
    fun onChangePhone(phone: String)
    fun onClickBack()
}