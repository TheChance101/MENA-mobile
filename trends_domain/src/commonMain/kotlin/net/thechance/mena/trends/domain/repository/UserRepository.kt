package net.thechance.mena.trends.domain.repository

import net.thechance.mena.trends.domain.entity.UserInfo

interface UserRepository {
    suspend fun getCurrentUserProfile(): UserInfo
}