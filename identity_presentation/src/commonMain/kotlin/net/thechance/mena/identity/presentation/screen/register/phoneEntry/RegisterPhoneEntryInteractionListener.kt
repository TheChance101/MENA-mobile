package net.thechance.mena.identity.presentation.screen.register.phoneEntry

import net.thechance.mena.identity.presentation.base.BaseInteractionListener
import net.thechance.mena.identity.presentation.screen.countryPicker.menaCountries.MenaCountry

interface RegisterPhoneEntryInteractionListener : BaseInteractionListener {
    fun onSelectCountryItem(country: MenaCountry)
    fun onDismissBottomSheet()
    fun onClickRegister()
    fun onClickCountry()
    fun onChangePhone(phone: String)
    fun onClickLogin()
}