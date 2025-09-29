package net.thechance.mena.identity.domain.repository

interface ResetPasswordRepository {
    suspend fun requestOTP(phoneNumber: String, countryCodeName: String)
    suspend fun verifyOTPCode(otpCode: String, phoneNumber: String)
    suspend fun resetPassword(
        newPassword: String,
        confirmPassword: String,
        phoneNumber: String
    )
}