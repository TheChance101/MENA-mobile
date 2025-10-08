package net.thechance.mena.trends.domain.repository

import net.thechance.mena.trends.domain.entity.Profile

interface UserRepository {
    suspend fun getCurrentUserProfile(): Profile
}