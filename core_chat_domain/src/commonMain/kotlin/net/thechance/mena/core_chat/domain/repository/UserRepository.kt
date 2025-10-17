package net.thechance.mena.core_chat.domain.repository

import net.thechance.mena.core_chat.domain.entity.User

interface UserRepository {
    suspend fun getUserInfo(): User
}