package net.thechance.mena.identity.domain.repository

import net.thechance.mena.identity.domain.entity.PhoneNumber

interface AuthenticationRepository {
    suspend fun login(phoneNumber: PhoneNumber, password: String)
    suspend fun refreshAccessToken(): String
    suspend fun getAccessToken(): String
}