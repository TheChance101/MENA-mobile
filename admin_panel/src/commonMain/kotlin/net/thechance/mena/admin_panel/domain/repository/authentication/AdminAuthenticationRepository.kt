package net.thechance.mena.admin_panel.domain.repository.authentication

import kotlinx.coroutines.flow.StateFlow

interface AdminAuthenticationRepository {
    suspend fun login(userName: String, password: String)
    suspend fun logout()
    suspend fun isUserLoggedIn(): Boolean
    fun observeToken(): StateFlow<String>
}