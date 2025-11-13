package net.thechance.mena.admin_panel.data.remote.dto.deposit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryDto(
    @SerialName("name")
    val name: String? = null,
    @SerialName("callingCode")
    val callingCode: String? = null,
    @SerialName("countryCodeName")
    val countryCodeName: String? = null,
    @SerialName("flagEmoji")
    val flagEmoji: String? = null,
    @SerialName("phoneNumberRegex")
    val phoneNumberRegex: String? = null

)