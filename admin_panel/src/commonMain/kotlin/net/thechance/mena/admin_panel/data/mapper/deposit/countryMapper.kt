package net.thechance.mena.admin_panel.data.mapper.deposit

import net.thechance.mena.admin_panel.data.remote.dto.deposit.CountryDto
import net.thechance.mena.admin_panel.domain.model.Country

fun CountryDto.toEntity(): Country {
    return Country(
        name = name.orEmpty(),
        callingCode = callingCode.orEmpty(),
        countryCodeName = countryCodeName.orEmpty(),
        flagEmoji = flagEmoji.orEmpty(),
        phoneNumberRegex = phoneNumberRegex.orEmpty()
    )
}