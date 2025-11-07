package net.thechance.mena.identity.domain.repository

import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.AuthenticationTokens
import net.thechance.mena.identity.domain.model.RegisterRequest

interface RegisterRepository {
    suspend fun requestOTP(phoneNumber: PhoneNumber, countryCodeName: String)
    suspend fun verifyOTPCode(otpCode: String)
    suspend fun checkUserExistence(username: String): Boolean
    suspend fun register(request: RegisterRequest): AuthenticationTokens
}