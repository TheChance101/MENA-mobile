package net.thechance.mena.identity.data.repository

import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import net.thechance.mena.identity.data.dto.auth.LoginRequestDto
import net.thechance.mena.identity.data.dto.auth.AuthenticationResponse
import net.thechance.mena.identity.data.dto.auth.RefreshRequestDto
import net.thechance.mena.identity.data.utils.accessToken
import net.thechance.mena.identity.data.utils.postJson
import net.thechance.mena.identity.data.utils.refreshToken
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.repository.AuthenticationRepository

class AuthenticationRepositoryImpl(
    private val client: HttpClient,
    private val settings: Settings
) : AuthenticationRepository {
    override suspend fun login(phoneNumber: PhoneNumber, password: String) {
        return safeWrapper {
            val loginResponse: AuthenticationResponse = client.postJson(LoginRequestDto(phoneNumber.getFormattedPhoneNumber(), password), LOGIN)
            saveAuthTokens(loginResponse)
        }
    }

    override suspend fun refreshAccessToken(): String {
        val refreshResponse: AuthenticationResponse = safeWrapper {
            client.postJson(RefreshRequestDto(settings.refreshToken), REFRESH)
        }
        saveAuthTokens(refreshResponse)
        return settings.accessToken
    }

    override suspend fun getAccessToken(): String {
        return settings.accessToken
    }

    private fun saveAuthTokens(authInfo: AuthenticationResponse) {
        settings.accessToken = authInfo.accessToken
        settings.refreshToken = authInfo.refreshToken
    }

    companion object {
        const val LOGIN = "identity/login"
        const val REFRESH = "identity/refresh"

    }
}