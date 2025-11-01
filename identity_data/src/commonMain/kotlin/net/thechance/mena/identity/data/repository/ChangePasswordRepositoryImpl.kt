package net.thechance.mena.identity.data.repository

import io.ktor.client.HttpClient
import net.thechance.mena.identity.data.dto.profile.request.ChangePasswordRequestDto
import net.thechance.mena.identity.data.dto.profile.response.ChangePasswordResponseDto
import net.thechance.mena.identity.data.utils.postJson
import net.thechance.mena.identity.domain.repository.ChangePasswordRepository

class ChangePasswordRepositoryImpl(
    val client : HttpClient
): ChangePasswordRepository {
    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): String {
        val response = client.postJson<ChangePasswordRequestDto, ChangePasswordResponseDto>(
            requestDto = ChangePasswordRequestDto(
                currentPassword = currentPassword,
                newPassword = newPassword,
                confirmPassword = confirmPassword
            ),
            path = CHANGE_PASSWORD_PATH
        )
       return response.message
    }

    companion object{
        const val CHANGE_PASSWORD_PATH = "identity/profile/change-password"
    }
}