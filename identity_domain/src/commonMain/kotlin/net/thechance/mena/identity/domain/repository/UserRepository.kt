package net.thechance.mena.identity.domain.repository

import kotlinx.coroutines.flow.Flow
import net.thechance.mena.identity.domain.entity.User
import net.thechance.mena.identity.domain.model.AuthenticationTokens

interface UserRepository {
    suspend fun getUser(): Flow<User?>
    suspend fun updateUser(user: User)
    suspend fun uploadUserProfileImage(imageByteArray: ByteArray?)
    suspend fun uploadUserProfileImageWithTokens(imageByteArray: ByteArray?, authTokens: AuthenticationTokens)
    suspend fun deleteUserProfileImage()
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    )
    suspend fun deleteAccount()
}