package net.thechance.mena.identity.domain.repository

interface ChangePasswordRepository {

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): String

}