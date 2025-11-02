package net.thechance.mena.admin_panel.data.service

import com.russhwolf.settings.Settings
import net.thechance.mena.admin_panel.data.remote.dto.authentication.AdminAuthenticationResponse
import net.thechance.mena.admin_panel.data.remote.dto.authentication.RefreshTokenRequestDto
import net.thechance.mena.admin_panel.data.remote.api_service.AdminAuthenticationApiService
import net.thechance.mena.admin_panel.data.utils.accessToken
import net.thechance.mena.admin_panel.data.utils.executeApiSafely
import net.thechance.mena.admin_panel.data.utils.refreshToken
import org.koin.core.annotation.Single

@Single
class AuthenticationService(
    private val adminAuthenticationApiService: AdminAuthenticationApiService,
    private val settings: Settings
) {
    suspend fun refreshAccessToken(): String {
        val refreshResponse: AdminAuthenticationResponse =
            executeApiSafely<AdminAuthenticationResponse> {
                adminAuthenticationApiService.refreshAccessToken(
                    RefreshTokenRequestDto(settings.refreshToken)
                )
            }
        saveAuthTokens(authenticationInfo = refreshResponse)
        return settings.accessToken
    }

    suspend fun getAccessToken(): String {
        return settings.accessToken
    }

    private fun saveAuthTokens(authenticationInfo: AdminAuthenticationResponse) {
        settings.accessToken = authenticationInfo.accessToken
        settings.refreshToken = authenticationInfo.refreshToken
    }
}