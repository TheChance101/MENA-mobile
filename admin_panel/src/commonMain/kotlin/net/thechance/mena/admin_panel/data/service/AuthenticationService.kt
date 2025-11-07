package net.thechance.mena.admin_panel.data.service

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.first
import net.thechance.mena.admin_panel.data.remote.dto.authentication.AdminAuthenticationResponse
import net.thechance.mena.admin_panel.data.remote.dto.authentication.RefreshTokenRequestDto
import net.thechance.mena.admin_panel.data.remote.api_service.AuthenticationApiService
import net.thechance.mena.admin_panel.data.utils.accessToken
import net.thechance.mena.admin_panel.data.utils.executeApiSafely
import net.thechance.mena.admin_panel.data.utils.putAccessToken
import net.thechance.mena.admin_panel.data.utils.putRefreshToken
import net.thechance.mena.admin_panel.data.utils.refreshToken
import net.thechance.mena.admin_panel.domain.exceptions.UnauthorizedException
import org.koin.core.annotation.Single

@OptIn(ExperimentalSettingsApi::class)
@Single
class AuthenticationService(
    private val authenticationApiService: AuthenticationApiService,
    private val settings: FlowSettings
) {
    suspend fun refreshAccessToken(): String {
        val refreshResponse: AdminAuthenticationResponse =
            try {
                executeApiSafely<AdminAuthenticationResponse> {
                    authenticationApiService.refreshAccessToken(
                        RefreshTokenRequestDto(settings.refreshToken.first())
                    )
                }
            }catch (_: UnauthorizedException){
                AdminAuthenticationResponse("", "")
            }
        saveAuthTokens(authenticationInfo = refreshResponse)
        return settings.accessToken.first()
    }

    suspend fun getAccessToken(): String {
        return settings.accessToken.first()
    }

    private suspend fun saveAuthTokens(authenticationInfo: AdminAuthenticationResponse) {
        settings.putAccessToken(authenticationInfo.accessToken)
        settings.putRefreshToken(authenticationInfo.refreshToken)
    }
}