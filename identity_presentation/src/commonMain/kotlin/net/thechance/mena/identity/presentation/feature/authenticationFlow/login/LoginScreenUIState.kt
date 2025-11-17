package net.thechance.mena.identity.presentation.feature.authenticationFlow.login

import net.thechance.mena.identity.presentation.feature.authenticationFlow.countryPicker.menaCountries.MenaCountry
import org.jetbrains.compose.resources.StringResource

data class LoginScreenUIState(
    val phoneNumber: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val showCountryBottomSheet: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: StringResource? = null,
    val isLoginEnabled: Boolean = false,
    val currentCountry: MenaCountry = MenaCountry.IRAQ,
)