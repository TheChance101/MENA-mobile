package net.thechance.mena.trends.domain.repository

import net.thechance.mena.trends.domain.entity.User

interface UserRepository {
    suspend fun getCurrentUserInfo(): User
}