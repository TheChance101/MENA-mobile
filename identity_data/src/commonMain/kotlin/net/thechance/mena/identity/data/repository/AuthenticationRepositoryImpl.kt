package net.thechance.mena.identity.data.repository

import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.thechance.mena.identity.data.dto.auth.response.AuthenticationResponse
import net.thechance.mena.identity.data.dto.auth.request.LoginRequestDto
import net.thechance.mena.identity.data.dto.auth.request.RefreshRequestDto
import net.thechance.mena.identity.data.dataSource.local.setting.accessToken
import net.thechance.mena.identity.data.utils.postJson
import net.thechance.mena.identity.data.dataSource.local.setting.refreshToken
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.repository.AuthenticationRepository

class AuthenticationRepositoryImpl(
    private val client: HttpClient,
    private val settings: Settings
) : AuthenticationRepository {

    private val observableToken: MutableStateFlow<String> = MutableStateFlow(settings.accessToken)

    override suspend fun login(phoneNumber: PhoneNumber, password: String) {
        return safeWrapper {
            val loginResponse: AuthenticationResponse = client.postJson(
                LoginRequestDto(phoneNumber.getFormattedPhoneNumber(), password),
                LOGIN_ENDPOINT
            )
            saveAuthTokens(loginResponse)
        }
    }

    override suspend fun refreshAccessToken(): String {
        val refreshResponse: AuthenticationResponse = safeWrapper {
            client.postJson(RefreshRequestDto(settings.refreshToken), REFRESH_ENDPOINT)
        }
        saveAuthTokens(refreshResponse)
        return settings.accessToken
    }

    override suspend fun getAccessToken(): String {
        return settings.accessToken
    }

    private suspend fun saveAuthTokens(authInfo: AuthenticationResponse) {
        settings.accessToken = authInfo.accessToken.also { observableToken.emit(it) }
        settings.refreshToken = authInfo.refreshToken
    }

    override fun observeTokenChange(): StateFlow<String>{
        return observableToken
    }

    companion object {
        const val LOGIN_ENDPOINT = "identity/authentication/login"
        const val REFRESH_ENDPOINT = "identity/authentication/refresh"
    }
}