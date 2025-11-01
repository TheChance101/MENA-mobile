package net.thechance.mena.admin_panel.data.remote.service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import net.thechance.mena.admin_panel.data.remote.dto.authentication.AdminAuthenticationResponse
import net.thechance.mena.admin_panel.data.remote.dto.authentication.LoginRequestDto
import net.thechance.mena.admin_panel.data.remote.dto.authentication.RefreshTokenRequestDto

interface AdminPanelApiService {
    @POST(LOGIN_ENDPOINT)
    suspend fun login(@Body loginRequest: LoginRequestDto):
            Response<AdminAuthenticationResponse>

    @POST(REFRESH_ENDPOINT)
    suspend fun refreshAccessToken(@Body refreshTokenRequest: RefreshTokenRequestDto):
            Response<AdminAuthenticationResponse>

    private companion object {
        const val AUTH_BASE = "identity/admin/authentication/"
        const val LOGIN_ENDPOINT = "${AUTH_BASE}login"
        const val REFRESH_ENDPOINT = "${AUTH_BASE}refresh"
    }
}