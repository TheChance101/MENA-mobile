package net.thechance.mena.admin_panel.domain.repository

import net.thechance.mena.admin_panel.domain.entity.User

interface UserRepo {
    suspend fun getAllUsers(): List<User>
}