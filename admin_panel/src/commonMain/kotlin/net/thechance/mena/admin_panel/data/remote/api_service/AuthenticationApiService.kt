package net.thechance.mena.admin_panel.data.remote.api_service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import net.thechance.mena.admin_panel.data.remote.dto.authentication.AdminAuthenticationResponse
import net.thechance.mena.admin_panel.data.remote.dto.authentication.LoginRequestDto
import net.thechance.mena.admin_panel.data.remote.dto.authentication.RefreshTokenRequestDto

interface AuthenticationApiService {
    @POST(LOGIN_ENDPOINT)
    suspend fun login(@Body loginRequest: LoginRequestDto):
            Response<AdminAuthenticationResponse>

    @POST(REFRESH_ENDPOINT)
    suspend fun refreshAccessToken(@Body refreshTokenRequest: RefreshTokenRequestDto):
            Response<AdminAuthenticationResponse>

    @POST(LOGOUT_ENDPOINT)
    suspend fun logout(): Response<Unit>

    private companion object {
        const val AUTH_BASE = "identity/admin/authentication/"
        const val LOGIN_ENDPOINT = "${AUTH_BASE}login"
        const val REFRESH_ENDPOINT = "${AUTH_BASE}refresh"
        const val LOGOUT_ENDPOINT = "${AUTH_BASE}logout"
    }
}