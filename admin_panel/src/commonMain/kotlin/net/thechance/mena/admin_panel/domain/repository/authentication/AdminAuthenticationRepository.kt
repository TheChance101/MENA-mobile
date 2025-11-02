package net.thechance.mena.admin_panel.domain.repository.authentication

interface AdminAuthenticationRepository {
    suspend fun login(userName: String, password: String)
}