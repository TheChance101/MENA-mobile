package net.thechance.mena.admin_panel.data.repository.authentication

import com.russhwolf.settings.Settings
import net.thechance.mena.admin_panel.data.remote.dto.authentication.AdminAuthenticationResponse
import net.thechance.mena.admin_panel.data.remote.dto.authentication.LoginRequestDto
import net.thechance.mena.admin_panel.data.remote.api_service.AuthenticationApiService
import net.thechance.mena.admin_panel.data.utils.accessToken
import net.thechance.mena.admin_panel.data.utils.executeApiSafely
import net.thechance.mena.admin_panel.data.utils.refreshToken
import net.thechance.mena.admin_panel.domain.repository.authentication.AdminAuthenticationRepository
import org.koin.core.annotation.Single

@Single
class AdminAuthenticationRepositoryImpl(
    private val authenticationApiService: AuthenticationApiService,
    private val settings: Settings
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

    private fun saveAuthTokens(authenticationInfo: AdminAuthenticationResponse) {
        settings.accessToken = authenticationInfo.accessToken
        settings.refreshToken = authenticationInfo.refreshToken
    }

    private fun clearAuthTokens() {
        settings.accessToken = ""
        settings.refreshToken = ""
    }
}