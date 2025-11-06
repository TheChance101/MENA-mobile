package net.thechance.mena.admin_panel.data.service

import com.russhwolf.settings.Settings
import net.thechance.mena.admin_panel.data.remote.dto.authentication.AdminAuthenticationResponse
import net.thechance.mena.admin_panel.data.remote.dto.authentication.RefreshTokenRequestDto
import net.thechance.mena.admin_panel.data.remote.api_service.AuthenticationApiService
import net.thechance.mena.admin_panel.data.utils.accessToken
import net.thechance.mena.admin_panel.data.utils.executeApiSafely
import net.thechance.mena.admin_panel.data.utils.observableToken
import net.thechance.mena.admin_panel.data.utils.refreshToken
import net.thechance.mena.admin_panel.domain.exceptions.UnauthorizedException
import org.koin.core.annotation.Single

@Single
class AuthenticationService(
    private val authenticationApiService: AuthenticationApiService,
    private val settings: Settings
) {
    suspend fun refreshAccessToken(): String {
        val refreshResponse: AdminAuthenticationResponse =
            try {
                executeApiSafely<AdminAuthenticationResponse> {
                    authenticationApiService.refreshAccessToken(
                        RefreshTokenRequestDto(settings.refreshToken)
                    )
                }
            }catch (_: UnauthorizedException){
                AdminAuthenticationResponse("", "")
            }
        saveAuthTokens(authenticationInfo = refreshResponse)
        return settings.accessToken
    }

    suspend fun getAccessToken(): String {
        return settings.accessToken
    }

    private suspend fun saveAuthTokens(authenticationInfo: AdminAuthenticationResponse) {
        settings.accessToken = authenticationInfo.accessToken.also { observableToken.emit(it) }
        settings.refreshToken = authenticationInfo.refreshToken
    }
}