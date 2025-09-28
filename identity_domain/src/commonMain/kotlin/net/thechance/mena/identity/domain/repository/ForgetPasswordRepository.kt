package net.thechance.mena.identity.domain.repository

interface ForgetPasswordRepository {
    suspend fun requestOTP(phoneNumber: String, countryCodeName: String)
    suspend fun verifyOTPCode(otpCode: String , phoneNumber: String)
}