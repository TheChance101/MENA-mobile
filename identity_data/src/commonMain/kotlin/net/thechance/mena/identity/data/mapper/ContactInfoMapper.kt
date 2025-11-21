package net.thechance.mena.identity.data.mapper

import net.thechance.mena.identity.data.dto.privacyAndPolicy.response.ContactInfoResponseDto
import net.thechance.mena.identity.domain.model.ContactInfo

fun ContactInfoResponseDto.toDomain(): ContactInfo {
    return ContactInfo(
        email = email,
        phoneNumber = phoneNumber,
        facebookAccount = facebookAccount,
    )
}