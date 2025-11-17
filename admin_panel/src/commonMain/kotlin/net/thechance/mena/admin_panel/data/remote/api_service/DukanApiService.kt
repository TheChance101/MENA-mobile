package net.thechance.mena.admin_panel.data.remote.api_service

import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import net.thechance.mena.admin_panel.data.remote.dto.DukanPagedResponse
import net.thechance.mena.admin_panel.data.remote.dto.dukan.DukanDeactivationDto
import net.thechance.mena.admin_panel.data.remote.dto.dukan.DukanDto
import net.thechance.mena.admin_panel.data.remote.dto.dukan.ProductDto
import net.thechance.mena.admin_panel.data.remote.dto.dukan.ShelfDto
import net.thechance.mena.admin_panel.data.remote.dto.dukan.UpdateDukanStatusRequestDto

interface DukanApiService {
    @GET(DUKAN_ADMIN_BASE)
    suspend fun getDukans(
        @Query("status") status: String,
        @Query("query") query: String? = null,
        @Query("sort") sort: String? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Response<DukanPagedResponse<DukanDto>>

    @GET(DUKAN_SHELVES_ENDPOINT)
    suspend fun getDukanShelves(
        @Path("dukanId") dukanId: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Response<DukanPagedResponse<ShelfDto>>

    @GET(SHELF_PRODUCTS_ENDPOINT)
    suspend fun getShelfProducts(
        @Path("shelfId") shelfId: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): Response<DukanPagedResponse<ProductDto>>

    @PATCH(UPDATE_DUKAN_STATUS_ENDPOINT)
    suspend fun updateDukanStatus(
        @Path("dukanId") dukanId: String,
        @Body request: UpdateDukanStatusRequestDto
    ): Response<Unit>

    @PATCH(DUKAN_ACTIVATION_ENDPOINT)
    suspend fun activateDukan(
        @Path("dukanId") dukanId: String
    ): Response<Unit>

    @PATCH(DUKAN_DEACTIVATION_ENDPOINT)
    suspend fun deactivateDukan(
        @Path("dukanId") dukanId: String,
        @Body deactivateReason: DukanDeactivationDto
    ): Response<Unit>

    private companion object {
        const val DUKAN_ADMIN_BASE = "dukan/admin"
        const val DUKAN_SHELVES_ENDPOINT = "$DUKAN_ADMIN_BASE/shelf/{dukanId}"
        const val SHELF_PRODUCTS_ENDPOINT = "$DUKAN_ADMIN_BASE/shelf/{shelfId}/products"
        const val DUKAN_ACTIVATION_ENDPOINT = "$DUKAN_ADMIN_BASE/{dukanId}/activate"
        const val DUKAN_DEACTIVATION_ENDPOINT = "$DUKAN_ADMIN_BASE/{dukanId}/deactivate"
        const val UPDATE_DUKAN_STATUS_ENDPOINT = "$DUKAN_ADMIN_BASE/{dukanId}/status"
    }
}