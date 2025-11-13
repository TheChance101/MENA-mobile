package net.thechance.mena.admin_panel.domain.use_case.auth

import net.thechance.mena.admin_panel.domain.exceptions.InvalidPasswordException
import net.thechance.mena.admin_panel.domain.repository.authentication.AdminAuthenticationRepository
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single
class LoginUseCase(
    @Provided
    private val adminAuthenticationRepository:
    AdminAuthenticationRepository
) {
    suspend fun login(userName: String, password: String) {
        if (!isPasswordValid(password)) throw InvalidPasswordException()
        adminAuthenticationRepository.login(
            userName = userName,
            password = password
        )
    }

    private fun isPasswordValid(password: String) = password.length >= PASSWORD_MIN_LENGTH

    private companion object {
        const val PASSWORD_MIN_LENGTH = 8
    }
}