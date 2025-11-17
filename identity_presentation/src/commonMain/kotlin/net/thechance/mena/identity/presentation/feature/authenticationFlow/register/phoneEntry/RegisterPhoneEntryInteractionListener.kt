package net.thechance.mena.identity.presentation.feature.authenticationFlow.register.phoneEntry

import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener
import net.thechance.mena.identity.presentation.feature.authenticationFlow.countryPicker.menaCountries.MenaCountry

interface RegisterPhoneEntryInteractionListener : BaseInteractionListener {
    fun onSelectCountryItem(country: MenaCountry)
    fun onDismissBottomSheet()
    fun onClickRegister()
    fun onClickCountry()
    fun onChangePhone(phone: String)
    fun onClearErrorMessage()
    fun onClickLogin()
}