package net.thechance.mena.identity.presentation.feature.authenticationFlow.resetPassword.phoneEntry

import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener
import net.thechance.mena.identity.presentation.feature.authenticationFlow.countryPicker.menaCountries.MenaCountry

interface ResetPasswordPhoneEntryScreenInteractionListener : BaseInteractionListener {
    fun onSelectCountryItem(country: MenaCountry)
    fun onDismissBottomSheet()
    fun onClickContinue()
    fun onClickCountry()
    fun onChangePhone(phone: String)
    fun onClickBack()
    fun onClearErrorMessage()
}