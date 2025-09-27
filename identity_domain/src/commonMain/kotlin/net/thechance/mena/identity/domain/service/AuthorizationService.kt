package net.thechance.mena.identity.domain.service

import kotlinx.coroutines.flow.StateFlow
import net.thechance.mena.identity.domain.repository.AuthenticationRepository

class AuthorizationService (private val authenticationRepository: AuthenticationRepository){

    suspend fun getAccessToken(): String {
        return authenticationRepository.getAccessToken()
    }

    suspend fun refreshToken(): String {
        return authenticationRepository.refreshAccessToken()
    }

    fun observeAccessToken(): StateFlow<String> = authenticationRepository.observeTokenChange()
}