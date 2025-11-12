package net.thechance.mena.admin_panel.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val name: String,
    val callingCode: String,
    val countryCodeName: String,
    val flagEmoji: String,
    val phoneNumberRegex: String,
)