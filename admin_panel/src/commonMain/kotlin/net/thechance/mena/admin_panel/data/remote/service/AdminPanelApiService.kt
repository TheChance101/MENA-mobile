package net.thechance.mena.admin_panel.data.remote.service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import net.thechance.mena.admin_panel.data.remote.dto.PagedResponse
import net.thechance.mena.admin_panel.data.remote.dto.authentication.AdminAuthenticationResponse
import net.thechance.mena.admin_panel.data.remote.dto.authentication.LoginRequestDto
import net.thechance.mena.admin_panel.data.remote.dto.authentication.RefreshTokenRequestDto
import net.thechance.mena.admin_panel.data.remote.dto.user.UserResponse
import net.thechance.mena.admin_panel.domain.entity.user.Status

interface AdminPanelApiService {

    @POST(LOGIN_ENDPOINT)
    suspend fun login(@Body loginRequest: LoginRequestDto):
            Response<AdminAuthenticationResponse>

    @POST(REFRESH_ENDPOINT)
    suspend fun refreshAccessToken(@Body refreshTokenRequest: RefreshTokenRequestDto):
            Response<AdminAuthenticationResponse>

    @GET(USER_BASE)
    suspend fun getUsers(
        @Query("query") query: String? = null,
        @Query("sort") sort: String? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Response<PagedResponse<UserResponse>>

    @PATCH(UPDATE_USER_STATUS_ENDPOINT)
    suspend fun updateUserStatus(
        @Path("userId") userId: String ,
        @Body status: String): Response<Unit>

    private companion object {
        const val AUTH_BASE = "identity/admin/authentication/"
        const val LOGIN_ENDPOINT = "${AUTH_BASE}login"
        const val REFRESH_ENDPOINT = "${AUTH_BASE}refresh"

        const val USER_BASE = "identity/admin/users"

        const val UPDATE_USER_STATUS_ENDPOINT = "${USER_BASE}/{userId}/status"

    }
}