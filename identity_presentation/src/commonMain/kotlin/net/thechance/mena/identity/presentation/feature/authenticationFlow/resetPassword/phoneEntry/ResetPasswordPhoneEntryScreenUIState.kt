package net.thechance.mena.identity.presentation.feature.authenticationFlow.resetPassword.phoneEntry

import net.thechance.mena.identity.presentation.feature.authenticationFlow.countryPicker.menaCountries.MenaCountry
import org.jetbrains.compose.resources.StringResource

data class ResetPasswordPhoneEntryScreenUIState(
    val phoneNumber: String = "",
    val showCountryBottomSheet: Boolean = false,
    val currentCountry: MenaCountry = MenaCountry.IRAQ,
    val isContinueEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: StringResource? = null
)