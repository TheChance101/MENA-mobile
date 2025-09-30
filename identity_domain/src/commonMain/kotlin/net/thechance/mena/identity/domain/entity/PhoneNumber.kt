package net.thechance.mena.identity.domain.entity

data class PhoneNumber(
    val countryCode: String,
    val localNumber: String,
) {
    fun getFormattedPhoneNumber(): String = countryCode + localNumber
}
