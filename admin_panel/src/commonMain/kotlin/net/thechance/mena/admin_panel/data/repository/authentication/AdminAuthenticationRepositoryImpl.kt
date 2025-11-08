package net.thechance.mena.admin_panel.data.repository.authentication

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.thechance.mena.admin_panel.data.remote.api_service.AuthenticationApiService
import net.thechance.mena.admin_panel.data.remote.dto.authentication.AdminAuthenticationResponse
import net.thechance.mena.admin_panel.data.remote.dto.authentication.LoginRequestDto
import net.thechance.mena.admin_panel.data.utils.accessToken
import net.thechance.mena.admin_panel.data.utils.executeApiSafely
import net.thechance.mena.admin_panel.data.utils.putAccessToken
import net.thechance.mena.admin_panel.data.utils.putRefreshToken
import net.thechance.mena.admin_panel.domain.repository.authentication.AdminAuthenticationRepository
import org.koin.core.annotation.Single

@OptIn(ExperimentalSettingsApi::class)
@Single
class AdminAuthenticationRepositoryImpl(
    private val authenticationApiService: AuthenticationApiService,
    private val settings: FlowSettings
) : AdminAuthenticationRepository {

    override suspend fun login(userName: String, password: String) {
        val loginResponse: AdminAuthenticationResponse =
            executeApiSafely<AdminAuthenticationResponse> {
                authenticationApiService.login(
                    LoginRequestDto(userName = userName, password = password)
                )
            }
        saveAuthTokens(authenticationInfo = loginResponse)
    }

    override suspend fun logout() {
        executeApiSafely<Unit> {
            authenticationApiService.logout()
        }
        clearAuthTokens()
    }

    override fun isUserLoggedIn(): Flow<Boolean> =
        settings.accessToken.map { token -> token.isNotBlank() }

    private suspend fun saveAuthTokens(authenticationInfo: AdminAuthenticationResponse) {
        settings.putAccessToken(authenticationInfo.accessToken)
        settings.putRefreshToken(authenticationInfo.refreshToken)
    }

    private suspend fun clearAuthTokens() {
        settings.putAccessToken("")
        settings.putRefreshToken("")
    }
}