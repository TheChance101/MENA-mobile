package net.thechance.mena.admin_panel.data.remote.api_service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import net.thechance.mena.admin_panel.data.remote.dto.PagedResponse
import net.thechance.mena.admin_panel.data.remote.dto.user.UpdateUserStatusRequestDto
import net.thechance.mena.admin_panel.data.remote.dto.user.UserResponse

interface UserApiService {
    @GET(USER_BASE)
    suspend fun getUsers(
        @Query("query") query: String? = null,
        @Query("sort") sort: List<String>? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Response<PagedResponse<UserResponse>>

    @PATCH(UPDATE_USER_STATUS_ENDPOINT)
    suspend fun updateUserStatus(
        @Path("userId") userId: String,
        @Body status: UpdateUserStatusRequestDto
    ): Response<Unit>

    private companion object {
        const val USER_BASE = "identity/admin/users"
        const val UPDATE_USER_STATUS_ENDPOINT = "${USER_BASE}/{userId}/status"
    }
}