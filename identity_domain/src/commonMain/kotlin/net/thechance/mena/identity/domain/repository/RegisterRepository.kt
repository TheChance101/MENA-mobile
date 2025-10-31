package net.thechance.mena.identity.domain.repository

import net.thechance.mena.identity.domain.entity.PhoneNumber

interface RegisterRepository {
    suspend fun requestOTP(phoneNumber: PhoneNumber, countryCodeName: String)
    suspend fun verifyOTPCode(otpCode: String)
    suspend fun createPassword(newPassword: String, confirmPassword: String)
}