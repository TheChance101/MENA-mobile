package net.thechance.mena.identity.data.repository

import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import net.thechance.mena.identity.data.dataSource.local.database.dao.UserDao
import net.thechance.mena.identity.data.dataSource.local.setting.accessToken
import net.thechance.mena.identity.data.dataSource.local.setting.imageUploadCompleted
import net.thechance.mena.identity.data.dataSource.local.setting.lastRegistrationPhoneNumber
import net.thechance.mena.identity.data.dataSource.local.setting.refreshToken
import net.thechance.mena.identity.data.dto.auth.request.LoginRequestDto
import net.thechance.mena.identity.data.dto.auth.request.RefreshRequestDto
import net.thechance.mena.identity.data.dto.auth.response.AuthenticationResponse
import net.thechance.mena.identity.data.mapper.toDomain
import net.thechance.mena.identity.data.utils.invalidateAuthTokens
import net.thechance.mena.identity.data.utils.postEmpty
import net.thechance.mena.identity.data.utils.postJson
import net.thechance.mena.identity.data.utils.safeWrapper
import net.thechance.mena.identity.domain.entity.PhoneNumber
import net.thechance.mena.identity.domain.model.AuthenticationTokens
import net.thechance.mena.identity.domain.repository.AuthenticationRepository
import kotlin.concurrent.Volatile

class AuthenticationRepositoryImpl(
    private val client: HttpClient,
    private val settings: Settings,
    private val userDao: UserDao? = null
) : AuthenticationRepository {

    private val observableToken: MutableStateFlow<String> = MutableStateFlow(
        getInitialToken()
    )

    @Volatile
    private var isTemporaryTokenMode: Boolean = false

    private fun getInitialToken(): String =
        if (shouldInitializeWithToken()) settings.accessToken else ""

    private fun shouldInitializeWithToken(): Boolean {
        val lastPhoneNumber = settings.lastRegistrationPhoneNumber
        val imageUploadCompleted = settings.imageUploadCompleted

        return lastPhoneNumber.isBlank() || imageUploadCompleted
    }

    override suspend fun login(phoneNumber: PhoneNumber, password: String) = safeWrapper {
        val response: AuthenticationResponse = client.postJson(
            LoginRequestDto(phoneNumber.getFormattedPhoneNumber(), password),
            LOGIN_ENDPOINT
        )
        saveAuthTokens(response.toDomain())
        try {
            client.invalidateAuthTokens()
        } catch (_: Exception) {
        }
    }

    override suspend fun logout() {
        safeWrapper {
            client.postEmpty(LOGOUT_ENDPOINT)
        }
        try {
            client.invalidateAuthTokens()
        } catch (_: Exception) {
        }
        if (userDao != null) {
            withContext(Dispatchers.IO) {
                try {
                    userDao.deleteUser()
                } catch (_: Exception) {
                }
            }
        }
        clearAuthTokens()
    }

    override suspend fun refreshAccessToken(): String {
        val response: AuthenticationResponse = safeWrapper {
            client.postJson(
                RefreshRequestDto(settings.refreshToken),
                REFRESH_ENDPOINT
            )
        }
        saveTokens(response.toDomain(), shouldEmit = !isTemporaryTokenMode)
        try {
            client.invalidateAuthTokens()
        } catch (_: Exception) {
        }
        return settings.accessToken
    }

    override suspend fun getAccessToken(): String = settings.accessToken

    override suspend fun getAuthTokens(): AuthenticationTokens? =
        createAuthTokensIfValid(settings.accessToken, settings.refreshToken)

    private fun createAuthTokensIfValid(
        accessToken: String,
        refreshToken: String
    ): AuthenticationTokens? =
        AuthenticationTokens(accessToken, refreshToken).takeIf {
            accessToken.isNotBlank() && refreshToken.isNotBlank()
        }

    override suspend fun saveAuthTokensWithoutEmit(authTokens: AuthenticationTokens) {
        isTemporaryTokenMode = true
        saveTokensToSettings(authTokens)
    }

    override suspend fun saveAuthTokensAndEmit(authTokens: AuthenticationTokens) {
        isTemporaryTokenMode = false
        saveAuthTokens(authTokens)
    }

    override suspend fun clearAuthTokens() {
        saveTokensToSettings(createEmptyTokens())
        isTemporaryTokenMode = false
        emitToken("")
    }

    override fun observeTokenChange(): StateFlow<String> = observableToken

    private suspend fun saveAuthTokens(authTokens: AuthenticationTokens) {
        saveTokens(authTokens, shouldEmit = !isTemporaryTokenMode)
    }

    private suspend fun saveTokens(authTokens: AuthenticationTokens, shouldEmit: Boolean) {
        saveTokensToSettings(authTokens)
        if (shouldEmit) {
            emitToken(authTokens.accessToken)
        }
    }

    private suspend fun emitToken(token: String) {
        observableToken.emit(token)
    }

    private fun saveTokensToSettings(authTokens: AuthenticationTokens) {
        settings.accessToken = authTokens.accessToken
        settings.refreshToken = authTokens.refreshToken
    }

    private fun createEmptyTokens() = AuthenticationTokens(
        accessToken = "",
        refreshToken = ""
    )

    companion object {
        const val LOGIN_ENDPOINT = "identity/authentication/login"
        const val REFRESH_ENDPOINT = "identity/authentication/refresh"
        const val LOGOUT_ENDPOINT = "identity/authentication/logout"
    }
}