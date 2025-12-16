package net.thechance.mena.identity.presentation.screen.register.shared

import kotlinx.serialization.Serializable
import net.thechance.mena.identity.domain.entity.PhoneNumber

@Serializable
data class PhoneNumberUIState(
    val countryCode: String = "",
    val localNumber: String = ""
)

fun PhoneNumberUIState.toPhoneNumber(): PhoneNumber {
    return PhoneNumber(
        countryCode = countryCode,
        localNumber = localNumber
    )
}

fun PhoneNumber.toPhoneNumberUIState(): PhoneNumberUIState {
    return PhoneNumberUIState(
        countryCode = countryCode,
        localNumber = localNumber
    )
}