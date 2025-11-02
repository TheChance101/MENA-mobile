package net.thechance.mena.identity.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import net.thechance.mena.identity.domain.entity.User

interface UserRepository {
    suspend fun getUser(): Flow<User?>
    suspend fun updateUser(user: User, shouldUpdateImage: Boolean, imageByteArray: ByteArray?)
    fun applyLanguage(languageIso: String)
    fun observeAppLanguage(): StateFlow<String>
    fun getCurrentAppLanguage(): String
}