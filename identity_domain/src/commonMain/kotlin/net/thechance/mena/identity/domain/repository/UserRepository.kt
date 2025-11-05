package net.thechance.mena.identity.domain.repository

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.identity.domain.entity.User

interface UserRepository {
    suspend fun getUser(): Flow<User?>
    suspend fun updateUser(user: User, shouldUpdateImage: Boolean)
    suspend fun uploadUserProfileImage(imageByteArray: ByteArray?)
    suspend fun deleteUserProfileImage()
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    )
}