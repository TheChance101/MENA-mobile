package net.thechance.mena.identity.domain.repository

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.util.AppLanguage

interface UserRepository {
    suspend fun getUser(): Flow<User?>
    suspend fun updateUser(user: User, shouldUpdateImage: Boolean)
    suspend fun uploadUserProfileImage(imageByteArray: ByteArray?)
    suspend fun deleteUserProfileImage()
    suspend fun updateUser(user: User, shouldUpdateImage: Boolean, imageByteArray: ByteArray?)
    fun applyLanguage(languageIso: String)
    fun observeAppLanguage(): Flow<AppLanguage>
    fun getCurrentAppLanguage(): AppLanguage
}