package net.thechance.mena.identity.domain.repository

import kotlinx.coroutines.flow.StateFlow
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.AuthenticationTokens

interface AuthenticationRepository {
    suspend fun login(phoneNumber: PhoneNumber, password: String)
    suspend fun logout()
    suspend fun refreshAccessToken(): String
    suspend fun getAccessToken(): String
    suspend fun getAuthTokens(): AuthenticationTokens?
    suspend fun saveAuthTokensWithoutEmit(authTokens: AuthenticationTokens)
    suspend fun saveAuthTokensAndEmit(authTokens: AuthenticationTokens)
    suspend fun clearAuthTokens()
    fun observeTokenChange(): StateFlow<String>
}