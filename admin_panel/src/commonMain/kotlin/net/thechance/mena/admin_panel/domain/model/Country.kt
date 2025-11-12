package net.thechance.mena.admin_panel.domain.model

data class Country(
    val name: String,
    val callingCode: String,
    val countryCodeName: String,
    val flagEmoji: String,
    val phoneNumberRegex: String,
)