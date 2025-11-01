package net.thechance.mena.admin_panel.domain.repository.authentication

interface AdminAuthenticationRepository {
    suspend fun login(userName: String, password: String)
    suspend fun refreshAccessToken(): String
    suspend fun getAccessToken(): String
}