package net.thechance.mena.identity.domain.repository

import kotlinx.coroutines.flow.StateFlow
import net.thechance.mena.identity.domain.entity.PhoneNumber

interface AuthenticationRepository {
    suspend fun login(phoneNumber: PhoneNumber, password: String)
    suspend fun refreshAccessToken(): String
    suspend fun getAccessToken(): String
    fun observeTokenChange(): StateFlow<String>
}