package net.thechance.mena.admin_panel.domain.repository

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.admin_panel.domain.entity.User

interface UserRepository {
    fun getAllUsers(): Flow<List<User>>
}