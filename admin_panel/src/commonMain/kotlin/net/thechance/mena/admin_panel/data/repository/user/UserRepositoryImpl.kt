package net.thechance.mena.admin_panel.data.repository.user

import net.thechance.mena.admin_panel.data.remote.service.ApiService
import net.thechance.mena.admin_panel.data.utils.executeApiSafely
import net.thechance.mena.admin_panel.domain.entity.user.User
import net.thechance.mena.admin_panel.domain.repository.user.UserRepository
import org.koin.core.annotation.Single

@Single
class UserRepositoryImpl (
    private val apiService: ApiService,
): UserRepository {
    override suspend fun getAllUser(): List<User> {
        return executeApiSafely { apiService.getAllUsers() }

    }

}