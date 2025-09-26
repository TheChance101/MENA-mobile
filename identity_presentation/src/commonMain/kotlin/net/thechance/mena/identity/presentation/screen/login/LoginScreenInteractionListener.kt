package net.thechance.mena.identity.presentation.screen.login

import net.thechance.mena.identity.presentation.base.BaseInteractionListener
import net.thechance.mena.identity.presentation.bottomSheet.countryPicker.menaCountries.MenaCountry

interface LoginScreenInteractionListener : BaseInteractionListener {

    fun onRegisterClicked()
    fun onForgotPasswordClicked()
    fun onLoginClicked()
    fun onPhoneCodeClicked()

    fun onPhoneChanged(phone: String)
    fun onPasswordChanged(password: String)

    fun onPasswordVisibilityToggled()
    fun clearErrorMessage()

    fun onSelectCountryItem(country: MenaCountry)
    fun onClickConfirmButton()
    fun onDismissBottomSheet()
}