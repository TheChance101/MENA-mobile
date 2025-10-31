package net.thechance.mena.admin_panel.data.remote.service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import net.thechance.mena.admin_panel.data.remote.dto.PagedResponse
import net.thechance.mena.admin_panel.data.remote.dto.authentication.AdminAuthenticationResponse
import net.thechance.mena.admin_panel.data.remote.dto.authentication.LoginRequestDto
import net.thechance.mena.admin_panel.data.remote.dto.authentication.RefreshTokenRequestDto
import net.thechance.mena.admin_panel.data.remote.dto.user.UserResponse

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

    @POST(BLOCK_USER_ENDPOINT)
    suspend fun blockUser(@Body userID: Long)

    @POST(ACTIVE_USER_ENDPOINT)
    suspend fun activeUser(@Body userID: Long)


    private companion object {
        const val AUTH_BASE = "identity/admin/authentication/"
        const val LOGIN_ENDPOINT = "${AUTH_BASE}login"
        const val REFRESH_ENDPOINT = "${AUTH_BASE}refresh"

        const val USER_BASE = "identity/admin/users"

        const val BLOCK_USER_ENDPOINT = "${USER_BASE}/USER_ID/block"

        const val ACTIVE_USER_ENDPOINT = "${USER_BASE}/USER_ID/activate"
    }
}