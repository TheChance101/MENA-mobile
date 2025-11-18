package net.thechance.mena.admin_panel.presentation.screen.deposit

import net.thechance.mena.admin_panel.domain.model.Country

fun DepositScreenState.CountryUiState.toEntity(): Country {
    return Country(
        name=name ,
        phoneNumberRegex=phoneNumberRegex,
        callingCode = callingCode,
        countryCodeName = countryCodeName,
        flagEmoji = flagEmoji
    )
}
fun Country.toUiState(): DepositScreenState.CountryUiState {
    return DepositScreenState.CountryUiState(
        name=name ,
        phoneNumberRegex=phoneNumberRegex,
        callingCode = callingCode,
        countryCodeName = countryCodeName,
        flagEmoji = flagEmoji
    )
}