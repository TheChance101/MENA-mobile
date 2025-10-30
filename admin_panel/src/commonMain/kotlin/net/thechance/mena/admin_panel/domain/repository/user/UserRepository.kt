package net.thechance.mena.admin_panel.domain.repository.user

import net.thechance.mena.admin_panel.domain.entity.user.User

interface UserRepository {
    suspend fun getAllUser():List<User>
}