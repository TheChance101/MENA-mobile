package net.thechance.mena.identity.presentation.feature.authenticationFlow.login

import net.thechance.mena.identity.presentation.core.base.BaseInteractionListener
import net.thechance.mena.identity.presentation.feature.authenticationFlow.countryPicker.menaCountries.MenaCountry

interface LoginScreenInteractionListener : BaseInteractionListener {

    fun onRegisterClicked()
    fun onForgotPasswordClicked()
    fun onLoginClicked()
    fun onPhoneCodeClicked()
    fun onPhoneChanged(phone: String)
    fun onPasswordChanged(password: String)
    fun onPasswordVisibilityToggled()
    fun clearErrorMessage()
    fun onConfirmCountryItem(country: MenaCountry)
    fun onDismissBottomSheet()
}